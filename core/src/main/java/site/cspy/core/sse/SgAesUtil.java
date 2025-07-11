package site.cspy.core.sse;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
public class SgAesUtil {

    private static final String ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 128;
    private static final int IV_SIZE = 16;

    private static final String CHAR_POOL = "abcdefghijklmnopqrstuvwxyz"       // 小写字母
            + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"       // 大写字母
            + "0123456789"                       // 数字
            + "-_";                              // URL安全的特殊字符

    /**
     * 生成AES密钥
     * @return Base64编码的密钥字符串
     */
    public static String generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(KEY_SIZE);
            SecretKey key = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate AES key", e);
        }
    }

    /**
     * 生成指定长度的随机密钥
     * @param keyLength 密钥长度
     * @return 生成的密钥
     */
    public static String generateKey(int keyLength) {
        SecureRandom random = new SecureRandom();
        StringBuilder key = new StringBuilder(keyLength);

        // 每次随机从字符池中抽取一个字符
        for (int i = 0; i < keyLength; i++) {
            int randomIndex = random.nextInt(CHAR_POOL.length());
            key.append(CHAR_POOL.charAt(randomIndex));
        }

        return key.toString();
    }

    /**
     * 生成默认的16字节AES密钥
     */
    public static String defaultAesKey() {
        return generateKey(16);
    }

    /**
     * 生成UUID
     */
    public static String uuid() {
        return java.util.UUID.randomUUID().toString();
    }

    /**
     * AES加密
     * @param plaintext 明文
     * @param key Base64编码的密钥
     * @return Base64编码的密文（包含IV）
     */
    public static String encrypt(String plaintext, byte[] key) {
        try {
            log.debug("Encrypting text: {}", plaintext);
            log.debug("Using key: {}", new String(key, StandardCharsets.UTF_8));

            // 生成随机IV
            byte[] iv = new byte[IV_SIZE];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);
            log.debug("Generated IV: {}", Base64.getEncoder().encodeToString(iv));

            // 初始化加密器
            SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            // 加密
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // 拼接IV和加密数据
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            // Base64编码
            String result = Base64.getEncoder().encodeToString(combined);
            log.debug("Encrypted result: {}", result);
            return result;

        } catch (Exception e) {
            log.error("Encryption failed", e);
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * AES解密
     * @param ciphertext Base64编码的密文（包含IV）
     * @param key Base64编码的密钥
     * @return 解密后的明文
     */
    public static String decrypt(String ciphertext, byte[] key) {
        try {
            log.debug("Decrypting text: {}", ciphertext);
            log.debug("Using key: {}", new String(key, StandardCharsets.UTF_8));

            // Base64解码
            byte[] combined = Base64.getDecoder().decode(ciphertext);

            // 提取IV和加密数据
            byte[] iv = new byte[IV_SIZE];
            byte[] encrypted = new byte[combined.length - IV_SIZE];
            System.arraycopy(combined, 0, iv, 0, IV_SIZE);
            System.arraycopy(combined, IV_SIZE, encrypted, 0, encrypted.length);
            log.debug("Extracted IV: {}", Base64.getEncoder().encodeToString(iv));

            // 初始化解密器
            SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            // 解密
            byte[] decrypted = cipher.doFinal(encrypted);
            String result = new String(decrypted, StandardCharsets.UTF_8);
            log.debug("Decrypted result: {}", result);
            return result;

        } catch (Exception e) {
            log.error("Decryption failed", e);
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
