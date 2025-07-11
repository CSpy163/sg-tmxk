package site.cspy.core.rest;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import site.cspy.core.i18n.I18nItem;

import java.util.Locale;

import static site.cspy.core.i18n.I18nItem.t;
import static site.cspy.core.i18n.I18nManager.toI18nMessage;


/**
 * 响应助手，用于快速构建 SgResponse
 * v241008
 */
@Component
public class SgResponseHelper {

    public static final I18nItem LOGIN_SUCCESS = new I18nItem("LOGIN_SUCCESS", "登录成功。", "Login success.");
    public static final I18nItem LOGOUT_SUCCESS = new I18nItem("LOGOUT_SUCCESS", "登出成功。", "Logout success.");
    public static final I18nItem QUERY_SUCCESS = new I18nItem("QUERY_SUCCESS", "查询成功。", "Query success.");
    public static final I18nItem SAVE_SUCCESS = new I18nItem("SAVE_SUCCESS", "保存成功。", "Save success.");
    public static final I18nItem UPDATE_SUCCESS = new I18nItem("UPDATE_SUCCESS", "更新成功。", "Update success.");
    public static final I18nItem DELETE_SUCCESS = new I18nItem("DELETE_SUCCESS", "删除成功。", "Delete success.");
    public static final I18nItem RESET_SUCCESS = new I18nItem("RESET_SUCCESS", "重置成功。", "Reset success.");
    public static final I18nItem VERIFY_SUCCESS = new I18nItem("VERIFY_SUCCESS", "验证成功。", "Verify success.");
    public static final I18nItem CLEAR_SUCCESS = new I18nItem("CLEAR_SUCCESS", "清空成功。", "Clear success.");
    public static final I18nItem SUCCESS = new I18nItem("SUCCESS", "操作成功。", "Operate success.");
    public static final I18nItem FAIL = new I18nItem("ERROR", "操作失败！", "Operate fail.");
    public static final I18nItem NOT_FOUND_ERROR = new I18nItem("NOT_FOUND_ERROR", "资源未找到！", "Resource not found.");
    public static final I18nItem DELETE_COUNT_SUCCESS = new I18nItem("DELETE_COUNT_SUCCESS", "删除成功，共删除 {0} 条数据。", "Delete success, {0} data deleted.");


    public static <T> SgResponse<T> sendData(T data, @NonNull I18nItem item) {
        return new SgResponse<>(data, item.getCode(), toI18nMessage(item));
    }

    public static <T> SgResponse<T> sendData(T data, @NonNull I18nItem item, @NonNull Locale locale) {
        return new SgResponse<>(data, item.getCode(), toI18nMessage(item, locale));
    }


    public static <T> SgResponse<T> send(I18nItem item) {
        return sendData(null, item);
    }


    /**
     * 发送响应
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 响应
     */
    public static <T> SgResponse<T> sendQueryData(T data) {
        return sendData(data, t("GLOBAL.QUERY_SUCCESS"));
    }

    /**
     * 发送响应
     *
     * @param data   数据
     * @param locale 语言
     * @param <T>    数据类型
     * @return 响应
     */
    public static <T> SgResponse<T> sendQueryData(T data, @NonNull Locale locale) {
        return sendData(data, t("GLOBAL.QUERY_SUCCESS"), locale);
    }

    /**
     * 发送响应式响应
     *
     * @param dataMono 数据
     * @param <T>      数据类型
     * @return 响应
     */
    public static <T> Mono<SgResponse<T>> sendQueryDataFromMono(Mono<T> dataMono) {
        return dataMono.flatMap(data -> Mono.just(sendQueryData(data)));
    }

    /**
     * 发送响应式响应
     *
     * @param dataMono 数据
     * @param locale   语言
     * @param <T>      数据类型
     * @return 响应
     */
    public static <T> Mono<SgResponse<T>> sendQueryDataFromMono(Mono<T> dataMono, Locale locale) {
        return dataMono.flatMap(data -> Mono.just(sendQueryData(data, locale)));
    }

    public static <T> SgResponse<T> sendSuccessMsg(String msg) {
        return new SgResponse<>(null, "SUCCESS", msg);
    }

    public static <T> SgResponse<T> sendSuccess() {
        return sendData(null, t("GLOBAL.SUCCESS"));
    }

    /**
     * 快捷发送成功
     *
     * @param locale 语言
     * @param <T>    数据类型
     * @return 响应
     */
    public static <T> SgResponse<T> sendSuccess(@NonNull Locale locale) {
        return sendData(null, SUCCESS.withArgs(locale));
    }

    public static <T> SgResponse<T> sendFailMsg(String msg) {
        return new SgResponse<>(null, "FAIL", msg);
    }

    public static <T> SgResponse<T> sendErrorMsg(String msg) {
        return new SgResponse<>(null, "ERROR", msg);
    }

    public static <T> SgResponse<T> sendFail() {
        return sendData(null, t("GLOBAL.UNKNOWN_ERROR"));
    }

    /**
     * 快捷发送失败
     *
     * @param locale 语言
     * @param <T>    数据类型
     * @return 响应
     */
    public static <T> SgResponse<T> sendFail(@NonNull Locale locale) {
        return sendData(null, FAIL.withArgs(locale));
    }

}
