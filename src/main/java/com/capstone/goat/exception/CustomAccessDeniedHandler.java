package com.capstone.goat.exception;

import com.capstone.goat.dto.response.ResponseDto;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        String result = gson.toJson((new ResponseDto<>(403,"접근 권한이 없는 사용자입니다.")));
        response.setStatus(401);
        response.getWriter().write(result);
    }
}
