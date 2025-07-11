package site.cspy.core.template;

import org.thymeleaf.context.Context;

/**
 * v20241002
 * 报表上下文
 * 用于实骨报表体系。
 */
public interface SgTemplateContext {

    /**
     * 提取报表上下文
     *
     * @param ctx 上下文
     * @return 报表上下文
     */
    default Context toContext(Context ctx) {
        return ctx == null ? new Context() : ctx;
    }

    /**
     * 获取模板
     *
     * @return 模板
     */
    String getTemplate();

    /**
     * 获取报表信息
     *
     * @return 报表信息
     */
    default SgTemplateInfo getReportInfo() {
        return new SgTemplateInfo();
    }
}
