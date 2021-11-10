package org.zerock.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Log4j2
public class CommonController {

    @GetMapping("/accessError")
    public void accessError(Authentication auth, Model model){
        log.debug("accessError({},model) invoked.", auth);

        log.info("access Denied: " + auth);

        model.addAttribute("msg", "Access Denied.");
    } //accessError


    @GetMapping("/customLogin")
    public void loginInput(String error, String logout, Model model){
        log.debug("loginInput(error,logout,model) invoked.");

        log.info("error : " + error);
        log.info("logout: " + logout);

        if (error != null) {
            model.addAttribute("error", "Login Error Check Your Account");
        } //if

        if (logout != null){
            model.addAttribute("logout", "Logout!!");
        } //if
    } //loginInput


    @GetMapping("/customLogout")
    public void logoutGET(){
        log.debug("logoutGET() invoked.");
    } //logoutGET
} //end class
