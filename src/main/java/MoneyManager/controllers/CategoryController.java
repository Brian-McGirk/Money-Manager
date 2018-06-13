package MoneyManager.controllers;

import MoneyManager.models.Category;
import MoneyManager.models.Expense;
import MoneyManager.models.User;
import MoneyManager.models.data.CategoryDao;
import MoneyManager.models.data.ExpenseDao;
import MoneyManager.models.data.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ExpenseDao expenseDao;

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

        if(categoryDao.existsByName(category.getName())){
            Iterable<Category> categories = user.getCategories();
            for(Category userCategory : categories){
                if(userCategory.getName().toLowerCase().equals(category.getName().toLowerCase())){
                    model.addAttribute("title", "Add");
                    model.addAttribute("categoryError", "That category already exists");
                    model.addAttribute("user", user);
                    model.addAttribute(new Expense());
                    model.addAttribute(new Category());
                    return "/user/add-expense";
                }
            }

            Category category1 = categoryDao.findByName(category.getName());
            user.addCategory(category1);
            userDao.save(user);

            return "redirect:/user/add-expense";

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
    public String processRemoveCategoryForm(@RequestParam(required = false) int[] categoryIds, HttpSession httpSession) {

        if(categoryIds == null){
            return "redirect:/home";
        }

        Object userInSession = httpSession.getAttribute("user");
        User user = userDao.findByUserName(userInSession.toString());

        List<Expense> expenses = user.getExpenses();

        for (int categoryId : categoryIds) {
            for(Expense expense : expenses){
                if(categoryId == expense.getCategory().getId()){
                    expenseDao.deleteById(expense.getId());
                }
            }
            categoryDao.deleteById(categoryId);
        }

        return "redirect:/home";
    }

}
