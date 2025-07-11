package site.cspy.omnireport.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import site.cspy.tmxk.common.omnireport.OmniReportHello;

@Slf4j
@Service
@DubboService
public class OmniReportService implements OmniReportHello {
    @Override
    public String hello(int token) {
        log.info("hello received, token: " + token);
        return "hello from omnireport, your token is " + token;
    }
}
