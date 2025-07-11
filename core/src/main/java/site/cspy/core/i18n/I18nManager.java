package site.cspy.core.i18n;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Locale;

import static site.cspy.core.i18n.I18nItem.t;


@Slf4j
@Component
public class I18nManager implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static MessageSource messageSource;

    private static Locale defaultLocale;

    public static String toI18nMessage(I18nItem item) {
        return toI18nMessage(item, defaultLocale);
    }

    public static String toI18nMessage(I18nItem item, Locale locale) {
        return messageSource.getMessage(item, locale);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        I18nManager.applicationContext = applicationContext;
        messageSource = applicationContext.getBean(MessageSource.class);
        String localeStr = applicationContext.getEnvironment().getProperty("core-starter.default-locale", "zh_CN");
        if (StringUtils.hasText(localeStr)) {
            defaultLocale = LocaleUtils.toLocale(localeStr);
            Locale.setDefault(defaultLocale);
        } else {
            defaultLocale = Locale.getDefault();
        }
        log.info(messageSource.getMessage(t("INFO.DEFAULT_LOCALE"), defaultLocale));
    }

}
