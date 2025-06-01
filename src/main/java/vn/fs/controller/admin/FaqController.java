package vn.fs.controller.admin;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.fs.entities.Category;
import vn.fs.entities.Faq;
import vn.fs.entities.Menu;
import vn.fs.entities.User;
import vn.fs.repository.CategoryRepository;
import vn.fs.repository.FaqRepository;
import vn.fs.repository.MenuRepository;
import vn.fs.repository.UserRepository;



@Controller
@RequestMapping("/admin")
public class FaqController {
    @Autowired
    FaqRepository faqRepository;

    @Autowired
    UserRepository userRepository;

    @ModelAttribute(value = "user")
    public User user(Model model, Principal principal, User user) {

        if (principal != null) {
            model.addAttribute("user", new User());
            user = userRepository.findByEmail(principal.getName());
            model.addAttribute("user", user);
        }

        return user;
    }

    // show list category - table list
    @ModelAttribute("faqs")
    public List<Faq> showFaq(Model model) {
        List<Faq> faqs = faqRepository.findAll();
        model.addAttribute("faqs", faqs);

        return faqs;
    }

    @GetMapping(value = "/faqs")
    public String faqs(Model model, Principal principal) {
        Faq faq = new Faq();
        model.addAttribute("faq", faq);

        return "admin/faqs";
    }

    // add category
    @PostMapping(value = "/addFaq")
    public String addCategory(@Validated @ModelAttribute("faq") Faq faq, ModelMap model,
                              BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "failure");

            return "admin/faqs";
        }
        faqRepository.save(faq);
//		menuRepository.save(menu);
        model.addAttribute("message", "successful!");

        return "redirect:/admin/faqs";
    }

    // get Edit category
    @GetMapping(value = "/editFaq/{id}")
    public String editCategory(@PathVariable("id") Long id, ModelMap model) {
        Faq faq = faqRepository.findById(id).orElse(null);

        model.addAttribute("faq", faq);

        return "admin/editFaq";
    }

    // delete category
    @GetMapping("/deleteFaq/{id}")
    public String delCategory(@PathVariable("id") Long id, Model model) {
        faqRepository.deleteById(id);

        model.addAttribute("message", "Delete successful!");

        return "redirect:/admin/faqs";
    }
}
