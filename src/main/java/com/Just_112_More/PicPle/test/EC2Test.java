package com.Just_112_More.PicPle.test1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EC2Test {

    @GetMapping("/test")
    public String ping(){
        return "connect success";
    }

}
