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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("expense")
public class ExpenseController {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ExpenseDao expenseDao;



//    @RequestMapping(value = "add", method = RequestMethod.GET)
//    public String displayAddForm(Model model, HttpSession httpSession){
//
//        Object userInSession = httpSession.getAttribute("user");
//
//        if(userInSession == null){
//            return "redirect:/user/login";
//        }
//
//        model.addAttribute("title", "Expense");
//        model.addAttribute("expenses", expenseDao.findAll());
//        model.addAttribute("categories", categoryDao.findAll());
//        model.addAttribute(new Expense());
//
//        return "expense/add";
//
//    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddForm(Model model, @ModelAttribute @Valid Expense expense, Errors errors,
                                 @RequestParam int categoryId, HttpSession httpSession){

        Object userInSession = httpSession.getAttribute("user");
        User user = userDao.findByUserName(userInSession.toString());

        if(errors.hasErrors()){
            model.addAttribute("title", "Add");
            model.addAttribute("user", user);
            model.addAttribute(expense);
            model.addAttribute("expenseError", true);
            model.addAttribute(new Category());
            return "/user/add-expense";
        }

        Optional<Category> categoryObject = categoryDao.findById(categoryId);
        Category category = categoryObject.get();

        expense.setCategory(category);

        expense.calcMonthlyCost(expense.getWeeklyCost());

        expenseDao.save(expense);
        user.addItem(expense);
        userDao.save(user);


        return "redirect:/user/add-expense";

    }

//    @RequestMapping(value = "add-daily-expense", method = RequestMethod.GET)
//    public String displayDailyAddForm(Model model, HttpSession httpSession){
//
//        Object userInSession = httpSession.getAttribute("user");
//
//        if(userInSession == null){
//            return "redirect:/user/login";
//        }
//
//        User user = userDao.findByUserName(userInSession.toString());
//
//
//        model.addAttribute("title", "Add Daily Expense");
//        model.addAttribute("user", user);
//        model.addAttribute(new Expense());
//
//        return "expense/add-daily";
//    }


    @RequestMapping(value = "add-daily-expense", method = RequestMethod.POST)
    public String processDailyAddForm(Model model, @ModelAttribute @Valid Expense expense,
                                      Errors errors, HttpSession httpSession){

        Object userInSession = httpSession.getAttribute("user");
        User user = userDao.findByUserName(userInSession.toString());

        if(errors.hasErrors()){
            model.addAttribute("title", "Add Daily Expense");
            model.addAttribute("user", user);
            model.addAttribute(expense);
            model.addAttribute("dailyExpenseTotal", expense.calcDailyTotal(user.getExpenses()));
            model.addAttribute("numberOfDailyExpenses", expense.getNumberOfDailyExpense(user.getExpenses()));
            return "expense/viewDaily";
        }



        expenseDao.save(expense);
        user.addItem(expense);
        userDao.save(user);

        model.addAttribute("title", "Add Daily Expense");
        model.addAttribute("user", user);

        return "redirect:view-daily";
    }

    @RequestMapping(value = "view-daily", method = RequestMethod.GET)
    public String viewDailyExpense(Model model, HttpSession httpSession){

        Object userInSession = httpSession.getAttribute("user");

        if(userInSession == null){
            return "redirect:/user/login";
        }

        User user = userDao.findByUserName(userInSession.toString());
        Expense expense = new Expense();

        model.addAttribute("user", user);
        model.addAttribute(expense);
        model.addAttribute("numberOfDailyExpenses", expense.getNumberOfDailyExpense(user.getExpenses()));
        model.addAttribute("dailyExpenseTotal", expense.calcDailyTotal(user.getExpenses()));

        return "expense/viewDaily";
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    public String displayRemoveCheeseForm(Model model, HttpSession httpSession) {

        Object userInSession = httpSession.getAttribute("user");

        if(userInSession == null){
            return "redirect:/user/login";
        }

        User user = userDao.findByUserName(userInSession.toString());

        model.addAttribute("user", user);
        model.addAttribute("title", "Remove Expenses");
        return "expense/remove";
    }

    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public String processRemoveCheeseForm(@RequestParam(required = false) int[] expenseIds) {

        if(expenseIds == null){
            return "redirect:/home";
        }

        for (int expenseId : expenseIds) {
            expenseDao.deleteById(expenseId);
        }

        return "redirect:/home";
    }


}
