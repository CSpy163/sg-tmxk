package site.cspy.service;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT1M")
public class WxChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(WxChatApplication.class, args);
    }

}
