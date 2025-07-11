package site.cspy.core.constant;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Spring 常量
 */
public class SpringConstant {
    /**
     * 通用 Json 序列化器
     */
    public static final ObjectMapper PRETTY_OM = new ObjectMapper();

    static {
        PRETTY_OM.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        PRETTY_OM.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        PRETTY_OM.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        PRETTY_OM.enable(SerializationFeature.INDENT_OUTPUT);
        PRETTY_OM.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        PRETTY_OM.registerModule(new JavaTimeModule());
    }


    /**
     * 通用 Json 序列化器
     */
    public static final ObjectMapper OM = new ObjectMapper();

    static {
        OM.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        OM.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        OM.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        OM.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        OM.registerModule(new JavaTimeModule());
    }

    /**
     * 通用 YAML 序列化器
     */
    public static final YAMLMapper YM = new YAMLMapper();

    static {
        YM.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        YM.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        YM.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        YM.enable(SerializationFeature.INDENT_OUTPUT);
        YM.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        YM.registerModule(new JavaTimeModule());
    }




}
