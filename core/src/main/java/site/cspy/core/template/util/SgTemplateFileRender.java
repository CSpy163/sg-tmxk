
package site.cspy.core.template.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringWebFluxTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import site.cspy.core.util.SgSpringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import static site.cspy.core.constant.SpringConstant.OM;
import static site.cspy.core.constant.SpringConstant.PRETTY_OM;


/**
 * 使用 Thymeleaf 模板引擎进行文本渲染
 */
@Slf4j
public class SgTemplateFileRender {

    /**
     * 任务 ID
     */
    private final String taskId;

    /**
     * 上下文，用于存储模板变量
     */
    private final Context context = new Context();

    /**
     * 模板模式
     */
    private TemplateMode mode = TemplateMode.TEXT;

    /**
     * 模板内容
     */
    @Getter
    private String template;

    /**
     * 构造函数，生成一个唯一的任务 ID
     */
    public SgTemplateFileRender() {
        this.taskId = UUID.randomUUID().toString();
        // 外部资源目录
        this.context.setVariable("EXTERNAL_RESOURCE", SgSpringUtil.getExternalStatic());
    }

    /**
     * 创建 SgReportFileRender 实例的静态方法
     *
     * @return 新的 SgReportFileRender 实例
     */
    public static SgTemplateFileRender newInstance() {
        return new SgTemplateFileRender();
    }

    /**
     * 设置模板内容
     *
     * @param template 模板字符串
     * @return 当前实例
     */
    public SgTemplateFileRender template(String template) {
        this.template = template;
        return this;
    }

    /**
     * 从文件设置模板内容
     *
     * @param file 模板文件
     * @return 当前实例
     */
    public SgTemplateFileRender template(@NonNull File file) {
        if (!file.exists() || !file.isFile()) {
            log.warn("文件不存在或不是有效文件，path: {}", file.getAbsolutePath());
            return this;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            return inputStream(fis);
        } catch (IOException e) {
            log.warn("文件读取错误，path: {}", file.getAbsolutePath(), e);
        }
        return this;
    }

    /**
     * 从类路径资源设置模板内容
     *
     * @param classpath 类路径资源路径
     * @return 当前实例
     */
    public SgTemplateFileRender classpath(String classpath) {
        ClassPathResource classPathResource = new ClassPathResource(classpath);
        try {
            return inputStream(classPathResource.getInputStream());
        } catch (IOException e) {
            log.warn("资源读取错误，classpath: {} TaskID: {}", classpath, taskId);
        }
        return this;
    }

    /**
     * 从输入流设置模板内容
     *
     * @param inputStream 输入流
     * @return 当前实例
     */
    public SgTemplateFileRender inputStream(@NonNull InputStream inputStream) {
        try (inputStream) {
            // 读取所有内容
            template = new String(inputStream.readAllBytes());
        } catch (IOException e) {
            log.warn("流读取错误！TaskID: {}", taskId);
        }
        return this;
    }

    /**
     * 设置上下文变量
     *
     * @param name  变量名
     * @param value 变量值
     * @return 当前实例
     */
    public SgTemplateFileRender setData(String name, Object value) {
        this.context.setVariable(name, value);
        return this;
    }

    /**
     * 设置上下文变量
     *
     * @param extraContext 上下文
     * @return 当前实例
     */
    public SgTemplateFileRender setContext(Context extraContext) {
        if (extraContext != null) {
            extraContext.getVariableNames().forEach(key -> {
                this.context.setVariable(key, extraContext.getVariable(key));
            });
        }
        return this;
    }

    /**
     * 批量设置上下文变量
     *
     * @param data 变量数据
     * @return 当前实例
     */
    public SgTemplateFileRender dataMap(Map<String, Object> data) {
        this.context.setVariables(data);
        return this;
    }

    public SgTemplateFileRender mode(@NonNull TemplateMode mode) {
        this.mode = mode;
        return this;
    }

    /**
     * 渲染模板并返回结果
     *
     * @return 渲染后的文本
     * @throws RuntimeException 如果模板内容为空则抛出异常
     */
    public String render() {
        if (StringUtils.hasText(template)) {
            return templateEngine().process(template, context);
        }
        throw new RuntimeException("模板内容不能为空！");
    }


    /**
     * 创建并配置 SpringWebFluxTemplateEngine
     *
     * @return 配置好的 TemplateEngine 实例
     */
    private SpringWebFluxTemplateEngine templateEngine() {
        // 创建 StringTemplateResolver 用于渲染纯文本模板
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(mode);  // 设置模板模式为TEXT
        templateResolver.setCacheable(false);      // 设置为非缓存，开发模式下使用

        // 创建 TemplateEngine 并配置
        SpringWebFluxTemplateEngine templateEngine = new SpringWebFluxTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }

    public static String toJson(Object obj) {
        try {
            return PRETTY_OM.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON 化失败！");
            return "";
        }
    }

    public static <T> T readJson(String data, Class<T> clazz) {
        try {
            log.info("JSON 解析中...\n{}", data);
            return OM.readValue(data, clazz);
        } catch (JsonProcessingException e) {
            log.error("❌ JSON 解析失败！data = {}, class = {}", data, clazz.getSimpleName(), e);
            return null;
        }
    }

    public static String toInlineJson(Object obj) {
        try {
            return OM.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON 化失败！");
            return "";
        }
    }
}