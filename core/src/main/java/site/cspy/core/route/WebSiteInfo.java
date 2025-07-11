package site.cspy.core.route;

import org.springframework.lang.NonNull;

import java.util.Locale;

/**
 * @param siteName
 * @param locale
 */
public record WebSiteInfo(@NonNull String siteName, @NonNull Locale locale) {

    public String getDomain() {
        return siteName;
    }

    public String getLanguage() {
        return locale.getLanguage();
    }

}

