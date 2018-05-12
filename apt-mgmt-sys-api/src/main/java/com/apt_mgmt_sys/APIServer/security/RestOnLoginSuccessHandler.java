package com.apt_mgmt_sys.APIServer.security;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.models.User;
import com.apt_mgmt_sys.APIServer.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RestOnLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private UserService userService;
    public RestOnLoginSuccessHandler(UserService userService){
        this.userService = userService;
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse res,
                                        Authentication auth) throws ServletException, IOException {
        System.out.format("==[LOGIN COMPLETE]==\n<authentication>: %s\n", auth);
        System.out.println("\n" + auth.isAuthenticated());

        res.setStatus(HttpStatus.OK.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        res.setHeader("Access-Control-Allow-Credentials", "true");

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonMap = null;
        try{
            String username = auth.getName();
            User user = userService.getUserByUsername(username);
            UserDetails userDetail = (UserDetails)auth.getPrincipal();
            List<Role> roles = userDetail.getAuthorities()
                    .stream()
                    .map(x -> Role.valueOf(x.getAuthority())).collect(Collectors.toList());
            jsonMap = Utilities.buildAuthenticationResponse(
                    auth.isAuthenticated(), auth.getName(),
                    user,roles, HttpStatus.OK.value(), null
            );
        } catch (Exception e){
            jsonMap = Utilities.buildAuthenticationResponse(
                    false, auth.getName(),
                    null, null,HttpStatus.UNAUTHORIZED.value(), e.getMessage()
            );
        } finally {
            res.getWriter().write(mapper.writeValueAsString(jsonMap));
            clearAuthenticationAttributes(req);
        }

    }
}
