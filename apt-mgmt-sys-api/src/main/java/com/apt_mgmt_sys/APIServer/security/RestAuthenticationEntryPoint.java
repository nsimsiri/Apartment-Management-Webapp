package com.apt_mgmt_sys.APIServer.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(final HttpServletRequest req, final HttpServletResponse res, final AuthenticationException authException) throws IOException {
        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized in RestEntryPoint");
    }
}
