package MoneyManager.controllers;

import MoneyManager.models.User;
import MoneyManager.models.data.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;


@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserDao userDao;

    @RequestMapping(value = "logout")
    public String logout(HttpSession httpSession){
        httpSession.removeAttribute("user");
        return "redirect:login";
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String displayLogin(Model model){

        model.addAttribute(new User());

        return "user/login/index";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String processLogin(Model model, @ModelAttribute @Valid User user, Errors errors, HttpSession httpSession){

        User findByUserName = userDao.findByUserName(user.getUserName());
        Object userInSession = httpSession.getAttribute("user");


        if(errors.hasErrors()){
            model.addAttribute(user);
            return "user/login/index";
        }

        if(findByUserName != null && findByUserName.getPassword().equals(user.getPassword())){
            httpSession.setAttribute("user", user.getUserName());
            return "redirect:home";
        }

        model.addAttribute("passwordError", "Invalid password");

        return "user/login/index";
    }

    @RequestMapping(value = "register", method = RequestMethod.GET)
    public String displayRegister(Model model){

        model.addAttribute(new User());

        return "user/register/index";
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ModelAndView processRegister(Model model, @ModelAttribute @Valid User user, Errors errors, HttpSession httpSession){

        boolean userName = userDao.existsByUserName(user.getUserName());
        Object userInSession = httpSession.getAttribute("user");


        if(userName){
            model.addAttribute("nameError", "Username already exists");
            return new ModelAndView("/user/register/index");
        }

        if(!user.getPassword().equals(user.getVerifyPassword())){
            model.addAttribute("matchError", "Passwords don't match");
        }

        if(errors.hasErrors()){
            model.addAttribute(user);
            return new ModelAndView("/user/register/index");
        }

        if(user.getPassword().equals(user.getVerifyPassword())){
            if(userInSession == null){
                httpSession.setAttribute("user", user.getUserName());
            }

//            userDao.save(user);

            return new ModelAndView("redirect:/home");
        }



        return new ModelAndView("user/register/index");
    }

//    @RequestMapping(value = "home", method = RequestMethod.GET)
//    public String displayHome(Model model, HttpSession httpSession){
//
//        Object userInSession = httpSession.getAttribute("user");
//
//        if(userInSession == null){
//            return "redirect:login";
//        }
//
//        model.addAttribute("userName", userInSession);
//
//        return "home/index";
//
//    }
//
//    @RequestMapping(value = "home", method = RequestMethod.POST)
//    public String processHome(Model model, HttpSession httpSession){
//
//        Object userInSession = httpSession.getAttribute("user");
//
//        if(userInSession == null){
//            return "redirect:login";
//        }
//
//        model.addAttribute("userName", userInSession);
//
//        return "home/index";
//
//    }




}
