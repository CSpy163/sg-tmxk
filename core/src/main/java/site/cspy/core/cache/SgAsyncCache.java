package site.cspy.core.cache;

import reactor.core.publisher.Mono;

/**
 * 异步缓存接口，用于管理多种缓存策略
 */
public interface SgAsyncCache<T> {
    /**
     * 刷新数据
     *
     * @return 缓存数据
     */
    Mono<T> load();

}
