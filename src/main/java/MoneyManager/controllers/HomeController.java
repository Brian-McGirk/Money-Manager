package MoneyManager.controllers;

import MoneyManager.models.Expense;
import MoneyManager.models.Income;
import MoneyManager.models.User;
import MoneyManager.models.data.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("home")
public class HomeController {

    @Autowired
    private UserDao userDao;

    private String requestedBy;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String displayHome(Model model, HttpSession httpSession) {

        Object userInSession = httpSession.getAttribute("user");

        if (userInSession == null) {
            return "redirect:/user/login";
        }

        User user = userDao.findByUserName(userInSession.toString());
        Expense expense = new Expense();
        Income income = new Income();

        for (String name : user.getRequestedBy()) {
            if (name != null) {
                requestedBy = name;
                model.addAttribute("requestedBy", requestedBy);
                break;
            }
        }


        List<User> partners = user.getPartners();
        partners.addAll(user.getPartnersOf());

        ArrayList<Expense> partnerExpenses = new ArrayList<>();
        ArrayList<Income> partnerIncomes = new ArrayList<>();


        double partnersDailyExpenseTotal = 0.0;
        int partnersNumberOfDailyExpenses = 0;

        double partnersDailyIncomeTotal = 0.0;
        int partnersNumberOfDailyIncomes = 0;

        if (partners.size() > 0) {
            for (User partner : partners) {

                partnersDailyExpenseTotal += expense.calcDailyTotal(partner.getExpenses());
                partnersNumberOfDailyExpenses += expense.getNumberOfDailyExpense(partner.getExpenses());
                partnerExpenses.addAll(partner.getExpenses());

                partnersDailyIncomeTotal += income.calcDailyAmount(partner.getIncomes());
                partnersNumberOfDailyIncomes += income.getNumberOfDailyIncome(partner.getIncomes());
                partnerIncomes.addAll(partner.getIncomes());
            }
        }

        double dailyExpenseTotal = partnersDailyExpenseTotal + expense.calcDailyTotal(user.getExpenses());
        int numberOfDailyExpenses = partnersNumberOfDailyExpenses + expense.getNumberOfDailyExpense(user.getExpenses());

        double dailyIncomeTotal = partnersDailyIncomeTotal + income.calcDailyAmount(user.getIncomes());
        int numberOfDailyIncomes = partnersNumberOfDailyIncomes + income.getNumberOfDailyIncome(user.getIncomes());


        model.addAttribute("partnerExpenses", partnerExpenses);
        model.addAttribute("partnerIncomes", partnerIncomes);


        model.addAttribute("monthlyExpenseTotal", expense.calcMonthlyTotal(user.getExpenses()) + expense.calcMonthlyTotal(partnerExpenses));
        model.addAttribute("monthlyIncomeTotal", income.calcMonthlyTotal(user.getIncomes()) + income.calcMonthlyTotal(partnerIncomes));

        model.addAttribute("dailyExpenseTotal", dailyExpenseTotal);
        model.addAttribute("dailyIncomeTotal", dailyIncomeTotal);

        model.addAttribute("numberOfDailyExpenses", numberOfDailyExpenses);
        model.addAttribute("numberOfDailyIncomes", numberOfDailyIncomes);
        model.addAttribute("user", user);
        model.addAttribute("title", "Home");


        return "home/index";

    }

    @RequestMapping(value = "deny", method = RequestMethod.GET)
    public String denyPartner(Model model, HttpSession httpSession) {

        Object userInSession = httpSession.getAttribute("user");

        if (userInSession == null) {
            return "redirect:/user/login";
        }

        User user = userDao.findByUserName(userInSession.toString());

        user.getRequestedBy().remove(requestedBy);

        userDao.save(user);

        return "redirect:";
    }

    @RequestMapping(value = "accept", method = RequestMethod.GET)
    public String allowPartner(Model model, HttpSession httpSession) {

        Object userInSession = httpSession.getAttribute("user");

        if (userInSession == null) {
            return "redirect:/user/login";
        }

        User user = userDao.findByUserName(userInSession.toString());

        User partner = userDao.findByUserName(requestedBy);

        user.getRequestedBy().remove(requestedBy);

        user.addPartnerOf(partner);

        userDao.save(user);

        return "redirect:";
    }
}
