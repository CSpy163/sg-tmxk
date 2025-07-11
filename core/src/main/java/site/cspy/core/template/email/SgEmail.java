package site.cspy.core.template.email;

import jakarta.mail.internet.MimeMessage;
import reactor.core.publisher.Mono;
import site.cspy.core.template.SgTemplate;

/**
 * 邮件模板
 */
public interface SgEmail extends SgTemplate {
    /**
     * 生成邮件
     *
     * @param to 接收者
     * @return 邮件
     */
    Mono<MimeMessage> makeEmail(String... to);
}
