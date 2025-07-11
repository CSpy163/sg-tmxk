package site.cspy.core.persistence;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据库加密字符串解析器，使用时需继承，并实现相关方法。
 * <p><strong>数据库存储格式：ENC(加密数据)</strong></p>
 * <p>加密数据结构：<strong>盐 16 位 + 密钥 sha256 后 4 位  +  AES加密体</strong></p>
 * <p>一般用于单独的重要字段，该字段通常不应与其他表字段产生关联。也不用于 <code>unique Key</code> 场景。</p>
 * <p>即使同一份 <code>data</code> 跟 <code>secret</code> ，因为有了盐的关系，所以会生成不同的加密体。对加密字段进行查询时，建议使用 Cache，通过 Mapper 查询出数据并缓存下来。</p>
 */
@Slf4j
public abstract class EncryptStringHandler implements TypeHandler<String> {

    /**
     * 加密正则
     */
    static final Pattern ENCRYPT_PATTERN = Pattern.compile("^ENC\\((?<salt>\\p{Alnum}{16})(?<hashEnd>[\\p{Alnum}=]{4})(?<aesBody>.*)\\)$");

    /**
     * 加密算法
     */
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * 加解密 cipher
     */
    private final Cipher cipher;

    /**
     * 根据 子类的 EncryptKeys 生成 hash 列表，内容为 128 长度的 16 进制数组。
     */
    private final List<String> hashedKeys;

    /**
     * 构造函数
     *
     * @throws NoSuchPaddingException   异常
     * @throws NoSuchAlgorithmException 异常
     */
    protected EncryptStringHandler() throws NoSuchPaddingException, NoSuchAlgorithmException {
        // 生成随机盐
        cipher = Cipher.getInstance(ALGORITHM);
        hashedKeys = Optional.ofNullable(getEncryptKeys()).orElse(List.of()).stream().map(this::calcKeyHash).toList();
    }

    /**
     * 获取所有密钥（原文），随后在本地进行 hash 计算，生成 256 位的 keyHash，用于后续计算。
     *
     * @return 密钥
     */
    public abstract List<String> getEncryptKeys();

    /**
     * 根据 4 位标识值，获取对应的密钥。
     *
     * @param hashEnd hashEnd
     * @return 密钥
     */
    private String findHashedKeys(String hashEnd) {
        String hashedKey = hashedKeys.stream().filter(key -> key.endsWith(hashEnd)).findFirst().orElse("");
        if (!StringUtils.hasText(hashedKey)) {
            throw new RuntimeException("未找到相应的 HashedKey，特征值：「%s」".formatted(hashEnd));
        }
        return hashedKey;
    }

    /**
     * 随机获取一个密钥，用于首次设置加密值。
     *
     * @return 密钥
     */
    public String getRandomHashedKey() {
        if (CollectionUtils.isEmpty(hashedKeys)) {
            return "";
        }

        Random random = new Random();
        int index = random.nextInt(hashedKeys.size());
        return hashedKeys.get(index);
    }

    /**
     * 获取 hashEnd
     *
     * @param encryptKey 密钥
     * @return hashEnd
     */
    private String calcKeyHash(String encryptKey) {
        if (!StringUtils.hasText(encryptKey)) {
            log.warn("EncryptKey 为空，无法计算 hash 值！");
            return "";
        }
        return BaseEncoding.base16().encode(Hashing.sha256().hashString(encryptKey, StandardCharsets.UTF_8).asBytes());
    }

    /**
     * 获取随机盐
     *
     * @return 盐
     */
    private String getRandomSalt() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase().substring(0, 16);
    }

    /**
     * 加密数据
     *
     * @param hashedKey 密钥
     * @param data      数据
     * @return 加密后的数据
     */
    private String encryptString(String hashedKey, String data) {
        if (StringUtils.hasText(data) && StringUtils.hasText(hashedKey)) {
            try {
                byte[] salt = getRandomSalt().getBytes(StandardCharsets.US_ASCII);
                IvParameterSpec iv = new IvParameterSpec(salt);
                if (StringUtils.hasText(hashedKey)) {
                    SecretKeySpec secretKeySpec = new SecretKeySpec(BaseEncoding.base16().decode(hashedKey), "AES");
                    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
                    byte[] aesBody = Base64.getEncoder().encode(cipher.doFinal(data.getBytes()));
                    String hashEnd = "";
                    if (hashedKey.length() >= 4) {
                        hashEnd = hashedKey.substring(hashedKey.length() - 4);
                    }
                    return String.format("ENC(%s%s%s)", new String(salt, StandardCharsets.US_ASCII), hashEnd, new String(aesBody));

                }
            } catch (Exception e) {
                log.error("数据加密失败！原因：「{}」", e.getMessage());
            }
        }
        return Optional.ofNullable(data).orElse("");
    }

    /**
     * 计算出所有的加密值
     *
     * @param data 数据
     * @return 加密后的数据
     */
    public List<String> generateAllEncryptedValue(String data) {
        return getEncryptKeys().stream().map(key -> encryptString(calcKeyHash(key), data)).toList();
    }

    /**
     * 计算出所有的加密值，包含原始值
     *
     * @param data 数据
     * @return 加密后的数据
     */
    public List<String> generateAllEncryptedValueWithOrigin(String data) {
        ArrayList<String> list = new ArrayList<>(generateAllEncryptedValue(data));
        list.add(data);
        return list;
    }


    /**
     * 解密数据
     *
     * @param data 数据
     * @return 解密后的数据
     */
    private String decryptString(String data) {
        if (StringUtils.hasText(data)) {
            try {
                Matcher matcher = ENCRYPT_PATTERN.matcher(data);
                if (matcher.matches()) {
                    String salt = matcher.group("salt");
                    String hashEnd = matcher.group("hashEnd");
                    String aesBody = matcher.group("aesBody");

                    byte[] keyHash = BaseEncoding.base16().decode(findHashedKeys(hashEnd));
                    if (keyHash.length > 0) {
                        IvParameterSpec iv = new IvParameterSpec(salt.getBytes(StandardCharsets.US_ASCII));
                        SecretKeySpec secretKeySpec = new SecretKeySpec(keyHash, "AES");
                        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
                        return new String(cipher.doFinal(Base64.getDecoder().decode(aesBody)));
                    }
                }
            } catch (Exception e) {
                log.error("数据解密失败！原因：「{}」", e.getMessage());
            }
        }
        return Optional.ofNullable(data).orElse("");
    }


    @Override
    public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, encryptString(getRandomHashedKey(), parameter));
    }

    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        return decryptString(rs.getString(columnName));
    }

    @Override
    public String getResult(ResultSet rs, int columnIndex) throws SQLException {
        return decryptString(rs.getString(columnIndex));
    }

    @Override
    public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return decryptString(cs.getString(columnIndex));
    }
}
