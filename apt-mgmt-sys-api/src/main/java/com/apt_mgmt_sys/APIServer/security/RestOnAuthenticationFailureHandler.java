package com.apt_mgmt_sys.APIServer.security;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


public class RestOnAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException e) throws IOException, ServletException {
        ObjectMapper mapper = new ObjectMapper();
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        res.setHeader("Access-Control-Allow-Credentials", "true");

        Map<String, Object> jsonMap = Utilities.buildAuthenticationResponse(
                false, null, null, null, HttpStatus.UNAUTHORIZED.value(), e.getMessage()
        );

        String json = mapper.writeValueAsString(jsonMap);
        res.getWriter().write(json);

    }
}
