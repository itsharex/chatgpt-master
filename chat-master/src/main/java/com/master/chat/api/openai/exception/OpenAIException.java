package com.master.chat.api.openai.exception;

import lombok.Getter;

/**
 * 描述： 异常
 *
 * @author https:www.unfbx.com
 *  2023-02-11
 */
@Getter
public class OpenAIException extends RuntimeException {

    private final String msg;
    private final int code;

    public OpenAIException(IError error) {
        super(error.msg());
        this.code = error.code();
        this.msg = error.msg();
    }

    public OpenAIException(String msg) {
        super(msg);
        this.code = CommonError.SYS_ERROR.code();
        this.msg = msg;
    }

    public OpenAIException() {
        super(CommonError.SYS_ERROR.msg());
        this.code = CommonError.SYS_ERROR.code();
        this.msg = CommonError.SYS_ERROR.msg();
    }
}
