package com.cottondealer.app.controller;

import com.cottondealer.app.model.GinSale;
import com.cottondealer.app.service.GinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/gins")
public class GinController {

    @Autowired
    private GinService ginService;

    @GetMapping
    public String listSales(Model model, @RequestParam(required = false) String keyword) {
        if (keyword != null) {
            model.addAttribute("sales", ginService.searchSales(keyword));
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("sales", ginService.getAllSales());
        }
        model.addAttribute("newSale", new GinSale());
        return "gin";
    }

    @PostMapping("/sale")
    public String saveSale(@ModelAttribute GinSale sale) {
        ginService.saveSale(sale);
        return "redirect:/gins";
    }
}
