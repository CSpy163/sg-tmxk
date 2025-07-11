package site.cspy.core.route;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Locale;

/**
 * 多站点工具类
 */
@Component
public class MultipleWebSiteHelper implements ApplicationContextAware {
    /**
     * 系统默认语言
     */
    static Locale DEFAULT_LOCALE = Locale.getDefault();

    /**
     * 获取当前站点信息
     *
     * @return 站点信息
     */
    public static Mono<WebSiteInfo> getWebSiteInfo() {
        return Mono.deferContextual(ctx -> {
            WebSiteInfo info = ctx.get("WebSiteInfo");
            return Mono.just(info);
        });
    }

    /**
     * 根据上下文获取请求用户的语言
     * @param exchange
     * @return
     */
    public static Locale getUserLocale(ServerWebExchange exchange) {
        if (exchange != null) {
            // 获取 Accept-Language 头信息
            List<String> acceptLanguageHeaders = exchange.getRequest().getHeaders().get("Accept-Language");
            if (acceptLanguageHeaders != null && !acceptLanguageHeaders.isEmpty()) {
                // 解析第一个 Accept-Language 头
                Locale locale = Locale.forLanguageTag(acceptLanguageHeaders.get(0));
                return locale;
            }
        }

        // 默认返回系统 locale
        return DEFAULT_LOCALE;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        String localeString = applicationContext.getEnvironment().getProperty("core-starter.default.locale");
        if (StringUtils.hasText(localeString)) {
            DEFAULT_LOCALE = Locale.forLanguageTag(localeString);
        }

    }
}
