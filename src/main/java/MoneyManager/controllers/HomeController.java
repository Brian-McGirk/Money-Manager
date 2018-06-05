package MoneyManager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("home")
public class HomeController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String displayHome(Model model, HttpSession httpSession){

        Object userInSession = httpSession.getAttribute("user");

        if(userInSession == null){
            return "redirect:user/login";
        }

        model.addAttribute("userName", userInSession);

        return "home/index";

    }

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
