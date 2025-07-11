package site.cspy.core.template.latex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import site.cspy.core.template.SgTemplate;
import site.cspy.core.template.util.SgTemplateFileRender;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardOpenOption.CREATE;

@Slf4j
public abstract class SgLatex implements SgTemplate {

    /**
     * Latex 报表片段
     */
    private final List<SgLatexSection> sections = new ArrayList<>();

    public SgLatex(SgLatexSection... sections) {
        this.sections.addAll(List.of(sections));
    }

    @Override
    public String getTemplate() {
        return """
                \\documentclass[a4paper]{ctexart}
                \\begin{document}
                [(${CONTENT})]
                \\end{document}
                """;
    }

    @Override
    public String render() {
        // 解析正文
        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < sections.size(); i++) {
            SgLatexSection section = sections.get(i);
            contentBuilder.append(section.getContent(String.valueOf(i)));
        }

        // 返回 main.tex
        return SgTemplateFileRender.newInstance()
                .template(getTemplate())
                .setData("CONTENT", contentBuilder.toString()).render();
    }

    /**
     * 全局资源
     *
     * @return
     */
    protected List<SgLatexResource> getGlobalResources() {
        return List.of();
    }

    /**
     * 获取编译命令
     *
     * @param basename 文件名
     * @return
     */
    protected List<List<String>> getCompileCommands(Path compilePath, String basename) {
        return List.of(
                List.of("xelatex", "-interaction=nonstopmode", "%s.tex".formatted(basename))
        );
    }

    public Mono<File> makeFile(String basename) {
        if (StringUtils.hasText(basename)) {
            return Mono.fromCallable(() -> {
                        // 创建编译目录
                        Path tempDirectory = Files.createTempDirectory("sg-latex-make-");
                        log.info("临时编译目录：{}", tempDirectory.toString());
                        // todo: 调试用
//                        Runtime.getRuntime().exec("explorer %s".formatted(tempDirectory));
                        // 解析全局资源
                        for (SgLatexResource resource : getGlobalResources()) {
                            // 将资源复制到 main.tex 同级目录
                            Path resourceFile = tempDirectory.resolve(resource.getResourceName());
                            log.info("复制资源文件：%s".formatted(resourceFile));
                            resource.copyTo(resourceFile.toFile());
                        }
                        for (int i = 0; i < sections.size(); i++) {
                            String nsPrefix = "%d_".formatted(i);
                            SgLatexSection section = sections.get(i);

                            for (SgLatexResource resource : section.getResources()) {
                                // 将资源复制到 main.tex 同级目录
                                Path resourceFile = tempDirectory.resolve(nsPrefix + resource.getResourceName());
                                log.info("复制资源文件：%s".formatted(resourceFile));
                                resource.copyTo(resourceFile.toFile());
                            }
                        }

                        // 写出 main.tex
                        String mainTexName = "%s.tex".formatted(basename);
                        Path mainTex = tempDirectory.resolve(mainTexName);
                        Files.writeString(mainTex, render(), CREATE);

                        for (List<String> commands : getCompileCommands(tempDirectory, basename)) {// 开始编译
                            ProcessBuilder pb = new ProcessBuilder(commands.toArray(new String[]{}));
                            pb.directory(tempDirectory.toFile());
                            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
                            pb.redirectError(ProcessBuilder.Redirect.DISCARD);
                            Process process = pb.start();
                            process.waitFor(30, TimeUnit.SECONDS);
                        }

                        // 生成最终文件
                        String finalPdfName = "%s.pdf".formatted(basename);
                        File finalReport = tempDirectory.resolve(finalPdfName).toFile();
                        log.info("最终报表位置: {}", finalReport);
                        return finalReport;
                    })
                    .subscribeOn(Schedulers.boundedElastic())  // 将耗时操作放到异步线程池中
                    .onErrorResume(e -> {
                        log.error("编译报表失败！", e);
                        return Mono.error(new RuntimeException("生成文件时出错", e));
                    });
        }
        return Mono.error(new RuntimeException("无效报表名！"));
    }
}
