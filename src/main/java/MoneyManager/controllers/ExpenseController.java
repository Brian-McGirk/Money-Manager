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
@RequestMapping("expense")
public class ExpenseController {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ExpenseDao expenseDao;

    private ArrayList<Expense> expenses = new ArrayList<>();

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddForm(Model model, @ModelAttribute @Valid Expense expense, Errors errors,
                                 @RequestParam int categoryId, HttpSession httpSession) {

        Object userInSession = httpSession.getAttribute("user");
        User user = userDao.findByUserName(userInSession.toString());

        if (errors.hasErrors()) {
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

    @RequestMapping(value = "add-daily-expense", method = RequestMethod.POST)
    public String processDailyAddForm(Model model, @ModelAttribute @Valid Expense expense,
                                      Errors errors, HttpSession httpSession) {

        Object userInSession = httpSession.getAttribute("user");
        User user = userDao.findByUserName(userInSession.toString());

        if (errors.hasErrors()) {
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
    public String viewDailyExpense(Model model, HttpSession httpSession) {

        Object userInSession = httpSession.getAttribute("user");

        if (userInSession == null) {
            return "redirect:/user/login";
        }

        User user = userDao.findByUserName(userInSession.toString());
        Expense expense = new Expense();


        List<User> partners = user.getPartners();
        partners.addAll(user.getPartnersOf());

        ArrayList<Expense> partnerExpenses = new ArrayList<>();

        double partnersDailyTotal = 0.0;
        int partnersNumberOfDailyExpenses = 0;

        if (partners.size() > 0) {
            for (User partner : partners) {
                partnersDailyTotal += expense.calcDailyTotal(partner.getExpenses());
                partnersNumberOfDailyExpenses += expense.getNumberOfDailyExpense(partner.getExpenses());
                partnerExpenses.addAll(partner.getExpenses());
            }
        }

        double dailyExpenseTotal = partnersDailyTotal + expense.calcDailyTotal(user.getExpenses());
        int numberOfDailyExpenses = partnersNumberOfDailyExpenses + expense.getNumberOfDailyExpense(user.getExpenses());


        model.addAttribute("title", "Daily Expenses");
        model.addAttribute("partnerExpenses", partnerExpenses);
        model.addAttribute("user", user);
        model.addAttribute(expense);
        model.addAttribute("numberOfDailyExpenses", numberOfDailyExpenses);
        model.addAttribute("dailyExpenseTotal", dailyExpenseTotal);

        return "expense/viewDaily";
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    public String displayRemoveExpenseForm(Model model, HttpSession httpSession) {

        Object userInSession = httpSession.getAttribute("user");

        if (userInSession == null) {
            return "redirect:/user/login";
        }

        User user = userDao.findByUserName(userInSession.toString());

        model.addAttribute("title", "Remove Expense");
        model.addAttribute("user", user);
        model.addAttribute("title", "Remove Expenses");
        return "expense/remove";
    }

    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public String processRemoveExpenseForm(@RequestParam(required = false) int[] expenseIds) {

        if (expenseIds == null) {
            return "redirect:/home";
        }

        for (int expenseId : expenseIds) {
            expenseDao.deleteById(expenseId);
        }

        return "redirect:/home";
    }

    @RequestMapping(value = "edit-selection", method = RequestMethod.GET)
    public String displayEditSelectionForm(Model model, HttpSession httpSession) {


        Object userInSession = httpSession.getAttribute("user");

        if (userInSession == null) {
            return "redirect:/user/login";
        }

        User user = userDao.findByUserName(userInSession.toString());

        model.addAttribute("title", "Edit Expense");
        model.addAttribute("user", user);

        return "expense/edit-selection";
    }

    @RequestMapping(value = "edit/{expenseId}", method = RequestMethod.GET)
    public String processEditSelectionForm(Model model, @PathVariable int expenseId, HttpSession httpSession) {

        Object userInSession = httpSession.getAttribute("user");

        if (userInSession == null) {
            return "redirect:/user/login";
        }

        User user = userDao.findByUserName(userInSession.toString());

        Optional<Expense> expenseOptional = expenseDao.findById(expenseId);
        Expense expense = expenseOptional.get();

        model.addAttribute("title", "Edit Expense");
        model.addAttribute(expense);
        model.addAttribute("user", user);

        expenseDao.deleteById(expenseId);

        return "expense/edit";
    }

    @RequestMapping(value = "edit", method = RequestMethod.POST)
    public String processEditForm(Model model, @ModelAttribute @Valid Expense expense, Errors errors,
                                  @RequestParam int categoryId, HttpSession httpSession) {

        Object userInSession = httpSession.getAttribute("user");
        User user = userDao.findByUserName(userInSession.toString());

        if (errors.hasErrors()) {
            model.addAttribute(expense);
            return "expense/edit";
        }

        Optional<Category> categoryObject = categoryDao.findById(categoryId);
        Category category = categoryObject.get();

        expense.setCategory(category);
        expense.calcMonthlyCost(expense.getWeeklyCost());

        expenseDao.save(expense);

        user.addItem(expense);

        userDao.save(user);

        return "redirect:/home";

    }

    @RequestMapping(value = "view-monthly")
    public String viewMonthlyExpense(Model model, HttpSession httpSession){

        Object userInSession = httpSession.getAttribute("user");

        if (userInSession == null) {
            return "redirect:/user/login";
        }

        User user = userDao.findByUserName(userInSession.toString());

        model.addAttribute("title", "Monthly Expense");
        model.addAttribute("user", user);

        return "expense/viewMonthly";
    }
}
