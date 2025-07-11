package site.cspy.service.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import static site.cspy.service.util.WxCrypto.XML_OM;

@Data
@JacksonXmlRootElement(localName = "xml")
public class WxBasicMessage {
    @JacksonXmlProperty(localName = "ToUserName")
    private String toUserName;
    @JacksonXmlProperty(localName = "FromUserName")
    private String fromUserName;
    @JacksonXmlProperty(localName = "CreateTime")
    private long createTime;
    @JacksonXmlProperty(localName = "MsgType")
    private String msgType;
    @JacksonXmlProperty(localName = "MsgId")
    private String msgId;
    @JacksonXmlProperty(localName = "MsgDataId")
    private String msgDataId;
    @JacksonXmlProperty(localName = "Idx")
    private String idx;


    /**
     * 文本类型消息
     */
    @JacksonXmlProperty(localName = "Content")
    private String content;

    /**
     * 图片类型消息
     */
    @JacksonXmlProperty(localName = "PicUrl")
    private String picUrl;
    @JacksonXmlProperty(localName = "MediaId")
    private String mediaId;


    public static WxBasicMessage parse(String msgBody) throws JsonProcessingException {
        return XML_OM.readValue(msgBody, WxBasicMessage.class);
    }

    public String responseText(String msg) throws JsonProcessingException {
        WxTextResponse wxTextResponse = new WxTextResponse();
        wxTextResponse.setToUserName(this.fromUserName);
        wxTextResponse.setFromUserName(this.toUserName);
        wxTextResponse.setCreateTime(System.currentTimeMillis());
        wxTextResponse.setMsgType("text");
        wxTextResponse.setContent(msg);
        return XML_OM.writeValueAsString(wxTextResponse);
    }
}
