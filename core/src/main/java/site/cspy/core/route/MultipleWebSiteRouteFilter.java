package site.cspy.core.route;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.*;

import static site.cspy.core.route.MultipleWebSiteHelper.getUserLocale;


@Slf4j
public class MultipleWebSiteRouteFilter implements WebFilter {

    /**
     * @param isProd    是否是生产环境
     * @param siteInfos 路由信息
     */
    public MultipleWebSiteRouteFilter(Boolean isProd, WebSiteRouteInfo... siteInfos) {
        this.isProd = Boolean.TRUE.equals(isProd);
        this.siteInfos = siteInfos;
    }

    /**
     * 网站产物目录
     */
    private static final String WEBSITES_PATH = "dist";

    /**
     * 检测资源类型
     */
    static final Tika TIKA = new Tika();


    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        // 获取域名
        InetSocketAddress host = request.getHeaders().getHost();
        Locale userLocale = getUserLocale(exchange);
        if (host != null) {
            // 解析路由
            WebSiteRouteInfo info = isProd ? getWebSiteInfo(host.getHostString()) : getWebSiteInfo(getDevPort(request));
            // 匹配成功，继续处理
            if (info != null) {
                WebSiteInfo webSiteInfo = new WebSiteInfo(info.siteTenantId(), userLocale);
                // 如果是接口请求，直接放行
                if (isApiRequest(request)) {
                    // 设置 siteName 属性，供数据筛选
                    exchange.getAttributes().put("siteName", info.siteTenantId());
                    return chain.filter(exchange).contextWrite(ctx -> ctx.put("WebSiteInfo", webSiteInfo));
                }
                log.info("HostRouteFilter: route={}, path={}", info.siteTenantId(), request.getURI().getPath());

                // 原路径
                String path = request.getURI().getPath();
                // 请求资源并写出
                ServerHttpResponse response = exchange.getResponse();
                // 尝试加载静态资源
                ClassPathResource classPathResource = tryClassPathResource(info, path);
                if (classPathResource != null) {
                    log.info("加载静态资源：「{}」", classPathResource.getFilename());
                    // 返回静态文件内容
                    return Mono.fromCallable(() -> {
                                // 读取资源文件
                                try (InputStream inputStream = classPathResource.getInputStream()) {
                                    // ！检测文件类型并设置响应头（根据文件名解析）
                                    String mimeType = TIKA.detect(classPathResource.getFilename());
                                    response.getHeaders().setContentType(MediaType.parseMediaType(mimeType));
                                    // 返回文件内容
                                    return response.bufferFactory().wrap(inputStream.readAllBytes());
                                }
                            })// 将任务调度到 elastic 线程池（专门用于阻塞 I/O 操作）
                            .subscribeOn(Schedulers.boundedElastic())
                            // 将文件内容写入响应流
                            .flatMap(buffer -> response.writeWith(Mono.just(buffer)))
                            .contextWrite(ctx -> ctx.put("WebSiteInfo", webSiteInfo));
                }
            }
        }
        return chain.filter(exchange);
    }


    /**
     * 获取开发端口
     *
     * @param request
     * @return
     */
    private int getDevPort(ServerHttpRequest request) {
        String proxyHost = request.getHeaders().getFirst("X-Forwarded-Host");
        if (StringUtils.hasText(proxyHost)) {
            return proxyHost.contains(":") ? Integer.parseInt(proxyHost.split(":")[1]) : 80;
        } else {
            InetSocketAddress host = request.getHeaders().getHost();
            return host != null ? host.getPort() : 80;
        }
    }

    /**
     * 根据 path 获取静态资源
     *
     * @param info 路由
     * @param path 路径
     * @return 静态资源
     */
    private ClassPathResource tryClassPathResource(WebSiteRouteInfo info, String path) {
        // 尝试获取多种路径形式的静态资源
        return getSpecificResource(info.siteTenantId(), path)
                .or(() -> getSpecificResource(info.siteTenantId(), path + ".html"))
                // 处理 / 请求
                .or(() -> getSpecificResource(info.siteTenantId(), path + "static/index.html"))
                .or(() -> getSpecificResource(info.siteTenantId(), path + "en.html"))
                .orElse(null);
    }

    /**
     * 尝试从指定路径加载资源，如果存在则返回 Optional
     *
     * @param siteTenantId 基础路径
     * @param relativePath 相对路径
     * @return 如果资源存在，则返回 ClassPathResource 的 Optional
     */
    private Optional<ClassPathResource> getSpecificResource(String siteTenantId, String relativePath) {
        String resourcePath = "%s/%s".formatted(WEBSITES_PATH, siteTenantId) + relativePath;
        ClassPathResource resource = new ClassPathResource(resourcePath);
        // 检查资源是否存在并且不是目录
        try (InputStream inputStream = resource.getInputStream()) {
            // 如果输入流可读取并且不是空，则认为是有效文件资源
            if (inputStream.available() > 0) {
                return Optional.of(resource);
            }
        } catch (IOException e) {
            log.warn("资源不存在或为空：{}", resourcePath);
        }
        return Optional.empty();
    }

    /**
     * 判断是否是 API 请求
     *
     * @param request 请求
     * @return 是否是 API 请求
     */
    private boolean isApiRequest(ServerHttpRequest request) {
        return request.getURI().getPath().startsWith("/api");
    }


    /**
     * 获取 路由信息
     *
     * @param devPort 开发端口
     * @return 路由信息
     */
    public WebSiteRouteInfo getWebSiteInfo(int devPort) {
        List<WebSiteRouteInfo> infos = Arrays.stream(siteInfos)
                .filter(route -> Objects.equals(devPort, route.devPort()))
                .sorted(Comparator.comparing(WebSiteRouteInfo::siteTenantId))
                .toList();
        return CollectionUtils.isEmpty(infos) ? null : infos.get(0);
    }

    /**
     * 获取 路由信息
     *
     * @param hostName 域名
     * @return 路由信息
     */
    public WebSiteRouteInfo getWebSiteInfo(String hostName) {
        List<WebSiteRouteInfo> infos = Arrays.stream(siteInfos)
                .filter(route -> Arrays.asList(route.hostNames()).contains(hostName))
                .sorted(Comparator.comparing(WebSiteRouteInfo::siteTenantId))
                .toList();
        return CollectionUtils.isEmpty(infos) ? null : infos.get(0);
    }


    /**
     * 路由信息
     */
    private final WebSiteRouteInfo[] siteInfos;

    /**
     * 是否是生产模式
     */
    private final Boolean isProd;

}
