package site.cspy.core.persistence.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.LocalDateTimeTypeHandler;

import java.time.LocalDateTime;

/**
 * 基础实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {
    @TableId(type = IdType.ASSIGN_UUID)
//    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    protected String id;
    @TableField(fill = FieldFill.INSERT, typeHandler = LocalDateTimeTypeHandler.class)
    protected LocalDateTime createAt;
    @TableField(fill = FieldFill.INSERT)
    protected String createBy;
    @TableField(fill = FieldFill.UPDATE, typeHandler = LocalDateTimeTypeHandler.class)
    protected LocalDateTime modifyAt;
    @TableField(fill = FieldFill.UPDATE)
    protected String modifyBy;
    protected String remark;
    @Version
    protected Integer version;
}
