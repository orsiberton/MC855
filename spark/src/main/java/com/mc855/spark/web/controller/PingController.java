package com.mc855.spark.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class PingController {

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String ping() {
        return "OK";
    }

}
