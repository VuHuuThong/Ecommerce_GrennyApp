package vn.fs.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.fs.entities.Comment;
import vn.fs.entities.Product;
import vn.fs.entities.User;
import vn.fs.repository.CommentRepository;
import vn.fs.repository.MenuRepository;
import vn.fs.repository.UserRepository;

import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/admin")
public class CmtController {
    @Autowired
    MenuRepository menuRepository;
    @Autowired
    CommentRepository commentRepository;

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
    @ModelAttribute("comments")
    public List<Comment> showCategory(Model model) {
        List<Comment> comments = commentRepository.findAll();
        model.addAttribute("comments", comments);
        return comments;
    }

    @GetMapping(value = "/comments")
    public String categories(Model model, Principal principal) {
        Comment comment = new Comment();
        model.addAttribute("comment", comment);

        return "admin/comment";
    }
    // get Edit brand
    @GetMapping(value = "/detailComment/{id}")
    public String detailComment(@PathVariable("id") Long id, ModelMap model) {
        Comment comment = commentRepository.findById(id).orElse(null);

        model.addAttribute("comment", comment);

        return "admin/commentDetail";
    }

     //delete category
    @GetMapping("/deleteComment/{id}")
    public String delCategory(@PathVariable("id") Long id, Model model) {
        commentRepository.deleteById(id);

        model.addAttribute("message", "Delete successful!");

        return "redirect:/admin/comments";
    }
}
