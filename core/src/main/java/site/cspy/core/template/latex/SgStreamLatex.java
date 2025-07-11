package site.cspy.core.template.latex;

import org.springframework.util.StringUtils;
import reactor.core.publisher.Sinks;

public abstract class SgStreamLatex extends SgLatex {

    Sinks.Many<String> sinks;

    protected void updateState(String message) {
        if (sinks != null && StringUtils.hasText(message)) {
            sinks.tryEmitNext(message);
        }
    }

    public SgStreamLatex(Sinks.Many<String> sinks, SgLatexSection... sections) {
        super(sections);
        this.sinks = sinks;
        updateState("准备解析报表内容。");
    }

}
