package com.common.base;

/**
 * Created by fangyitao on 2019/8/7.
 */
public enum ResponseCode {
    /** ��ȷ **/
    SUCCESS_CODE(200),
    /** �������� **/
    PARAM_ERROR_CODE(400),
    /** ���Ƶ��� **/
    LIMIT_ERROR_CODE(401),
    /** token ���� **/
    TOKEN_TIMEOUT_CODE(402),
    /** ��ֹ���� **/
    NO_AUTH_CODE(403),
    /** ��Դû�ҵ� **/
    NOT_FOUND(404),
    /** ���������� **/
    SERVER_ERROR_CODE(500),
    /** ���񽵼��� **/
    DOWNGRADE(406);
    private int code;
    public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }
    private ResponseCode(int code) {
        this.code = code;
    }
}
