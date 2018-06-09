package MoneyManager.controllers;

import MoneyManager.models.Category;
import MoneyManager.models.Income;
import MoneyManager.models.User;
import MoneyManager.models.data.IncomeDao;
import MoneyManager.models.data.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;


@Controller
@RequestMapping("income")
public class IncomeController {

    @Autowired
    private IncomeDao incomeDao;

    @Autowired
    private UserDao userDao;

    private Object userInSession;

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddForm(Model model, HttpSession httpSession){

        userInSession = httpSession.getAttribute("user");

        if(userInSession == null){
            return "redirect:/user/login";
        }

        User user = userDao.findByUserName(userInSession.toString());

        model.addAttribute("title", "Add Income");
        model.addAttribute("user", user);
        model.addAttribute(new Income());

        return "income/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddForm(Model model, @ModelAttribute @Valid Income income, Errors errors,
                                 HttpSession httpSession){

        Object userInSession = httpSession.getAttribute("user");
        User user = userDao.findByUserName(userInSession.toString());

        if(errors.hasErrors()){
            model.addAttribute("title", "Add Income");
            model.addAttribute("user", user);
            model.addAttribute(income);
            return "/income/add";
        }

        income.calcMonthlyAmount(income.getWeeklyAmount());

        incomeDao.save(income);

        user.addIncome(income);
        userDao.save(user);

        return "redirect:/income/add";
    }


}
