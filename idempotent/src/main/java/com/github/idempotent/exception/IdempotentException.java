package com.github.idempotent.exception;

import com.github.idempotent.enums.IdempotentErrorCode;

/**
 * @author zhangmc
 * @create 2020-01-19 11:23
 */
public class IdempotentException extends RuntimeException {

    private IdempotentErrorCode idempotentErrorCode;

    public IdempotentErrorCode getIdempotentErrorCode(){
        return this.idempotentErrorCode;
    }

    public IdempotentException(IdempotentErrorCode idempotentErrorCode){
        super();
        this.idempotentErrorCode = idempotentErrorCode;
    }

    public IdempotentException(IdempotentErrorCode idempotentErrorCode, String message){
        super(message);
        this.idempotentErrorCode = idempotentErrorCode;
    }

}
