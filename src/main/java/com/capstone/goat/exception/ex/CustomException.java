package com.capstone.goat.exception.ex;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private CustomErrorCode errorCode;

    public CustomException(CustomErrorCode errorCode){
        this.errorCode = errorCode;
    }
}
