package site.cspy.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import site.cspy.service.domain.WxAkResponse;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
@Service
@RequiredArgsConstructor
public class WechatContext {
    private final RedissonClient redissonClient;

    @Value("${tmxk.wechat.app-id}")
    private String appId;
    @Value("${tmxk.wechat.app-key}")
    private String appKey;

    private static final String AK_BUCKET = "WX_AK";
    private static final String AK_LOCK = "lock:refreshWxAk";


    /**
     * 刷新微信 AK，并缓存
     *
     * @return
     */
    public Mono<String> refreshWxAccessToken() {
        return WebClient.builder()
                .baseUrl("https://api.weixin.qq.com")
                .build()
                .get().uri(uriBuilder -> {
                    return uriBuilder.path("/cgi-bin/token")
                            .queryParam("grant_type", "client_credential")
                            .queryParam("appid", appId)
                            .queryParam("secret", appKey).build();
                }).retrieve().bodyToMono(WxAkResponse.class)
                .retry(3)
                .map(akResponse -> {
                    if (akResponse != null && StringUtils.hasText(akResponse.getAccessToken())) {
                        // 获取 bucket 并更新值
                        RBucket<String> bucket = redissonClient.getBucket(AK_BUCKET);
                        bucket.set(akResponse.getAccessToken(), Duration.of(akResponse.getExpiresIn(), SECONDS));
                        return akResponse.getAccessToken();
                    }
                    throw new RuntimeException("获取微信AK失败！");
                });
    }


    /**
     * 获取微信 AK
     *
     * @return
     */
    public Mono<String> getWxAccessToken() {
        RBucket<String> bucket = redissonClient.getBucket(AK_BUCKET);
        if (!bucket.isExists()) {
            RLock lock = redissonClient.getLock(AK_LOCK);
            if (lock.isLocked()) {
                return Mono.error(new RuntimeException("微信AK锁被占用，请稍等！"));
            } else {
                if (lock.tryLock()) {
                    return refreshWxAccessToken()
                            .doFinally(signal -> lock.unlock());
                } else {
                    return Mono.error(new RuntimeException("微信AK锁正在刷新，请稍等！"));
                }
            }
        } else {
            return Mono.just(bucket.get());
        }
    }


}
