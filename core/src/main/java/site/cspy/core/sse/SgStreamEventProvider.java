package site.cspy.core.sse;

import org.springframework.util.MultiValueMap;

public interface SgStreamEventProvider {
    AbstractSseSession getSession(MultiValueMap<String, String> params);
}
