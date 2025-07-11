package site.cspy.service.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.cspy.service.domain.WxBasicMessage;
import site.cspy.service.domain.WxSignatureQuery;
import site.cspy.service.util.WxCrypto;

@Slf4j
@RestController
@RequestMapping("/api/wechat")
public class WeChatController {

    @Resource
    WxCrypto wxCrypto;


    @GetMapping("/eventListener")
    public ResponseEntity<String> eventListener(WxSignatureQuery signatureQuery, @RequestParam("echostr") String echostr) {
        // 校验微信签名
        if (wxCrypto.verifySignature(signatureQuery)) {
            return ResponseEntity.ok(echostr);
        }
        return ResponseEntity.badRequest().body("Signature Not Match!");
    }

    @PostMapping("/eventListener")
    public ResponseEntity<String> postEventListener(WxSignatureQuery signatureQuery, @RequestBody String encryptedBody) throws Exception {
        log.info("encryptedBody: {}", encryptedBody);
        // 校验微信签名
        if (wxCrypto.verifySignature(signatureQuery)) {
            // 解密消息
            WxBasicMessage message = WxBasicMessage.parse(wxCrypto.decryptMsg(encryptedBody));
            switch (message.getMsgType()) {
                case "text":
                    log.info("收到文本消息：{}", message.getContent());
                    break;
                case "image":
                    log.info("收到图片消息：{}", message.getPicUrl());
                    return ResponseEntity.ok(message.responseText(""));
            }
            return ResponseEntity.ok(message.responseText(String.join("\n", message.getContent().split(""))));
        }
        return ResponseEntity.badRequest().body("Signature Not Match!");
    }



}
