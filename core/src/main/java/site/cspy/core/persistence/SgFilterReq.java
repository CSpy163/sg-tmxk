package site.cspy.core.persistence;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.*;

/**
 * 分页查询参数。
 * <p>先全部转为 String，再通过相应的映射器转为可用的值。</p>
 * <strong>支持的操作</strong>
 * <ol>
 *     <li>EQ: 完全相等</li>
 *     <li>IN: 存在于集合</li>
 *     <li>LIKE: like-%keyword%、like_l-%keyword、like_r-keyword%</li>
 *     <li>BETWEEN: [between]、[between_Lr)、(between_lR]</li>
 *     <li>ORDER: UPPERCASE-DESC、lowercase-ASC</li>
 * </ol>
 */
@Data
//@Schema(requiredProperties = {"EQ", "IN", "LIKE", "LIKE_l", "LIKE_r", "BETWEEN", "BETWEEN_Lr", "BETWEEN_lR", "ORDER"})
public class SgFilterReq {
    /**
     * EQ：完全相等
     */
    @JsonProperty("EQ")
    private Map<String, String> eq = new HashMap<>();

    /**
     * 存在于集合
     */
    @JsonProperty("IN")
    private Map<String, List<String>> in = new HashMap<>();

    /**
     * LIKE：contains
     */
    @JsonProperty("LIKE")
    private Map<String, String> like = new HashMap<>();

    /**
     * %[keyword] 左 like
     */
    @JsonProperty("LIKE_l")
    private Map<String, String> likeLeft = new HashMap<>();

    /**
     * [keyword]% 右 like
     */
    @JsonProperty("LIKE_r")
    private Map<String, String> likeRight = new HashMap<>();

    /**
     * 存在于两个数据之间
     */
    @JsonProperty("BETWEEN")
    private Map<String, List<String>> between = new HashMap<>();

    /**
     * 左闭右开
     */
    @JsonProperty("BETWEEN_Lr")
    private Map<String, List<String>> betweenEqLeft = new HashMap<>();

    /**
     * 左开右闭
     */
    @JsonProperty("BETWEEN_lR")
    private Map<String, List<String>> betweenEqRight = new HashMap<>();

    /**
     * 列排序，根据列名在数组中的顺序进行排序。
     * 1. 列名大写表示从大到小排序，倒序。
     * 2. 列名小写表示从小到大排序，正序。
     */
    @JsonProperty("ORDER")
    private List<String> order = new ArrayList<>();

    /**
     * 获取参数值
     *
     * @param name 参数名
     * @return 值
     */
    public String getEqValue(String name) {
        return getEq().get(name);
    }

    /**
     * 获取参数值，如果为空，则设置默认值
     *
     * @param name       参数名
     * @param defaultVal 默认值
     * @return 值
     */
    public String getEqValueOrDefault(String name, String defaultVal) {
        String val = getEqValue(name);
        if (val != null) {
            return val;
        }
        getEq().put(name, defaultVal);
        return defaultVal;
    }

    /**
     * 设置 EQ 参数值
     *
     * @param name   参数名
     * @param newVal 新值
     */
    public void putEqValue(String name, String newVal) {
        getEq().put(name, newVal);
    }

    /**
     * 设置 IN 参数值
     *
     * @param name    参数名称
     * @param newVals 新值
     */
    public void putInValues(String name, List<String> newVals) {
        getIn().put(name, Optional.ofNullable(newVals).orElse(List.of()));
    }


}