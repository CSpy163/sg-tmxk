package site.cspy.core.sse;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

import static site.cspy.core.sse.SgAesUtil.defaultAesKey;
import static site.cspy.core.sse.SgAesUtil.uuid;

@Slf4j
public abstract class AbstractSseSession {
    protected final String aesToken;
    protected final int expireSeconds;
    @Getter
    protected final String sessionID;
    protected LocalDateTime expireAt;

    Sinks.Many<ServerSentEvent<String>> mySink = Sinks.many().replay().limit(1);

    public AbstractSseSession(int expireSeconds) {
        this.sessionID = uuid();
        this.expireSeconds = expireSeconds;
        this.aesToken = defaultAesKey();
        this.expireAt = LocalDateTime.now().plusSeconds(expireSeconds);
    }

    public boolean expired() {
        return expireAt.isBefore(LocalDateTime.now());
    }


    /**
     * 每个 messages 都是单行文本
     *
     * @param messages
     */
    public void sendData(String... messages) {
        sendEvent("message", messages);
    }

    public void sendEvent(String event, String... messages) {
        ServerSentEvent.Builder<String> builder = ServerSentEvent.builder();
        builder.event(event);
        Arrays.stream(messages).forEach(builder::data);
        mySink.tryEmitNext(builder.build());
    }

    public static final String HEARTBEAT_TOKEN = "ping";

    public boolean canConnect(String nonce) {
        try {
            log.debug("Attempting to connect with nonce: {}", nonce);
            log.debug("Using aesToken: {}", aesToken);
            String decrypted = SgAesUtil.decrypt(nonce, aesToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            log.debug("Decrypted value: {}, Expected: {}", decrypted, HEARTBEAT_TOKEN);
            if (Objects.equals(decrypted, HEARTBEAT_TOKEN)) {
                this.refresh();
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Failed to decrypt nonce", e);
            return false;
        }
    }


//    public void emitMessage(String message) {
//        String data = "data: %s\n\n".formatted(message);
//        log.info("向 {} 发送消息：{}", sessionID, data);
//        mySink.tryEmitNext(data + "1"); // data: "message);
//        mySink.tryEmitNext(message + "2"); // data: "message);
//
//        new Thread(() -> {
//            int count = 0;
//            while (true) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//                mySink.tryEmitNext(message + count++);
//            }
//        }).start();
//    }

    public abstract void handle(MultiValueMap<String, String> messages);

    public abstract void startup();

    public Flux<ServerSentEvent<String>> eventStream() {
        return mySink.asFlux();
    }

    public void refresh() {
        expireAt = LocalDateTime.now().plusSeconds(expireSeconds);
    }

    public SseSessionInfo info() {
        return new SseSessionInfo(sessionID, aesToken, expireSeconds);
    }


}
