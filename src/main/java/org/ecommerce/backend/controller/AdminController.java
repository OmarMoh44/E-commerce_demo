package org.ecommerce.backend.controller;

import lombok.RequiredArgsConstructor;
import org.ecommerce.backend.dto.UserMainView;
import org.ecommerce.backend.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-sellers-buyers")
    public List<UserMainView> findAllUsers(@RequestParam(defaultValue = "10") int size,
                                           @RequestParam(defaultValue = "0") int page){
        System.out.printf("page: %d, size: %d%n", page, size);
        return adminService.findAllUsers(page, size);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user-details/{id}")
    public UserMainView getUserDetailsById(@PathVariable Long id){
        return adminService.getUserDetailsById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id){
        adminService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
