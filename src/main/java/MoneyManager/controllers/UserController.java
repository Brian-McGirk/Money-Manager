package MoneyManager.controllers;

import MoneyManager.models.Category;
import MoneyManager.models.Expense;
import MoneyManager.models.User;
import MoneyManager.models.data.CategoryDao;
import MoneyManager.models.data.ExpenseDao;
import MoneyManager.models.data.UserDao;
import MoneyManager.models.forms.AddUserExpenseForm;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ExpenseDao expenseDao;

    @Autowired
    private CategoryDao categoryDao;

    @RequestMapping(value = "logout")
    public String logout(HttpSession httpSession){
        httpSession.removeAttribute("user");
        return "redirect:login";
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String displayLogin(Model model){

        model.addAttribute(new User());

        return "user/login";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String processLogin(Model model, @ModelAttribute @Valid User user, Errors errors, HttpSession httpSession){

        User findByUserName = userDao.findByUserName(user.getUserName());
        Object userInSession = httpSession.getAttribute("user");

        if(userInSession != null){
            model.addAttribute("nameError", "A user is already logged in");
            return "user/login";
        }

        if(errors.hasErrors()){
            model.addAttribute(user);
            return "user/login";
        }

        if(findByUserName == null){
            model.addAttribute("nameError", "Username doesn't exist");
            return "user/login";
        }

//        findByUserName.getPassword().equals(user.getPassword())
        if(findByUserName != null && BCrypt.checkpw(user.getPassword(), findByUserName.getPw_hash())){
            httpSession.setAttribute("user", user.getUserName());
            return "redirect:/home";
        }

        model.addAttribute("passwordError", "Invalid password");

        return "user/login";
    }

    @RequestMapping(value = "register", method = RequestMethod.GET)
    public String displayRegister(Model model){

        model.addAttribute(new User());

        return "user/register";
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public String processRegister(Model model, @ModelAttribute @Valid User user, Errors errors, HttpSession httpSession){

        boolean userName = userDao.existsByUserName(user.getUserName());
        Object userInSession = httpSession.getAttribute("user");

        if(userInSession != null){
            model.addAttribute("nameError", "A user is already logged in");
            return "user/register";
        }

        if(userName){
            model.addAttribute("nameError", "Username already exists");
            return "user/register";
        }


        if(!user.getPassword().equals(user.getVerifyPassword())){
            model.addAttribute("matchError", "Passwords don't match");
        }

        if(errors.hasErrors()){
            model.addAttribute(user);
            return "user/register";
        }


        if(user.getPassword().equals(user.getVerifyPassword())){
            if(userInSession != null && userInSession.equals(user.getUserName())){
                model.addAttribute("nameError", "User already logged in");
                return "user/register";
            }

            user.setPw_hash(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            httpSession.setAttribute("user", user.getUserName());


            userDao.save(user);

            return "redirect:/home";
        }



        return "user/register";
    }

    @RequestMapping(value = "edit")
    public String displayEditLinks(Model model){

        return "user/edit/index";
    }


    @RequestMapping(value = "add-expense", method = RequestMethod.GET)
    public String displayAddForm(Model model, HttpSession httpSession){

        Object userInSession = httpSession.getAttribute("user");

        if(userInSession == null){
            return "redirect:/user/login";
        }

        User user = userDao.findByUserName(userInSession.toString());



        model.addAttribute("title", "Add");
        model.addAttribute("user", user);
        model.addAttribute(new Expense());
        model.addAttribute(new Category());


        return "user/add-expense";
    }

    @RequestMapping(value="add-expense", method = RequestMethod.POST)
    public String processAddForm(Model model, @RequestParam String categoryName, HttpSession httpSession,
                                 @ModelAttribute @Valid Expense expense, Errors errors){

        Object userInSession = httpSession.getAttribute("user");
        User user = userDao.findByUserName(userInSession.toString());


        if(errors.hasErrors() || categoryName == null || categoryName.length() > 15 || categoryName.length() < 3){
            if(categoryName == null || categoryName.length() > 15 || categoryName.length() < 3){
                model.addAttribute("categoryNameError", "Category must between 3 and 15 characters");
            }
            model.addAttribute("title", "Add");
            model.addAttribute("user", user);
            model.addAttribute("categoryAndExpenseError", true);
            model.addAttribute(expense);
            model.addAttribute(new Category());
            return "user/add-expense";
        }

        if(categoryDao.existsByName(categoryName)){
            Iterable<Category> categories = user.getCategories();
            for(Category userCategory : categories){
                if(userCategory.getName().toLowerCase().equals(categoryName.toLowerCase())){
                    model.addAttribute("title", "Add");
                    model.addAttribute("categoryNameError", "That category already exists");
                    model.addAttribute("user", user);
                    model.addAttribute(expense);
                    model.addAttribute(new Category());
                    return "/user/add-expense";
                }
            }

            Category category1 = categoryDao.findByName(categoryName);
            user.addCategory(category1);
            userDao.save(user);

            return "redirect:/user/add-expense";
        }

        Category category = new Category(categoryName);
        categoryDao.save(category);

        expense.setCategory(category);
        expense.calcMonthlyCost(expense.getWeeklyCost());

        expenseDao.save(expense);

        user.addItem(expense);
        user.addCategory(category);

        userDao.save(user);


        return "redirect:add-expense";

    }

    @RequestMapping(value = "settings", method = RequestMethod.GET)
    public String displaySettings(Model model, HttpSession httpSession){

        Object userInSession = httpSession.getAttribute("user");

        if(userInSession == null){
            return "redirect:/user/login";
        }

        User user = userDao.findByUserName(userInSession.toString());


        model.addAttribute("title", "Settings");


        return "user/settings";
    }

    @RequestMapping(value = "settings", method = RequestMethod.POST)
    public String processSettingsForm(Model model, HttpSession httpSession, @RequestParam String userName){

        Object userInSession = httpSession.getAttribute("user");
        User user = userDao.findByUserName(userInSession.toString());

        if(!userDao.existsByUserName(userName)){
        model.addAttribute("title", "Settings");
        model.addAttribute("userError", "That user doesn't exist");
        return "user/settings";
        }

        if(user.getUserName().equals(userName)){
        model.addAttribute("title", "Settings");
        model.addAttribute("userError", "You cannot partner with yourself");
        return "user/settings";
        }

        for(User partner : user.getPartners()){
        if(partner.getUserName().equals(userName)){
           model.addAttribute("title", "Settings");
           model.addAttribute("userError", "You are already partners with this user");
           return "user/settings";
        }
        }


        User partner = userDao.findByUserName(userName);

        partner.setRequestedBy(user.getUserName());

        userDao.save(partner);
//      user.addPartner(partner);
//      partner.addPartner(user);


//        userDao.save(user);


//      userDao.save(partner);

        model.addAttribute("title", "Settings");
        model.addAttribute("requestSent", "Your request has been sent!");


        return "user/settings";
    }


}

//    @RequestMapping(value = "add-expense", method = RequestMethod.GET)
//    public String displayAddExpense(Model model, HttpSession httpSession){
//
//        Object userInSession = httpSession.getAttribute("user");
//        User user = userDao.findByUserName(userInSession.toString());
//
//        AddUserExpenseForm form = new AddUserExpenseForm(expenseDao.findAll(), user);
//
//        model.addAttribute("form", form);
//
//        return "user/add-expense";
//    }
//
//    @RequestMapping(value = "add-expense", method = RequestMethod.POST)
//    public String processAddExpense(Model model, @ModelAttribute @Valid AddUserExpenseForm form,
//                                    Errors errors){
//
//        if(errors.hasErrors()){
//            model.addAttribute("form", form);
//            return "user/add-expense";
//        }
//
//        Expense expense = expenseDao.findById(form.getExpenseId()).get();
//        User user = userDao.findById(form.getUserId()).get();
//        user.addItem(expense);
//        userDao.save(user);
//
//        return "redirect:/home/view/" + user.getId();
//    }
