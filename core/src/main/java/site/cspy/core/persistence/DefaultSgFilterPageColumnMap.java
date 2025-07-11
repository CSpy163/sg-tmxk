package site.cspy.core.persistence;

import lombok.NonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 简单实现的列名映射
 *
 * @param <T> 实体类型
 */
public class DefaultSgFilterPageColumnMap<T> implements SgFilterPageColumnMap<T> {
    /**
     * 列名映射集合
     */
    Map<String, Function<String, Object>> columnMap = new HashMap<>();

    /**
     * 构造简单的列名映射
     *
     * @param simpleColumns 列名
     */
    public DefaultSgFilterPageColumnMap(String... simpleColumns) {
        addSimple(simpleColumns);
    }

    /**
     * 简单添加
     *
     * @param simpleColumns 简单列名
     * @return this
     */
    public DefaultSgFilterPageColumnMap<T> addSimple(String... simpleColumns) {
        Arrays.asList(simpleColumns).forEach(col -> {
            columnMap.put(col, s -> s);
        });
        return this;
    }

    /**
     * 手动添加转换方法
     *
     * @param columnName 列名
     * @param mapper     转换方法
     * @return this
     */
    public DefaultSgFilterPageColumnMap<T> add(String columnName, Function<String, Object> mapper) {
        columnMap.put(columnName, mapper);
        return this;
    }

    @Override
    public @NonNull Map<String, Function<String, Object>> getColumnMaps() {
        return columnMap;
    }
}
