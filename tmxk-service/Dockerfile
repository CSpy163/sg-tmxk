# 基础镜像用 JRE 21
FROM eclipse-temurin:21-jre

LABEL org.opencontainers.image.title="tmxk-service"
LABEL org.opencontainers.image.description="tmxk-service 微服务，支持多环境配置"
LABEL org.opencontainers.image.documentation="https://github.com/CSpy163"
LABEL org.opencontainers.image.authors="ShiGu <cspy2117@163.com>"
LABEL org.opencontainers.image.version="1.0.0"
LABEL org.opencontainers.image.envvars="REDIS_SERVER_HOST,REDIS_SERVER_PORT,NACOS_SERVER_HOST,NACOS_SERVER_PORT,DB_FILE,WECHAT_TOKEN,WECHAT_AES_KEY,WECHAT_APP_ID,WECHAT_APP_KEY"


# 定义环境变量
ENV JAVA_OPTS="-Xms512m -Xmx512m" \
    SPRING_PROFILES_ACTIVE=prod

# 创建工作目录
WORKDIR /app

# 把Jar包复制到容器（注意这里路径）
COPY target/tmxk-service-*.jar app.jar

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
