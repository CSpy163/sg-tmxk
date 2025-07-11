package site.cspy.core.persistence;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import site.cspy.core.persistence.util.SgFilterPageUtil;

import java.util.List;
import java.util.Objects;

/**
 * 分页查询下上文
 *
 * @param page    分页参数
 * @param wrapper 查询条件
 * @param <T>     实体类
 */
public record SgFilterPageContext<T>(IPage<T> page, QueryWrapper<T> wrapper) {

    /**
     * 获取分页查询包装器
     *
     * @param pageReq   分页请求参数
     * @param columnMap 实体类列名映射
     * @param <T>       实体类
     * @return 包装器
     */
    public static <T> SgFilterPageContext<T> getContext(@NonNull SgFilterPage pageReq, @NonNull SgFilterPageColumnMap<T> columnMap) {
        // 设置分页参数
        IPage<T> page = new Page<>();
        page.setCurrent(pageReq.getPageStart());
        page.setSize(pageReq.getPageSize());

        // 设置查询参数
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        SgFilterReq filter = pageReq.getFilter();
        if (filter != null) {
            // EQ 查询
            List<String> eqKeys = SgFilterPageUtil.getValidColumnNames(filter.getEq(), columnMap);
            for (String eqKey : eqKeys) {
                // 取值
                Object eqValue = SgFilterPageUtil.mapTo(columnMap, eqKey, filter.getEq().get(eqKey));
                queryWrapper.eq(eqValue != null, eqKey, eqValue);
            }

            // IN 查询（需精确匹配列名才有效）
            List<String> inKeys = SgFilterPageUtil.getValidColumnNames(filter.getIn(), columnMap);
            for (String inKey : inKeys) {
                // 取默认 str 列表
                List<String> values = filter.getIn().get(inKey);
                // 值映射
                if (!CollectionUtils.isEmpty(values)) {
                    List<Object> valueList = values.stream().map(v -> SgFilterPageUtil.mapTo(columnMap, inKey, v)).filter(Objects::nonNull).toList();
                    // 每个值都需要转换成有效的形式
                    queryWrapper.in(inKey, valueList);
                }
            }

            // LIKE
            List<String> likeKeys = SgFilterPageUtil.getValidColumnNames(filter.getLike(), columnMap);
            for (String likeKey : likeKeys) {
                // 取值
                Object likeValue = SgFilterPageUtil.mapTo(columnMap, likeKey, filter.getLike().get(likeKey));
                queryWrapper.like(likeValue != null, likeKey, likeValue);
            }

            // LIKE_l
            List<String> likeLeftKeys = SgFilterPageUtil.getValidColumnNames(filter.getLikeLeft(), columnMap);
            for (String likeLeftKey : likeLeftKeys) {
                // 取值
                Object likeLeftValue = SgFilterPageUtil.mapTo(columnMap, likeLeftKey, filter.getLikeLeft().get(likeLeftKey));
                queryWrapper.likeLeft(likeLeftValue != null, likeLeftKey, likeLeftValue);
            }

            // LIKE_r
            List<String> likeRightKeys = SgFilterPageUtil.getValidColumnNames(filter.getLikeRight(), columnMap);
            for (String likeRightKey : likeRightKeys) {
                // 取值
                Object likeRightValue = SgFilterPageUtil.mapTo(columnMap, likeRightKey, filter.getLikeRight().get(likeRightKey));
                queryWrapper.likeRight(likeRightValue != null, likeRightKey, likeRightValue);
            }

            // BETWEEN
            List<String> betweenKeys = SgFilterPageUtil.getValidColumnNames(filter.getBetween(), columnMap);
            for (String betweenKey : betweenKeys) {
                // 取值
                List<String> betweenValue = filter.getBetween().get(betweenKey);
                if (!CollectionUtils.isEmpty(betweenValue) && betweenValue.size() == 2) {
                    // 值映射
                    List<Object> valueList = betweenValue.stream().map(v -> SgFilterPageUtil.mapTo(columnMap, betweenKey, v)).toList();
                    Object leftVal = valueList.get(0);
                    Object rightVal = valueList.get(1);
                    queryWrapper.between(leftVal != null && rightVal != null, betweenKey, leftVal, rightVal);
                    queryWrapper.ge(leftVal != null && rightVal == null, betweenKey, leftVal);
                    queryWrapper.le(leftVal == null && rightVal != null, betweenKey, rightVal);
                }
            }

            // BETWEEN_Lr
            List<String> betweenLeftKeys = SgFilterPageUtil.getValidColumnNames(filter.getBetweenEqLeft(), columnMap);
            for (String betweenLeftKey : betweenLeftKeys) {
                // 取值
                List<String> betweenLeftValue = filter.getBetweenEqLeft().get(betweenLeftKey);
                if (!CollectionUtils.isEmpty(betweenLeftValue) && betweenLeftValue.size() == 2) {
                    // 值映射
                    List<Object> valueList = betweenLeftValue.stream().map(v -> SgFilterPageUtil.mapTo(columnMap, betweenLeftKey, v)).toList();
                    Object leftVal = valueList.get(0);
                    Object rightVal = valueList.get(1);
                    // 每个值都需要转换成有效的形式
                    queryWrapper.ge(leftVal != null, betweenLeftKey, leftVal);
                    queryWrapper.lt(rightVal != null, betweenLeftKey, rightVal);
                }
            }

            // BETWEEN_lR
            List<String> betweenRightKeys = SgFilterPageUtil.getValidColumnNames(filter.getBetweenEqRight(), columnMap);
            for (String betweenRightKey : betweenRightKeys) {
                // 取值
                List<String> betweenRightValue = filter.getBetweenEqRight().get(betweenRightKey);
                if (!CollectionUtils.isEmpty(betweenRightValue) && betweenRightValue.size() == 2) {
                    // 值映射
                    List<Object> valueList = betweenRightValue.stream().map(v -> SgFilterPageUtil.mapTo(columnMap, betweenRightKey, v)).toList();
                    Object leftVal = valueList.get(0);
                    Object rightVal = valueList.get(1);
                    queryWrapper.gt(leftVal != null, betweenRightKey, leftVal);
                    queryWrapper.le(rightVal != null, betweenRightKey, rightVal);
                }
            }

            // ORDER
            List<String> orderKeys = filter.getOrder();
            if (!CollectionUtils.isEmpty(orderKeys)) {
                List<SgFilterPageOrder> orderList = orderKeys.stream().map(key -> SgFilterPageUtil.getOrderType(columnMap, key)).filter(Objects::nonNull).toList();
                for (SgFilterPageOrder order : orderList) {
                    switch (order.orderType()) {
                        case DESC:
                            queryWrapper.orderByDesc(order.columnName());
                            break;
                        case ASC:
                            queryWrapper.orderByAsc(order.columnName());
                            break;
                    }
                }
            }
        }
        return new SgFilterPageContext<>(page, queryWrapper);
    }
}