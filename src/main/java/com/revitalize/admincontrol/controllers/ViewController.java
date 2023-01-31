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
        return mvEmpresaCad;    }


    @RequestMapping("/client")
    public ModelAndView clientProduct() {
        ModelAndView mvClientProduct = new ModelAndView();
        mvClientProduct.setViewName("/clienteproduto.html");
        return mvClientProduct;
    }
    @RequestMapping("/register")
    public ModelAndView registerUser() {
        ModelAndView mvRegisterUsert = new ModelAndView();
        mvRegisterUsert.setViewName("/register.html");
        return mvRegisterUsert;
    }

    @RequestMapping("/new-password")
    public ModelAndView newPassword() {
        ModelAndView mvNewPassword = new ModelAndView();
        mvNewPassword.setViewName("/forgot-password.html");
        return mvNewPassword;
    }

    @RequestMapping("/registernewuser")
    public ModelAndView newUser() {
        ModelAndView mvnewUser = new ModelAndView();
        mvnewUser.setViewName("/registernewuser.html");
        return mvnewUser;
    }

    @RequestMapping("/editregister")
    public ModelAndView editRegister() {
        ModelAndView mvEditRegister = new ModelAndView();
        mvEditRegister.setViewName("/editregister.html");
        return mvEditRegister;
    }

}
