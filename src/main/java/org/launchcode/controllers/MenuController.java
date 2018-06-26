package org.launchcode.controllers;



import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value="cheese/menu")
public class MenuController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private MenuDao menuDao;

    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menus");


        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model){
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());


        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(Model model,
                                         @ModelAttribute @Valid Menu menu, Errors errors){
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }
        menuDao.save(menu);

        return "redirect:view/" + menu.getId();

    }

    @RequestMapping(value="view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(@PathVariable int menuId, Model model){

        Menu menu = menuDao.findOne(menuId);
        String menuName = menu.getName();

        model.addAttribute("menu", menu);
        model.addAttribute("title", menuName );

        return "menu/view";




    }
    @RequestMapping(value="add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(@PathVariable int menuId, Model model){
        Menu menu = menuDao.findOne(menuId);
        AddMenuItemForm form =   new AddMenuItemForm( menu, cheeseDao.findAll());
        model.addAttribute("form", form);
        model.addAttribute("title", "Add cheese to menu: " + form.getMenu().getName() );

        return "menu/add-item";
    }

    @RequestMapping(value="add-item", method = RequestMethod.POST)
    public String addItem(@ModelAttribute  @Valid AddMenuItemForm form,
                          Errors errors,@RequestParam int cheeseId, @RequestParam int menuId, Model model){

        if(errors.hasErrors()){
            Menu menu = menuDao.findOne(form.getMenuId());
            AddMenuItemForm formMenu =   new AddMenuItemForm( menu, cheeseDao.findAll());
            model.addAttribute("form", formMenu);
            model.addAttribute("title", "Add cheese to menu: " + formMenu.getMenu().getName() );

            return "menu/add-item/";

        }

        Cheese cheese = cheeseDao.findOne(cheeseId);
        Menu menu = menuDao.findOne(menuId);
        menu.addItem(cheese);
        menuDao.save(menu);

        return "redirect:view/" + menu.getId();

    }


}
