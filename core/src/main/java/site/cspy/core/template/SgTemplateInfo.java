package site.cspy.core.template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 渲染信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SgTemplateInfo {
    private String name;
    private String code;
    private String version;
    private String author;
    private String email;
    private LocalDateTime createTime;
}
