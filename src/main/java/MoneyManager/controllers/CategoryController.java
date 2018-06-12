package MoneyManager.controllers;

import MoneyManager.models.Category;
import MoneyManager.models.Expense;
import MoneyManager.models.User;
import MoneyManager.models.data.CategoryDao;
import MoneyManager.models.data.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private UserDao userDao;

//    @RequestMapping(value="add", method = RequestMethod.GET)
//    public String displayAddForm(Model model, HttpSession httpSession){
//
//        Object userInSession = httpSession.getAttribute("user");
//
//        if(userInSession == null){
//            return "redirect:/user/login";
//        }
//
//        model.addAttribute("title", "Add Category");
//        model.addAttribute(new Category());
//
//
//        return "category/add";
//    }

    @RequestMapping(value="add", method = RequestMethod.POST)
    public String processAddForm(Model model, @ModelAttribute @Valid Category category, Errors errors,
                                 HttpSession httpSession){

        Object userInSession = httpSession.getAttribute("user");
        User user = userDao.findByUserName(userInSession.toString());

        if(errors.hasErrors()){
            model.addAttribute("title", "Add");
            model.addAttribute("categoryError", "Category must be between 3 and 15 characters");
            model.addAttribute("user", user);
            model.addAttribute(new Expense());
            model.addAttribute(new Category());
            return "/user/add-expense";
        }

        categoryDao.save(category);
        user.addCategory(category);
        userDao.save(user);

        return "redirect:/user/add-expense";

    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    public String displayRemoveCategoryForm(Model model, HttpSession httpSession) {

        Object userInSession = httpSession.getAttribute("user");

        if(userInSession == null){
            return "redirect:/user/login";
        }

        User user = userDao.findByUserName(userInSession.toString());

        model.addAttribute("user", user);
        model.addAttribute("title", "Remove Category");
        return "category/remove";
    }

    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public String processRemoveCategoryForm(@RequestParam(required = false) int[] categoryIds) {

        if(categoryIds == null){
            return "redirect:/home";
        }

        for (int categoryId : categoryIds) {
            categoryDao.deleteById(categoryId);
        }

        return "redirect:/home";
    }




}
