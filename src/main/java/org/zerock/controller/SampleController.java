package org.zerock.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@RequestMapping("/sample/")
@Controller
public class SampleController {

    @GetMapping("/all")
    public void doAll(){
        log.info("do all can access everybody.");
    } //doAll

    @GetMapping("/member")
    public void doMember(){
        log.info("logined member.");
    } //doMember

    @GetMapping("/admin")
    public void doAdmin(){
        log.info("admin only.");
    } //doAdmin

} //end class
