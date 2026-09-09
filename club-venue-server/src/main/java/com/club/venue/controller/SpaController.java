package com.club.venue.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

    @RequestMapping(value = {
        "/login",
        "/dashboard",
        "/club",
        "/venue",
        "/activity",
        "/facility",
        "/user",
        "/profile"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
