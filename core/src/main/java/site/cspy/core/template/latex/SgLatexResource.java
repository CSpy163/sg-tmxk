package site.cspy.core.template.latex;

import java.io.File;
import java.io.IOException;

/**
 * latex 用到的资源文件，目前有以下类型：
 * 1. 图片
 */
public interface SgLatexResource {
    /**
     * 资源名称，用于在模板中引用
     *
     * @return 资源名称
     */
    String getResourceName();

    /**
     * 复制到目标位置
     *
     * @param targetFile 目标文件
     */
    void copyTo(File targetFile) throws IOException;
}
