package site.cspy.core.template.latex.resources;

import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.context.Context;
import site.cspy.core.template.latex.SgLatexResource;
import site.cspy.core.template.util.SgTemplateFileRender;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
public class SgLatexResourceEchartsCT implements SgLatexResource {
    private final String classpathTemplate;
    private final Context context;
    private final String resourceName;

    public SgLatexResourceEchartsCT(String classpathTemplate, Context context, String resourceName) {
        this.classpathTemplate = classpathTemplate;
        this.context = context;
        this.resourceName = resourceName;
    }

    @Override
    public String getResourceName() {
        return resourceName;
    }

    @Override
    public void copyTo(File targetFile) throws IOException {
        String html = SgTemplateFileRender.newInstance()
                .classpath(classpathTemplate)
                .setContext(context)
                .render();

        File tempFile = File.createTempFile("sg-latex-html-", ".html");
        tempFile.deleteOnExit();
        Files.writeString(tempFile.toPath(), html);
//        SgBrowserUtil.saveCaptureTo(tempFile.getAbsolutePath(), "main", targetFile);
    }


}
