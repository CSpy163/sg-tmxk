package site.cspy.core.rest;


/**
 * 接口通用返回类。
 * v241008
 *
 * @param <T>  返回数据类型
 * @param code 返回码
 * @param msg  返回信息
 * @author cspy
 */
public record SgResponse<T>(T data, String code, String msg) {
}
