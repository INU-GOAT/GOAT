package com.capstone.goat.exception;

import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.capstone.goat.dto.response.ResponseDto;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        String result = gson.toJson(new ResponseDto<>(401, "인증이 실패하였습니다."));
        response.setStatus(401);
        response.getWriter().write(result);
    }
}


