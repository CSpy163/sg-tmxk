package site.cspy.core.persistence.util;


import lombok.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import site.cspy.core.persistence.SgFilterPage;
import site.cspy.core.persistence.SgFilterPageColumnMap;
import site.cspy.core.persistence.SgFilterPageOrder;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static site.cspy.core.persistence.SgFilterPageOrder.SgFilterPageOrderType.ASC;
import static site.cspy.core.persistence.SgFilterPageOrder.SgFilterPageOrderType.DESC;


/**
 * 用于 {@link SgFilterPage} 相关的工具类。
 */
public class SgFilterPageUtil {

    /**
     * 精确匹配列名。
     *
     * @param map        列名映射，key 是数据库中的列名。
     * @param columnName 用户传入的列名
     * @return 是否匹配
     */
    public static boolean matchColumn(@NonNull SgFilterPageColumnMap<?> map, @NonNull String columnName) {
        return StringUtils.hasText(columnName) && map.getColumnMaps().containsKey(columnName);
    }


    /**
     * 判断传入的 columnName 是否有效
     *
     * @param map        列名映射，key 是数据库中的列名。
     * @param columnName 列名（允许传入：数据库中一致的列名、全大写列名、全小写列名）
     * @return 是否是有效列名
     */
    public static String getColNameInDb(@NonNull SgFilterPageColumnMap<?> map, @NonNull String columnName) {
        if (StringUtils.hasText(columnName)) {
            // 获取列映射器
            Map<String, Function<String, Object>> columnMaps = map.getColumnMaps();
            return columnMaps.keySet().stream().filter(Objects::nonNull)
                    // 只允许列名完全相同、大写形式相同（DESC）、小写形式相同（ASC）
                    .filter(col -> col.equals(columnName) || col.toUpperCase().equals(columnName) || col.toLowerCase().equals(columnName))
                    .findFirst().orElse(null);
        }
        return null;
    }

    /**
     * 获取有效列名的排序方式
     *
     * @param map        列名映射，key 是数据库中的列名。
     * @param columnName 列名
     * @return 排序方式（column全大写 DESC、其他形式（全小写，非全大写的全匹配）为 ASC）
     */
    public static SgFilterPageOrder getOrderType(@NonNull SgFilterPageColumnMap<?> map, @NonNull String columnName) {
        String dbColumn = getColNameInDb(map, columnName);
        if (StringUtils.hasText(dbColumn)) {
            SgFilterPageOrder.SgFilterPageOrderType orderType = Objects.equals(dbColumn.toUpperCase(), columnName) ? DESC : ASC;
            return new SgFilterPageOrder(dbColumn, orderType);
        }
        return null;
    }

    /**
     * filterMap 获取有效列名。
     *
     * @param filterMap 过滤器子 columnMap
     * @param columnMap 列名映射
     * @return 过滤器子 columnMap 有效的列名
     */
    public static List<String> getValidColumnNames(Map<String, ?> filterMap, @NonNull SgFilterPageColumnMap<?> columnMap) {
        if (CollectionUtils.isEmpty(filterMap)) {
            return List.of();
        }
        return filterMap.keySet().stream().map(key -> getColNameInDb(columnMap, key)).filter(Objects::nonNull).toList();
    }


    /**
     * 快速转换值
     *
     * @param columnMap  列名映射
     * @param columnName 列名
     * @param value      值（字符串类型）
     * @return 转换后的值
     */
    public static Object mapTo(@NonNull SgFilterPageColumnMap<?> columnMap, String columnName, String value) {
        return columnMap.getColumnMaps().get(columnName).apply(value);
    }

}
