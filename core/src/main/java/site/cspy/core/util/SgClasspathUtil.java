package site.cspy.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.lang.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Latex 报表工具类
 */
@Slf4j
public class SgClasspathUtil {

    /**
     * 资源路径表达式
     */
    static final String CLASSPATH_FULL_PATTERN = "classpath*:%s/**/*";


    /**
     * 将 classpath 下的文件复制到指定目录
     *
     * @param classpath  classpath 路径
     * @param targetPath 目标路径
     */
    public static void copyClassPathToPath(String classpath, @NonNull Path targetPath) throws IOException {
        // 模式匹配资源
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        // 路径模式
        Pattern pathPattern = Pattern.compile("^.*%s/?(?<relativePath>.*)$".formatted(classpath));

        try {
            // 找到符合条件的资源文件
            Resource[] resources = patternResolver.getResources(CLASSPATH_FULL_PATTERN.formatted(classpath));
            // 复制资源到 targetPath
            for (Resource resource : resources) {
                if (resource instanceof FileSystemResource fsResource) {
                    // 匹配相对路径
                    Matcher matcher = pathPattern.matcher(fsResource.getPath());
                    if (matcher.matches()) {
                        // 获取相对路径
                        String relativePath = matcher.group("relativePath");
                        // 新建 targetPath 下面的文件，与相对路径匹配
                        File file = new File(targetPath.toFile(), relativePath);
                        if (fsResource.isReadable()) {
                            Files.createDirectories(file.toPath().getParent());
                            // 开始复制
                            try (InputStream inputStream = fsResource.getInputStream()) {
                                Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            }
                        } else {
                            // 如果是文件夹，则创建文件夹
                            Files.createDirectories(file.toPath());
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.info(MessageFormat.format("「报表错误」复制 classpath 文件到目标路径失败！{0} Error: {1}", classpath, e.getMessage()));
            throw e;
        }
    }
}
