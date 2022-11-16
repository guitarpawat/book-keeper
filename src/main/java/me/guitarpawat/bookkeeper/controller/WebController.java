package me.guitarpawat.bookkeeper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index(ModelMap model) {
        model.addAttribute("name", "กีต้าร์");
        return "index";
    }
}
