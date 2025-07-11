package site.cspy.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 用于静态化获取 Spring 上下文
 */
@Slf4j
@Component
public class SgSpringUtil implements ApplicationContextAware {

    /**
     * 静态 context
     */
    private static ApplicationContext context;

    /**
     *
     */
    private static final String EXTERNAL_STATIC = "core-starter.template.external_static";

    /**
     * 获取模板模块的依赖路径
     * @return 模板模块的依赖路径
     */
    public static String getExternalStatic() {
        // 获取用户的根目录
        Environment environment = context.getEnvironment();
        String externalStatic = environment.getProperty(EXTERNAL_STATIC);
        if (!StringUtils.hasText(externalStatic)) {
            log.warn("未配置 {} 环境变量，默认使用用户根目录。", EXTERNAL_STATIC);
            return System.getProperty("user.home");
        }
        return externalStatic;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        SgSpringUtil.context = applicationContext;
    }
}
