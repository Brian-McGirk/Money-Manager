package MoneyManager.controllers;

import MoneyManager.models.Expense;
import MoneyManager.models.Income;
import MoneyManager.models.User;
import MoneyManager.models.data.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("home")
public class HomeController {

    @Autowired
    private UserDao userDao;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String displayHome(Model model, HttpSession httpSession){

        Object userInSession = httpSession.getAttribute("user");

        if(userInSession == null){
            return "redirect:/user/login";
        }

        User user = userDao.findByUserName(userInSession.toString());
        Expense expense = new Expense();
        Income income = new Income();


        model.addAttribute("monthlyExpenseTotal", expense.calcMonthlyTotal(user.getExpenses()));
        model.addAttribute("monthlyIncomeTotal", income.calcMonthlyTotal(user.getIncomes()));
        model.addAttribute("dailyExpenseTotal", expense.calcDailyTotal(user.getExpenses()));
        model.addAttribute("dailyIncomeTotal", income.calcDailyAmount(user.getIncomes()));
        model.addAttribute("numberOfDailyExpenses", expense.getNumberOfDailyExpense(user.getExpenses()));
        model.addAttribute("numberOfDailyIncomes", income.getNumberOfDailyIncome(user.getIncomes()));
        model.addAttribute("user", user);


        return "home/index";

    }

//    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
//    public String displayView(Model model, @PathVariable int id){
//
//        model.addAttribute("user", userDao.findById(id).get());
//
//        return "home/view";
//
//    }

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
