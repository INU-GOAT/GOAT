package com.capstone.goat.dto.response;

import lombok.Getter;

@Getter
public class ResponseDto<T> {
    private T data;
    private String msg;

    public ResponseDto(T data, String msg){
        this.data =data;
        this.msg = msg;
    }
}
