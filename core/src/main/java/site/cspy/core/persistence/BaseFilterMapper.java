package site.cspy.core.persistence;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Deprecated
public interface BaseFilterMapper<T> extends BaseMapper<T> {

    String LIKE = "LIKE";
    String EQ = "EQ";
    String BETWEEN = "BETWEEN";
    String IN = "IN";

    Pattern ALLOWED_KEY_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");


    default FilterPage<T> selectFilterPage(FilterPage<T> page, QueryWrapper<T> queryWrapper) {
        if (page != null) {
            if (queryWrapper == null) {
                queryWrapper = new QueryWrapper<>();
            }

            Map<String, Map<String, Object>> filter = Optional.ofNullable(page.getFilter()).orElse(Map.of());
            for (String key : filter.keySet()) {
                Map<String, Object> specificValue = filter.get(key);
                switch (key) {
                    case LIKE -> {
                        for (String likeKey : specificValue.keySet()) {
                            if (!ALLOWED_KEY_PATTERN.matcher(likeKey).matches()) {
                                continue;
                            }
                            queryWrapper.like(likeKey, specificValue.get(likeKey));
                        }
                    }
                    case EQ -> {
                        for (String eqKey : specificValue.keySet()) {
                            if (!ALLOWED_KEY_PATTERN.matcher(eqKey).matches()) {
                                continue;
                            }
                            queryWrapper.eq(eqKey, specificValue.get(eqKey));
                        }
                    }
                    case BETWEEN -> {
                        for (String betweenKey : specificValue.keySet()) {
                            if (!ALLOWED_KEY_PATTERN.matcher(betweenKey).matches()) {
                                continue;
                            }
                            List<Object> values = specificValue.get(betweenKey) instanceof List<?> list ? (List<Object>) list : new ArrayList<>();
                            if (values.size() >= 2) {
                                Object v1 = values.get(0) instanceof String str ? StringUtils.hasText(str) ? str : null : values.get(0);
                                Object v2 = values.get(1) instanceof String str ? StringUtils.hasText(str) ? str : null : values.get(1);
                                if (v1 != null && v2 != null) {
                                    queryWrapper.between(betweenKey, v1, v2);
                                } else if (v1 != null) {
                                    queryWrapper.ge(betweenKey, v1);
                                } else if (v2 != null) {
                                    queryWrapper.le(betweenKey, v2);
                                }
                            }
                        }
                    }
                    case IN -> {
                        for (String inKey : specificValue.keySet()) {
                            if (!ALLOWED_KEY_PATTERN.matcher(inKey).matches()) {
                                continue;
                            }
                            if (specificValue.get(inKey) instanceof List<?> list) {
                                queryWrapper.in(inKey, list);
                            }

                        }
                    }
                }

            }
        }
        return selectPage(page, queryWrapper);
    }
}