package com.revitalize.admincontrol.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ViewController {

    @RequestMapping("/")
    public ModelAndView login() {
        ModelAndView mvLogin = new ModelAndView();
        mvLogin.setViewName("/login.html");
        return mvLogin;
    }


    @RequestMapping("/adduser")
    public ModelAndView addUsers() {
        ModelAndView mvRegisterUser = new ModelAndView();
        mvRegisterUser.setViewName("/register.html");
        return mvRegisterUser;
    }

    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView mvIndex = new ModelAndView();
        mvIndex.setViewName("/index.html");
        return mvIndex;
    }

    @RequestMapping("/home")
    public ModelAndView home() {
        ModelAndView mvHome = new ModelAndView();
        mvHome.setViewName("/home.html");
        return mvHome;
    }

    @RequestMapping("/addcompany")
    public ModelAndView empresaCad() {
        ModelAndView mvEmpresaCad = new ModelAndView();
        mvEmpresaCad.setViewName("/registercompany.html");
        return mvEmpresaCad;
    }
}
