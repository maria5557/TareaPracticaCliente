package com.example.client.interceptor;


import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
/*
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String jwt = request.getHeader("jwt");

        if (jwt == null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write("JWT missing in request");
            return false;  // Si no hay JWT, bloqueamos la petición
        }

        try {
            String[] splited = jwt.split("\\.");
            byte[] content = Base64.getDecoder().decode(splited[1]);
            String jwtstring = new String(content, StandardCharsets.UTF_8);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(jwtstring);
            int age = node.get("age").asInt();

            if (age < 18) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write("Age is under 18, request denied");
                return false;  // Si la edad es menor de 18, denegamos la petición
            }

        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write("Invalid JWT");
            System.out.println(e.getMessage());
            return false;  // Si el JWT no es válido, lo rechazamos
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}

 */
