package org.zerock.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class CustomLoginSuccessHandler
        implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth)
            throws IOException, ServletException {

        log.warn("Login Success");

        List<String> roleNames = new ArrayList<>();

        auth.getAuthorities().forEach(authority -> {
            roleNames.add(authority.getAuthority());
        }); //forEach

        log.warn("ROLE NAMES : " + roleNames);

        if(roleNames.contains("ROLE_ADMIN")){

            res.sendRedirect("/sample/admin");
            return;
        } //if

        if(roleNames.contains("ROLE_MEMBER")){

            res.sendRedirect("/sample/member");
            return;
        } //if

        res.sendRedirect("/");
    } //onAuthenticationSuccess
} //end class
