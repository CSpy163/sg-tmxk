package site.cspy.core.template.latex.resources;

import org.springframework.core.io.ClassPathResource;
import site.cspy.core.template.latex.SgLatexResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * 静态资源图片
 */
public class SgLatexResourceStaticClasspathImage implements SgLatexResource {
    private final String resourceName;
    private final String classpath;

    public SgLatexResourceStaticClasspathImage(String resourceName, String classpath) {
        this.resourceName = resourceName;
        this.classpath = classpath;
    }

    @Override
    public String getResourceName() {
        return resourceName;
    }

    @Override
    public void copyTo(File targetFile) throws IOException {
        if (targetFile != null) {
            // 加载类路径中的文件
            ClassPathResource classPathResource = new ClassPathResource(classpath);

            // 检查资源是否存在
            if (!classPathResource.exists()) {
                throw new IOException("File not found in classpath: " + classpath);
            }

            // 复制文件到目标路径
            Files.copy(classPathResource.getInputStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
