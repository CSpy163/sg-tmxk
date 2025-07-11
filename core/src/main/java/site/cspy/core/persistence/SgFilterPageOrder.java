package site.cspy.core.persistence;

/**
 * 真实列名的排序关系。
 *
 * @param columnName 数据库中的真实列名
 * @param orderType  排序方式
 */
public record SgFilterPageOrder(String columnName, SgFilterPageOrderType orderType) {

    /**
     * 按列名的排序方式
     */
    public enum SgFilterPageOrderType {
        DESC, ASC;
    }

}
