package site.cspy.core.util;

import org.apache.tika.Tika;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class SgWebFluxUtil {

    private static Mono<byte[]> handleError(Throwable throwable) {
        // 记录日志或执行其他异常处理
        System.err.println("Error reading file: " + throwable.getMessage());
        return Mono.empty(); // 或者返回一个默认值
    }

    public static Mono<byte[]> readFileToMono(File file) {
        return Mono.fromCallable(() -> Files.readAllBytes(file.toPath()))
                .publishOn(Schedulers.boundedElastic())
                .onErrorResume(SgWebFluxUtil::handleError);
    }

    static final Tika TIKA = new Tika();

    public static Mono<ResponseEntity<byte[]>> fileToResponse(File file) {
        String fileName = file.getName();
        String urlEncodeName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        return readFileToMono(file)
                .map(bytes -> {
                    String mimeType = TIKA.detect(fileName);
                    return ResponseEntity.ok()
                            .header("Content-Disposition", "attachment; filename=" + urlEncodeName)
                            .header("Content-Type", mimeType)
                            .body(bytes);
                });
    }


}
