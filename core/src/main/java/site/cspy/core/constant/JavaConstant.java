package site.cspy.core.constant;

import java.time.format.DateTimeFormatter;

/**
 * Java 常量
 */
public class JavaConstant {
    /**
     * 日期时间格式化器
     */
    public static final DateTimeFormatter DATE_TIME_ZN_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
    public static final DateTimeFormatter FS_DATE_TIME_ZN_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DATE_TIME_ZN_FORMATTER;
    public static final DateTimeFormatter DATE_TIME_DMY_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy");
    public static final DateTimeFormatter DATE_TIME_DMYHM_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy HH:mm");
}
