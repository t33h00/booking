package com.lotus.booking.Controllers;

import com.lotus.booking.Entity.Role;
import com.lotus.booking.Service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/save")
public class RoleApi {

    @Autowired
    private RoleService roleService;

    @PostMapping("/role")
    public String saveRole(@RequestBody Role role){
        roleService.addRole(role);
        return "Role saved!";
    }

}
