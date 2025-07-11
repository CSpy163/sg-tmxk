package site.cspy.core.sse;

/**
 * SSE 会话信息
 *
 * @param sessionID     会话 ID
 * @param aesToken      AES 加密密钥
 * @param expireSeconds 过期时间
 */
public record SseSessionInfo(
        String sessionID,
        String aesToken,
        int expireSeconds
) {
}
