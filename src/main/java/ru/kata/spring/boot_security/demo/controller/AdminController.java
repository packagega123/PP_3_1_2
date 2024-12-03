package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.naming.AuthenticationException;
import java.security.Principal;
import java.util.List;
import java.util.Set;


@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public String showAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "index";
    }

    @GetMapping()
    public String showUser(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "admin";
    }

    @GetMapping("/new")
    public String addUserForm(@ModelAttribute("user") User user, Model model) {
        List<Role> roles = roleService.getRolesList();
        model.addAttribute("allRoles", roles);
        return "form";
    }

    @PostMapping()
    public String saveUser(@ModelAttribute("user") User user,
                           @RequestParam("authorities") List<String> values) throws AuthenticationException {
        Set<Role> roleSet = roleService.getSetOfRoles(values);
        user.setRoles(roleSet);
        userService.createUser(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute("user") User user,
                           @RequestParam("authorities") List<String> values) throws AuthenticationException {
        Set<Role> roleSet = roleService.getSetOfRoles(values);
        user.setRoles(roleSet);
        userService.updateUser(user);

        return "redirect:/admin/users";
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam(value = "id", required = true, defaultValue = "") long id) {
        User user = userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit")
    public String editUserForm(@RequestParam(value = "id", required = true, defaultValue = "") long id, Model model) {
        User user = userService.getUser(id);

        if (null == user) {
            return "redirect:/admin/users";
        }

        model.addAttribute("user", userService.getUser(id));
        //
        List<Role> roles = roleService.getRolesList();
        model.addAttribute("allRoles", roles);
        return "edit";
    }
}
