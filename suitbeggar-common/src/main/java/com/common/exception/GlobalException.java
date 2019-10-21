package com.common.exception;

/**
 * Created by fangyitao on 2019/8/7.
 * 自定义异常
 */
public class GlobalException extends Exception{
    private static final long serialVersionUID = -5701182284190108797L;

    private int code;
    public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

    public GlobalException(GlobalException e) {
        super(e.getMessage());
        this.code = e.getCode();
    }

    public GlobalException(String message) {
        super(message);
    }

    public GlobalException(String message, int code) {
        super(message);
        this.code = code;
    }

}
