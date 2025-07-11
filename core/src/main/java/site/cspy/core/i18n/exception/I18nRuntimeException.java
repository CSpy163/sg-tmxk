package site.cspy.core.i18n.exception;

import lombok.NonNull;
import site.cspy.core.i18n.I18nItem;
import site.cspy.core.i18n.I18nManager;

/**
 * 国际化错误
 */
public class I18nRuntimeException extends RuntimeException {
    I18nItem item;

    public I18nRuntimeException(@NonNull I18nItem item) {
        super(I18nManager.toI18nMessage(item));
        this.item = item;
    }

    public I18nItem getItem() {
        return item;
    }
}
