package site.cspy.core.persistence;

import lombok.NonNull;

import java.util.Map;
import java.util.function.Function;

/**
 * 用于配合 SgFilterPage 检验有效列名。
 */
public interface SgFilterPageColumnMap<T> {

    /**
     * 返回有效 column 映射。
     * <p>key： 有效的列名</p>
     * <p>value: 映射器，从 String 转为 Object，方便 QueryWrapper 进行使用。</p>
     *
     * @return 值映射器
     */
    @NonNull
    Map<String, Function<String, Object>> getColumnMaps();
}
