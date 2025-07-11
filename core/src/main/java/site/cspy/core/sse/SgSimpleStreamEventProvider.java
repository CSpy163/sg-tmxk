package site.cspy.core.sse;

import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Component("SSE_SIMPLE_PROVIDER")
public class SgSimpleStreamEventProvider implements SgStreamEventProvider {


    @Override
    public AbstractSseSession getSession(MultiValueMap<String, String> params) {
        return null;
    }
}
