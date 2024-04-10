package com.capstone.goat.config;

import com.capstone.goat.exception.CustomAccessDeniedHandler;
import com.capstone.goat.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig  {

    private final TokenProvider tokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.
                csrf().disable()
                .headers().frameOptions().disable();
        http
                .authorizeRequests()
                .antMatchers("/","/index.js","/js/**","/css/**","/image/**","/h2-console/**").permitAll()
                .antMatchers(HttpMethod.PUT,"/api/users").hasRole("USER")
                .antMatchers(HttpMethod.DELETE,"/api/users").hasRole("USER")
                .antMatchers(HttpMethod.GET,"/api/users").hasRole("USER")
                .antMatchers("/api/users/club").hasRole("USER")
                .antMatchers("/api/users","/api/users/*").permitAll()
                .antMatchers(HttpMethod.GET,"/api/clubs/*").permitAll()
                .antMatchers("/api/clubs","/api/clubs/*").hasRole("USER")
                .anyRequest().permitAll();
        http
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception->exception.accessDeniedHandler(new CustomAccessDeniedHandler())
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }



}
