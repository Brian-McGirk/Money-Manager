package MoneyManager.controllers;

import MoneyManager.models.Category;
import MoneyManager.models.data.CategoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryDao categoryDao;

    @RequestMapping(value="add", method = RequestMethod.GET)
    public String displayAddForm(Model model, HttpSession httpSession){

        Object userInSession = httpSession.getAttribute("user");

        if(userInSession == null){
            return "redirect:/user/login";
        }

        model.addAttribute("title", "Add Category");
        model.addAttribute(new Category());


        return "category/add";
    }

    @RequestMapping(value="add", method = RequestMethod.POST)
    public String processAddForm(Model model, @ModelAttribute @Valid Category category, Errors errors){

        if(errors.hasErrors()){
            model.addAttribute("title", "Add Category");
            return "category/add";
        }

        categoryDao.save(category);

        return "redirect:add";

    }


}
