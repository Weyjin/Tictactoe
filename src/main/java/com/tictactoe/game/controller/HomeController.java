package com.tictactoe.game.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    @RequestMapping("/")
    public ModelAndView index(){
        ModelAndView view=new ModelAndView();
        view.setViewName("home/index");
        return view;
    }
}
