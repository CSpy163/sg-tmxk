package site.cspy.core.cache;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于统一管理 Token 缓存的持有类。
 *
 * <p>此类提供懒加载Token的缓存模式。</p>
 * <ul>
 *     <li>懒加载Token：缓存数据过期后，根据配置的加载器在下次请求时重新加载。</li>
 * </ul>
 */
@Slf4j
@Component
public class SgTokenHolder implements ApplicationContextAware {
    private static final String TOKEN_NAME = "SG_TOKEN_HOLDER_KEY";

    /**
     * 存放不同Token的异步缓存Map。
     */
    private static final Map<String, AsyncCache<String, Object>> cacheMap = new ConcurrentHashMap<>();

    /**
     * 存放懒加载模式的Token加载器。
     */
    private static final Map<String, SgAsyncCache<?>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 注册懒加载的Token缓存。
     *
     * @param tokenName  Token 名称（唯一标识）
     * @param expireTime 过期时间
     * @param fetcher    加载器，用于在Token过期后重新加载
     */
    public static void registerLazy(@NonNull String tokenName, @NonNull Duration expireTime, @NonNull SgAsyncCache<?> fetcher) {
        AsyncCache<String, Object> cache = Caffeine.newBuilder()
                .expireAfterWrite(expireTime)  // 设置懒加载的过期时间
                .buildAsync();

        cacheMap.put(tokenName, cache);
        loaderMap.put(tokenName, fetcher);
    }

    /**
     * 获取指定Token的值。
     *
     * <p>如果Token是懒加载模式，则在Token失效后调用加载逻辑重新加载数据。</p>
     *
     * @param tokenName Token 名称
     * @return 包含Token值的Mono对象
     */
    public static Mono<Object> getToken(String tokenName) {
        AsyncCache<String, Object> asyncCache = cacheMap.get(tokenName);
        if (asyncCache == null) {
            log.info("Token: " + tokenName + " not found.");
            return Mono.empty();
        }

        // 从缓存加载数据，若未找到则调用加载逻辑
        return Mono.fromFuture(asyncCache.get(TOKEN_NAME, k -> {
            SgAsyncCache<?> loader = loaderMap.get(tokenName);
            if (loader != null) {
                return loader.load().toFuture();
            } else {
                log.warn("加载器未找到，Token名称：「{}」", tokenName);
                return CompletableFuture.completedFuture(null); // 返回空值，避免错误
            }
        })).filter(Objects::nonNull); // 过滤掉null值
    }


    /**
     * 获取指定Token的值，并尝试转换为指定类型。
     *
     * @param tokenName    Token 名称
     * @param defaultValue 默认值
     * @param <T>          值类型
     * @return Token Value
     */
    public static <T> Mono<T> getToken(String tokenName, T defaultValue) {
        AsyncCache<String, Object> asyncCache = cacheMap.get(tokenName);
        if (asyncCache == null) {
            log.info("Token: " + tokenName + " not found.");
            return Mono.just(defaultValue); // 返回默认值
        }

        // 从缓存加载数据，若未找到则调用加载逻辑
        return Mono.fromFuture(asyncCache.get(TOKEN_NAME, k -> {
                    SgAsyncCache<?> loader = loaderMap.get(tokenName);
                    if (loader != null) {
                        return loader.load().toFuture();
                    } else {
                        log.warn("加载器未找到，Token名称：「{}」", tokenName);
                        return CompletableFuture.completedFuture(null); // 返回空值，避免错误
                    }
                }))
                .map(data -> {
                    try {
                        // 尝试转换类型
                        return (T) data;
                    } catch (ClassCastException e) {
                        log.error("类型转换错误，Token名称：「{}」，错误：「{}」", tokenName, e.getMessage());
                        return defaultValue; // 转换失败时返回默认值
                    }
                })
                .defaultIfEmpty(defaultValue); // 若值为null，使用默认值
    }

    private static ApplicationContext context;

    /**
     * 设置ApplicationContext。
     *
     * @param applicationContext Spring的应用上下文
     * @throws BeansException 如果在设置上下文时发生错误
     */
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        SgTokenHolder.context = applicationContext;
    }
}
