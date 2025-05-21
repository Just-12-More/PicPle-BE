package com.Just_112_More.PicPle.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class ExController {
    @GetMapping("/hello")
    public String hello(){
        return "hello spring";
    }
}
