package site.cspy.core.i18n;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;


/**
 * 预设国际化项，包含了以下元素：
 * 1. 唯一键
 * 2. 参数数组
 *
 * <p>
 * 构造后不可变，如果需要改参数，则返回新实例。
 * </p>
 */
@Validated
public class I18nItem implements MessageSourceResolvable {
    /**
     * 唯一键
     */
    @Getter
    protected final String code;

    /**
     * 参数
     */
    protected final Object[] args;

    public I18nItem(@NotEmpty String code, @NonNull Object... args) {
        this.code = StringUtils.hasText(code) ? code : "";
        this.args = Optional.ofNullable(args).orElse(new Object[0]);
    }

    /**
     * 快速构造
     *
     * @param code 唯一件
     * @param args 参数
     * @return 实例
     */
    public static I18nItem t(String code, Object... args) {
        return new I18nItem(code, args);
    }

    /**
     * 追加参数
     *
     * @param args 参数
     * @return 新实例
     */
    public I18nItem withArgs(Object... args) {
        return new I18nItem(code, args);
    }


    @Override
    public String toString() {
        return "[%s]".formatted(code);
    }

    @Override
    public String[] getCodes() {
        return new String[]{code};
    }

    @Override
    public Object[] getArguments() {
        return args;
    }

    @Override
    public String getDefaultMessage() {
        return this.toString();
    }
}
