package site.cspy.service.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class WxSignatureQuery {
    @JsonAlias("signature")
    private String signature;
    @JsonAlias("timestamp")
    private String timestamp;
    @JsonAlias("nonce")
    private String nonce;
}
