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

import javax.net.ssl.HttpsURLConnection;
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

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddForm(Model model, HttpSession httpSession){

        Object userInSession = httpSession.getAttribute("user");

        if(userInSession == null){
            return "redirect:/user/login";
        }

        model.addAttribute("title", "Expense");
        model.addAttribute("expenses", expenseDao.findAll());
        model.addAttribute("categories", categoryDao.findAll());
        model.addAttribute(new Expense());

        return "expense/add";

    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddForm(Model model, @ModelAttribute @Valid Expense expense, Errors errors,
                                 @RequestParam int categoryId, HttpSession httpSession){

        Object userInSession = httpSession.getAttribute("user");
        User user = userDao.findByUserName(userInSession.toString());

        if(errors.hasErrors()){
            model.addAttribute("title", "Expense");
            model.addAttribute("expenses", expenseDao.findAll());
            return "expense/add";
        }

        Optional<Category> categoryObject = categoryDao.findById(categoryId);
        Category category = categoryObject.get();

        expense.setCategory(category);

        expenseDao.save(expense);
        user.addItem(expense);
        userDao.save(user);


        return "redirect:/user/add-expense";

    }

}
