package com.shabanaj.beloyal.Controller;

import com.shabanaj.beloyal.Dto.User.UpdateUserDto;
import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Enums.Role;
import com.shabanaj.beloyal.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/api/besahub/admin/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody User user) {
        try{
            userService.createUser(user);

            return ResponseEntity.ok("User created successfully!");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        try{
            List<User> users = userService.getAllUsers();

            return ResponseEntity.ok(users);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(List.of());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findUserById(@PathVariable Long id){
        try{
            User user= userService.getUserOrThrow(id);

            return ResponseEntity.ok(user);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        try{
            userService.deleteUser(id);

            return ResponseEntity.ok("User deleted successfully!");
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UpdateUserDto updatedUser){
        try{
            userService.updateUser(id, updatedUser);

            return ResponseEntity.ok("User updated successfully!");
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/enable")
    public ResponseEntity<String> enableUser(@PathVariable Long id){
        try{
            userService.enableUser(id);

            return ResponseEntity.ok("User was enabled successfully!");
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<String> disableUser(@PathVariable Long id){
        try{
            userService.disableUser(id);

            return ResponseEntity.ok("User was disabled successfully!");
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/lock")
    public ResponseEntity<String> lockUser(@PathVariable Long id){
        try {
            userService.lockUser(id);

            return ResponseEntity.ok("User locked successfully!");
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/unlock")
    public ResponseEntity<String> unlockUser(@PathVariable Long id){
        try {
            userService.unlockUser(id);

            return ResponseEntity.ok("User unlocked successfully!");
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/roles")
    public ResponseEntity<Set<Role>> getRoles(@PathVariable Long id){
        try{
            return ResponseEntity.ok(userService.getUserRoles(id));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

}
