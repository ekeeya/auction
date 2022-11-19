package com.oddjobs.auction.controllers.users;

import com.oddjobs.auction.entities.users.AdminUser;
import com.oddjobs.auction.entities.users.Buyer;
import com.oddjobs.auction.entities.users.Seller;
import com.oddjobs.auction.entities.users.User;
import com.oddjobs.auction.entities.users.dto.GenericUserDTO;
import com.oddjobs.auction.entities.users.dto.Mapper;
import com.oddjobs.auction.entities.users.dto.ResponseHandler;
import com.oddjobs.auction.entities.users.forms.RegisterForm;
import com.oddjobs.auction.services.TokenProviderService;
import com.oddjobs.auction.services.users.UserService;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrDataFactory;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Slf4j
@RestController
@ResponseBody
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final Mapper mapper;
    private final UserService userService;
    private final TokenProviderService tokenProviderService;

    private final QrDataFactory qrDataFactory;
    private final QrGenerator qrGenerator;
    private final CodeVerifier verifier;

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
                    .data(mapper.toDTO(user, null))
                    .status(HttpStatus.OK)
                    .build();
            return ResponseHandler.generateResponse(response);
        } else {
            Page<User> users = userService.getAllUsers(page, size, "username");

            List<GenericUserDTO> allUsers = users.getContent().stream().map(r -> mapper.toDTO(r, null)).collect(Collectors.toList());
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
    public ResponseEntity<?> registerUser(
            @RequestBody @Valid RegisterForm registerForm, BindingResult bindingResult) {
        ResponseHandler response;
        String qrCodeImage = null;
        String messages;
        Object data = null;
        try {
            if (bindingResult.hasErrors()) {
                List<ObjectError> errors = bindingResult.getAllErrors();
                registerForm.setErrors(errors);
                messages = "Errors in your request";
                data = registerForm.getReadableErrors();
            } else {
                User user = userService.registerUser(registerForm);
                if (user.isEnabled()) {
                    QrData qrData = qrDataFactory.newBuilder().label(user.getUsername()).secret(user.getSecret()).issuer("Auction").build();
                    qrCodeImage = getDataUriForImage(qrGenerator.generate(qrData), qrGenerator.getImageMimeType());
                }
                response = new ResponseHandler.ResponseHandlerBuilder()
                        .error(false)
                        .data(mapper.toDTO(user, qrCodeImage))
                        .status(HttpStatus.OK)
                        .message("User " + user.getUsername() + " successfully registered")
                        .build();
                return ResponseHandler.generateResponse(response);
            }

        } catch (Exception e) {
            messages = e.getMessage();
        }

        response = new ResponseHandler.ResponseHandlerBuilder()
                .error(true)
                .data(data)
                .status(HttpStatus.BAD_REQUEST)
                .message(messages)
                .build();

        return ResponseHandler.generateResponse(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtpCode(
            @RequestParam(value = "code") String code
    ) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        String username = securityContext.getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (!verifier.isValidCode(user.getSecret(), code)) {
            return ResponseHandler.generateResponse(new ResponseHandler.ResponseHandlerBuilder()
                    .error(true)
                    .data(null)
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Invalid or Expired OTP Code")
                    .build());
        } else {
            String access_token = tokenProviderService.createToken(user, true);
            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", access_token);
            return ResponseEntity.ok(tokens);
        }
    }

    @GetMapping("/changeStatus/{username}")
    @RolesAllowed({"ROLE_ADMIN"})
    @PreAuthorize("#username != authentication.principal.username")
    public ResponseEntity<?> disableAccount(
            @PathVariable(value = "username") String username,
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
