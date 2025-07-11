package site.cspy.core.persistence;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 前端分页过滤请求参数。
 * <p>仅适用于 Mybatis-Plus 生态中简单的单表查询。</p>
 * 关联查询（一对多）请自行实现。
 */
@Data
public class SgFilterPage {

    /**
     * 页码（从1开始）
     */
    @Min(1)
    private int pageStart;

    /**
     * 每页大小
     */
    @Min(1)
    private int pageSize;

    /**
     * 查询参数
     */
    private SgFilterReq filter = new SgFilterReq();

}
