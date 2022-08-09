package com.inlym.lifehelper.login.qrcode.exception;

/**
 * 无效扫码登录凭据异常
 *
 * <h2>主要用途
 * <p>当使用无效的小程序码（即“扫码登录凭据”）时，抛出此异常，要求客户端重新获取小程序码。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @date 2022/8/6
 * @since 1.3.0
 **/
public class InvalidQrCodeTicketException extends RuntimeException {
    public InvalidQrCodeTicketException(String message) {
        super(message);
    }

    public static InvalidQrCodeTicketException fromId(String id) {
        return new InvalidQrCodeTicketException("扫码登录凭据（ID：" + id + "）已失效！");
    }
}
