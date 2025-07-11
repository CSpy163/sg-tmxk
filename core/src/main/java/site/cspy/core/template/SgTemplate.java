package site.cspy.core.template;

/**
 * 实骨模板根接口
 */
public interface SgTemplate {
    /**
     * 模板内容
     * @return 模板内容
     */
    String getTemplate();

    /**
     * 模板渲染出字符串
     *
     * @return 渲染结果
     */
    String render();
}
