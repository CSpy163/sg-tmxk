package site.cspy.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import site.cspy.service.domain.WxEncryptedMsgBody;
import site.cspy.service.domain.WxSignatureQuery;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class WxCrypto {

    @Value("${tmxk.wechat.token}")
    private String token;

    @Value("${tmxk.wechat.aes-key}")
    private String aesKey;

    /**
     * 校验微信签名
     *
     * @param query 微信请求参数
     * @return 签名是否正确
     */
    public boolean verifySignature(@NonNull WxSignatureQuery query) {
        String needSha1Str = Stream.of(query.getTimestamp(), query.getNonce(), token).sorted().collect(Collectors.joining(""));
        String mySignature = Hashing.sha1().hashString(needSha1Str, StandardCharsets.US_ASCII).toString();
        return Objects.equals(mySignature, query.getSignature());
    }

    public static final ObjectMapper XML_OM = new XmlMapper();

    public String decryptMsg(String encryptedMsgXmlBody) throws Exception {
        WxEncryptedMsgBody msgBody = XML_OM.readValue(encryptedMsgXmlBody, WxEncryptedMsgBody.class);
        String decrypt = decrypt(aesKey, msgBody.getEncrypt(), null);
        log.info("解密成功！\n{}", decrypt);
        return decrypt;
    }

    public static String decrypt(String encodingAESKeyBase64, String encryptBase64, String expectedAppId) throws Exception {
        byte[] aesKey = Base64.getDecoder().decode(encodingAESKeyBase64 + "=");
        byte[] iv = Arrays.copyOfRange(aesKey, 0, 16);
        byte[] decodedEncrypt = Base64.getDecoder().decode(encryptBase64);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKey, "AES"), new IvParameterSpec(iv));
        byte[] fullMsg = cipher.doFinal(decodedEncrypt);

        ByteBuffer buffer = ByteBuffer.wrap(fullMsg);
        buffer.position(16);
        int msgLen = buffer.getInt();
        byte[] msgBytes = new byte[msgLen];
        buffer.get(msgBytes);
        byte[] appIdBytes = new byte[buffer.remaining()];
        buffer.get(appIdBytes);

        String msg = new String(msgBytes, StandardCharsets.UTF_8);
        String appId = new String(appIdBytes, StandardCharsets.UTF_8);
//        if (!appId.equals(expectedAppId)) {
//            throw new IllegalArgumentException("AppID 不匹配");
//        }
        return msg;
    }



}
