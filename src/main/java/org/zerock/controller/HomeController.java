package org.zerock.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Log4j2

@Controller
public class HomeController {
    @RequestMapping("/")
    public String index(){

        return "index";
    } //homeController

} //end class
