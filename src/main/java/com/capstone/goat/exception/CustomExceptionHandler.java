package com.capstone.goat.exception;


import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import com.capstone.goat.dto.response.ResponseDto;
import com.capstone.goat.exception.ex.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@CrossOrigin(origins = "*")
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseDto<Integer>> handleConstraintViolationException(ConstraintViolationException ex, HttpServletResponse response){
        log.error("유효성 검사 예외 발생 msg:{}",ex.getMessage());
        return new ResponseEntity<>(new ResponseDto<>(-1,ex.getMessage()),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseDto<Integer>> MyNotFoundException(CustomException ex,HttpServletResponse response){
        log.error("예외 발생 msg:{}",ex.getErrorCode().getMessage());
        return new ResponseEntity<>(new ResponseDto<>(-1,ex.getErrorCode().getMessage()),ex.getErrorCode().getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Integer>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,HttpServletResponse response){
        BindingResult bindingResult = ex.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String message = fieldError.getDefaultMessage();
        log.error("유효성 검사 예외 발생 msg:{}",message);
        return new ResponseEntity<>(new ResponseDto<>(-1,message),HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDto<Integer>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex,HttpServletResponse response) {
        String errorMessage = "요청한 JSON 데이터를 읽을 수 없습니다: " + ex.getMessage();
        return new ResponseEntity<>(new ResponseDto<>(-1,errorMessage), HttpStatus.BAD_REQUEST);
    }

}
