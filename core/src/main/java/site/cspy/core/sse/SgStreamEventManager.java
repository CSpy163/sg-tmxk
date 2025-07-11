package site.cspy.core.sse;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import static site.cspy.core.i18n.I18nItem.t;
import static site.cspy.core.rest.SgResponseHelper.*;

@Slf4j
@Component
public class SgStreamEventManager {


    public CopyOnWriteArrayList<AbstractSseSession> sessions = new CopyOnWriteArrayList<>();

    @Resource
    ApplicationContext applicationContext;

    public void registerSession() {

    }


    @Scheduled(fixedRate = 60_000) // 每 60 秒执行一次
    public void cleanupExpiredSessions() {
        Iterator<AbstractSseSession> iterator = sessions.iterator();
        while (iterator.hasNext()) {
            AbstractSseSession session = iterator.next();
            if (session.expired()) {
                iterator.remove(); // 安全移除
                log.info("移除过期会话: {}", session.getSessionID());
            }
        }
    }

    @Bean
    public RouterFunction<ServerResponse> router() {
        return RouterFunctions.route()
                .GET("/api/sse/register", this::registerSession)
                .GET("/api/sse/subscribe", this::subscribeSession)
                .GET("/api/sse/message", this::message)
                .build();
    }

    public Mono<ServerResponse> registerSession(ServerRequest request) {
        String providerName = request.queryParam("provider").orElse("");
        if (StringUtils.hasText(providerName)) {
            SgStreamEventProvider provider = applicationContext.getBean(providerName, SgStreamEventProvider.class);
            AbstractSseSession session = provider.getSession(request.queryParams());
            sessions.add(session);
            log.info("创建 SSE 会话：{}", session.getSessionID());
            return ServerResponse.ok().bodyValue(sendData(session.info(), t("SUCCESS")));
        } else {
            return ServerResponse.ok().bodyValue(sendFail());
        }
    }

    public Mono<ServerResponse> subscribeSession(ServerRequest request) {
        String sessionID = request.queryParam("sessionID").orElse("");
        String nonce = request.queryParam("nonce").orElse("");

        log.info("订阅 SSE 会话：{} nonce：{}", sessionID, nonce);

        if (StringUtils.hasText(sessionID) && StringUtils.hasText(nonce)) {
            log.info("连接会话：{}", sessionID);
            AbstractSseSession session = getSession(sessionID);
            if (session.canConnect(nonce)) {
                return ServerResponse.ok()
                        .contentType(MediaType.TEXT_EVENT_STREAM) // 指定 SSE 响应格式
                        .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                        .header(HttpHeaders.CONNECTION, "keep-alive")
                        .header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
                        .body(session.eventStream(), ServerSentEvent.class);
            }
        }
        return ServerResponse.ok().bodyValue(sendFail());
    }


    private AbstractSseSession getSession(String sessionID) {
        AbstractSseSession abstractSseSession = sessions.stream().filter(s -> s.getSessionID().equals(sessionID)).findFirst().orElse(null);
        if (abstractSseSession == null) {
            throw new RuntimeException("会话不存在");
        }
        return abstractSseSession;
    }


    public Mono<ServerResponse> message(ServerRequest request) {
        String sessionID = request.queryParam("sessionID").orElse("");

        log.info("收到消息：{}", sessionID);
        if (StringUtils.hasText(sessionID)) {
            AbstractSseSession session = getSession(sessionID);
            session.handle(request.queryParams());
            return ServerResponse.ok().bodyValue(sendSuccess());
        }
        return ServerResponse.ok().bodyValue(sendFail());
    }

}
