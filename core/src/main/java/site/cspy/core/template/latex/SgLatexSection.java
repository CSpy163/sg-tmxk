package site.cspy.core.template.latex;


import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import site.cspy.core.template.util.SgTemplateFileRender;

import java.util.*;

/**
 * v20241023
 * <p>
 * latex 片段
 */
@Slf4j
public abstract class SgLatexSection {
    /**
     * 参数
     */
    protected final Context context = new Context();

    /**
     * 子部件映射
     */
    private final Map<String, List<SgLatexSection>> subSectionMap = new HashMap<>();

    /**
     * 资源
     */
    private final List<SgLatexResource> resources = new ArrayList<>();

    /**
     * 设置变量
     *
     * @param key
     * @param value
     */
    public void addData(String key, Object value) {
        context.setVariable(key, value);
    }

    /**
     * 设置完整变量
     *
     * @param extraContext
     */
    public void addContext(@NonNull Context extraContext) {
        extraContext.getVariableNames().forEach(key -> {
            addData(key, extraContext.getVariable(key));
        });
    }

    /**
     * 手动添加静态资源
     *
     * @param resources 静态资源
     * @return
     */
    public SgLatexSection addResources(SgLatexResource... resources) {
        this.resources.addAll(Arrays.asList(resources));
        return this;
    }

    /**
     * 手动添加子部件
     *
     * @param name        子部件名称
     * @param subSections 子部件
     * @return
     */
    public SgLatexSection addSubSections(String name, @NonNull SgLatexSection... subSections) {
        if (StringUtils.hasText(name) && subSections.length > 0) {
            List<SgLatexSection> currentSubs = subSectionMap.getOrDefault(name, new ArrayList<>());
            List<SgLatexSection> newSubs = Arrays.asList(subSections);
            currentSubs.addAll(newSubs);
            // 更新现有 name 子部件列表
            subSectionMap.put(name, currentSubs);
            // 收集新子部件的静态资源
            List<SgLatexResource> resourceList = Arrays.stream(subSections).flatMap(s -> s.getResources().stream()).toList();
            resources.addAll(resourceList);
        } else {
            log.warn("无效子组件，跳过操作！");
        }
        return this;
    }

    /**
     * 禁止重写
     * @return
     */
    public final List<SgLatexResource> getResources() {
        return resources;
    }

    /**
     * 获取模板
     *
     * @return
     */
    public abstract SgTemplateFileRender getRenderer();

    /**
     * 获取正文
     *
     * @return 正文
     */
    public String getContent() {
        return getContent("");
    }

    /**
     * 获取正文同时设置 命名空间
     *
     * @param namespace
     * @return
     */
    public String getContent(String namespace) {
        SgTemplateFileRender renderer = getRenderer();
        // 先设置 命名空间 环境变量
        renderer.setContext(context)
                .setData("namespace", namespace)
                .setData("nsPrefix", StringUtils.hasText(namespace) ? namespace + "_" : "");
        // 根据命名空间计算子组件
        for (String subSectionName : subSectionMap.keySet()) {
            List<String> list = subSectionMap.getOrDefault(subSectionName, List.of()).stream().map(ss -> ss.getContent(namespace)).toList();
            renderer.setData(subSectionName, list);
        }
        return renderer.render();
    }
}
