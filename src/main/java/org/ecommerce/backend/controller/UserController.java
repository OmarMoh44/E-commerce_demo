package org.ecommerce.backend.controller;

import lombok.RequiredArgsConstructor;
import org.ecommerce.backend.dto.UserMainView;
import org.ecommerce.backend.model.User;
import org.ecommerce.backend.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final ModelMapper modelMapper;
    private final UserService userService;

    @GetMapping
    public UserMainView getUser(@AuthenticationPrincipal User user){
        return modelMapper.map(user, UserMainView.class);
    }

    @PatchMapping
    public UserMainView updateUser(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> updates){
        return userService.updateUser(user, updates);
    }

    @DeleteMapping
    public void deleteUser(@AuthenticationPrincipal User user){
        userService.deleteUser(user);
    }


}

