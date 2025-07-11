package site.cspy.core.template.latex.resources;

import org.springframework.util.StringUtils;
import site.cspy.core.template.latex.SgLatexResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base64 图片资源
 */
public class SgLatexResourceBase64Image implements SgLatexResource {

    // 1. 正则表达式提取 Base64 数据部分（去掉头信息）
    static final Pattern BASE64_PATTERN = Pattern.compile("^(data:(.*?);base64,)?(?<body>.*)$");

    private final String resourceName;
    private final String base64;

    public SgLatexResourceBase64Image(String resourceName, String base64) {
        this.resourceName = resourceName;
        this.base64 = base64;
    }

    @Override
    public String getResourceName() {
        return resourceName;
    }

    @Override
    public void copyTo(File targetFile) throws IOException {
        if (StringUtils.hasText(base64)) {
            Matcher matcher = BASE64_PATTERN.matcher(base64);
            if (matcher.matches()) {
                String body = matcher.group("body");
                Files.write(targetFile.toPath(), Base64.getDecoder().decode(body));
            }
        }
    }
}
