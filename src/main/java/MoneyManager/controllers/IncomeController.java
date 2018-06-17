package MoneyManager.controllers;

import MoneyManager.models.Income;
import MoneyManager.models.User;
import MoneyManager.models.data.IncomeDao;
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

    @RequestMapping(value = "add-daily-income", method = RequestMethod.POST)
    public String processDailyAddForm(Model model, @ModelAttribute @Valid Income income,
                                      Errors errors, HttpSession httpSession){

        Object userInSession = httpSession.getAttribute("user");
        User user = userDao.findByUserName(userInSession.toString());

        if(errors.hasErrors()){
            model.addAttribute("title", "Add Daily Income");
            model.addAttribute("user", user);
            model.addAttribute(income);
            model.addAttribute("dailyIncomesTotal", income.calcDailyAmount(user.getIncomes()));
            model.addAttribute("numberOfDailyIncomes", income.getNumberOfDailyIncome(user.getIncomes()));
            return "income/viewDaily";
        }

        incomeDao.save(income);
        user.addIncome(income);
        userDao.save(user);

        model.addAttribute("title", "Add Daily Income");
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
        Income income = new Income();

        List<User> partners = user.getPartners();
        partners.addAll(user.getPartnersOf());

        ArrayList<Income> partnerIncomes = new ArrayList<>();

        double partnersDailyTotal = 0.0;
        int partnersNumberOfDailyIncomes = 0;

        if(partners.size() > 0){
            for(User partner : partners){
                partnersDailyTotal += income.calcDailyAmount(partner.getIncomes());
                partnersNumberOfDailyIncomes += income.getNumberOfDailyIncome(partner.getIncomes());
                partnerIncomes.addAll(partner.getIncomes());
            }
        }

        double dailyIncomeTotal = partnersDailyTotal + income.calcDailyAmount(user.getIncomes());
        int numberOfDailyIncomes = partnersNumberOfDailyIncomes + income.getNumberOfDailyIncome(user.getIncomes());

        model.addAttribute("title", "Daily Income");
        model.addAttribute("partnerIncomes", partnerIncomes);
        model.addAttribute("user", user);
        model.addAttribute(income);
        model.addAttribute("numberOfDailyIncomes", numberOfDailyIncomes);
        model.addAttribute("dailyIncomeTotal", dailyIncomeTotal);

        return "income/viewDaily";
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    public String displayRemoveIncomeForm(Model model, HttpSession httpSession) {

        Object userInSession = httpSession.getAttribute("user");

        if(userInSession == null){
            return "redirect:/user/login";
        }

        User user = userDao.findByUserName(userInSession.toString());

        model.addAttribute("user", user);
        model.addAttribute("title", "Remove Incomes");
        return "income/remove";
    }

    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public String processRemoveIncomeForm(@RequestParam(required = false) int[] incomeIds) {

        if(incomeIds == null){
            return "redirect:/home";
        }

        for (int incomeId : incomeIds) {
            incomeDao.deleteById(incomeId);
        }

        return "redirect:/home";
    }

    @RequestMapping(value = "edit-selection", method = RequestMethod.GET)
    public String displayEditSelectionForm(Model model, HttpSession httpSession){


        Object userInSession = httpSession.getAttribute("user");

        if(userInSession == null){
            return "redirect:/user/login";
        }

        User user = userDao.findByUserName(userInSession.toString());

        model.addAttribute("title", "Edit Income");
        model.addAttribute("user", user);

        return "income/edit-selection";
    }

    @RequestMapping(value = "edit/{incomeId}", method = RequestMethod.GET)
    public String processEditSelectionForm(Model model, @PathVariable int incomeId, HttpSession httpSession) {

        Object userInSession = httpSession.getAttribute("user");

        if(userInSession == null){
            return "redirect:/user/login";
        }

        User user = userDao.findByUserName(userInSession.toString());


        Optional<Income> incomeOptional = incomeDao.findById(incomeId);
        Income income = incomeOptional.get();

        model.addAttribute("title", "Edit Income");
        model.addAttribute(income);
        model.addAttribute("user", user);

        incomeDao.deleteById(incomeId);

        return "income/edit";
    }

    @RequestMapping(value = "edit", method = RequestMethod.POST)
    public String processEditForm(Model model, @ModelAttribute @Valid Income income, Errors errors,
                                  HttpSession httpSession) {

        Object userInSession = httpSession.getAttribute("user");
        User user = userDao.findByUserName(userInSession.toString());

        if(errors.hasErrors()){
            model.addAttribute(income);
            return "income/edit";
        }

        income.calcMonthlyAmount(income.getWeeklyAmount());
        incomeDao.save(income);

        user.addIncome(income);


        userDao.save(user);

        return "redirect:/home";

    }

}
