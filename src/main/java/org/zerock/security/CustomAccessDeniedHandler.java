package org.zerock.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Log4j2
public class CustomAccessDeniedHandler implements AccessDeniedHandler {


    @Override
    public void handle(HttpServletRequest req,
                       HttpServletResponse res,
                       org.springframework.security.access.AccessDeniedException e) throws IOException, ServletException {
        log.error("Access Denied Handler");

        log.error("Redirect...");

        res.sendRedirect("/accessError");
    } //handle
} //end class
