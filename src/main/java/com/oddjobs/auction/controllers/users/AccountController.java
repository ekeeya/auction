package com.oddjobs.auction.controllers.users;
import com.oddjobs.auction.entities.users.AdminUser;
import com.oddjobs.auction.entities.users.Buyer;
import com.oddjobs.auction.entities.users.Seller;
import com.oddjobs.auction.entities.users.User;
import com.oddjobs.auction.entities.users.dto.GenericUserDTO;
import com.oddjobs.auction.entities.users.dto.Mapper;
import com.oddjobs.auction.entities.users.dto.ResponseHandler;
import com.oddjobs.auction.entities.users.forms.RegisterForm;
import com.oddjobs.auction.services.users.UserService;
import com.oddjobs.auction.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@ResponseBody
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final Mapper mapper;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    @GetMapping("/users")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<Object> getUserByUsername(
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page

    ) {
        if (username != null) {
            User user = userService.findByUsername(username);
            ResponseHandler response = new ResponseHandler.ResponseHandlerBuilder()
                    .error(false)
                    .message("success")
                    .data(mapper.toDTO(user))
                    .status(HttpStatus.OK)
                    .build();
            return ResponseHandler.generateResponse(response);
        } else {
            Page<User> users = userService.getAllUsers(page, size, "username");

            List<GenericUserDTO> allUsers = users.getContent().stream().map(mapper::toDTO).collect(Collectors.toList());
            ResponseHandler response = new ResponseHandler.ResponseHandlerBuilder()
                    .error(false)
                    .message("success")
                    .data(allUsers)
                    .status(HttpStatus.OK)
                    .count(users.getTotalElements())
                    .totalPages(users.getTotalPages())
                    .size(users.getSize())
                    .build();
            return ResponseHandler.generateResponse(response);
        }

    }

    @PostMapping("/register")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<?> registerUser(
            @RequestBody @Valid RegisterForm registerForm, BindingResult bindingResult) {
        ResponseHandler response;
        String messages;
        if (registerForm.emailAlreadyExists(userService)) {
            messages = "Email already exists";
            ObjectError error = new ObjectError(bindingResult.getObjectName(), messages);
            bindingResult.addError(error);

        }
        if (registerForm.usernameAlreadyExists(userService)) {
            messages = "Username already exists";
            ObjectError error = new ObjectError(bindingResult.getObjectName(), messages);
            bindingResult.addError(error);
        }
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            registerForm.setErrors(errors);
            messages = "Errors in your request";
            response = new ResponseHandler.ResponseHandlerBuilder()
                    .error(true)
                    .data(registerForm.getReadableErrors())
                    .status(HttpStatus.BAD_REQUEST)
                    .message(messages)
                    .build();

        } else {

            String accountType = registerForm.getAccountType();
            User user;
            if (accountType.equals(Utils.ACCOUNT_TYPE.SELLER.name())) {
                user = new Seller();
                ((Seller) user).setIdentification(registerForm.getIdentification());
                ((Seller) user).setName(registerForm.getName());
                ((Seller) user).setNin(registerForm.getNin());

            } else if (accountType.equals(Utils.ACCOUNT_TYPE.BUYER.name())) {
                user = new Buyer();
                ((Buyer) user).setFirstname(registerForm.getFirstname());
                ((Buyer) user).setLastname(registerForm.getLastname());
                ((Buyer) user).setGender(registerForm.getGender());
            } else {
                user = new AdminUser();
                ((AdminUser) user).setFirstname(registerForm.getFirstname());
                ((AdminUser) user).setLastname(registerForm.getLastname());
                ((AdminUser) user).setDepartment(registerForm.getDepartment());
            }
            user.setUsername(registerForm.getUsername());
            user.setPassword(passwordEncoder.encode(registerForm.getPassword()));
            user.setAccountType(Utils.ACCOUNT_TYPE.valueOf(accountType));
            user.setEmail(registerForm.getEmail());
            user.setAddress(registerForm.getAddress());
            User u = userService.save(user);
            response = new ResponseHandler.ResponseHandlerBuilder()
                    .error(false)
                    .data(mapper.toDTO(u))
                    .status(HttpStatus.OK)
                    .message("User " + user.getUsername() + " successfully registered")
                    .build();
        }
        return ResponseHandler.generateResponse(response);
    }

    @GetMapping("/changeStatus/{username}")
    @RolesAllowed({"ROLE_ADMIN"})
    @PreAuthorize("#username != authentication.principal.username")
    public ResponseEntity<?> disableAccount(
            @PathVariable(value="username") String username,
            @RequestParam(name = "enable") boolean enable
    ) {
        User user = userService.findByUsername(username);
        String status = enable ? "Enabled" : "Disabled";
        String message;
        HttpStatus statusCode = HttpStatus.OK;
        boolean error = false;
        ResponseHandler response;
        if (user == null) {
            message = "Account you are trying to " + status + " does not exist!";
            statusCode = HttpStatus.BAD_REQUEST;
            error = true;
        } else {
            if (!user.getEnabled() && !enable) {
                message = "Account is already Disabled!";
            } else if (user.getEnabled() && enable) {
                message = "Account is already Enabled!";
            } else {
                userService.disableEnableAccount(user, enable);
                message = "User Account has been " + status;
            }
        }
        response = new ResponseHandler.ResponseHandlerBuilder()
                .error(error)
                .status(statusCode)
                .message(message)
                .build();
        return ResponseHandler.generateResponse(response);
    }


}
