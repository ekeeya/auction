/*
 * Online auctioning system
 *
 * Copyright (c) 2022.  Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>.
 *
 * This program is not free software.
 *
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 */

package com.skycastle.auction.controllers.users;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.skycastle.auction.components.ContextProvider;
import com.skycastle.auction.entities.tokens.RefreshToken;
import com.skycastle.auction.entities.users.User;
import com.skycastle.auction.entities.users.dto.GenericUserDTO;
import com.skycastle.auction.entities.users.dto.Mapper;
import com.skycastle.auction.entities.users.dto.ResponseHandler;
import com.skycastle.auction.entities.users.forms.RegisterForm;
import com.skycastle.auction.services.TokenProviderService;
import com.skycastle.auction.services.refreshTokens.RefreshTokenService;
import com.skycastle.auction.services.users.UserService;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrDataFactory;
import dev.samstevens.totp.qr.QrGenerator;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Slf4j
@RestController
@ResponseBody
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AccountController {

    private final Mapper mapper;
    private final UserService userService;
    private final TokenProviderService tokenProviderService;
    private final QrDataFactory qrDataFactory;
    private final QrGenerator qrGenerator;
    private final CodeVerifier verifier;

    private final RefreshTokenService refreshTokenService;

    private final ContextProvider contextProvider;
    private ResponseEntity<?> generateTokens(User user) {
        Map<String,Object>  accessToken = tokenProviderService.createToken(user, true, false);
        Map<String,Object>  refreshToken = tokenProviderService.createToken(user, true, true);
        Map<String, Object> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        // save the refresh token
        RefreshToken rToken = new RefreshToken();
        String refresh_token = (String) refreshToken.get("token");
        rToken.setToken(refresh_token);
        refreshTokenService.save(rToken);
        return ResponseEntity.ok(tokens);
    }


    @RolesAllowed({"ROLE_ADMIN"})
    @GetMapping("/users")
    public ResponseEntity<?> getUserByUsername(
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page

    ) {
        log.info("Getting users");
        if (username != null) {
            User user = userService.findByUsername(username);
            ResponseHandler response = ResponseHandler.builder()
                    .error(false)
                    .message("success")
                    .data(mapper.toDTO(user, null))
                    .status(HttpStatus.OK)
                    .build();
            return ResponseHandler.generateResponse(response);
        } else {
            Page<User> users = userService.getAllUsers(page, size, "username");
            List<GenericUserDTO> allUsers = users.getContent().stream().map(r -> mapper.toDTO(r, null)).collect(Collectors.toList());
            ResponseHandler response = ResponseHandler.builder()
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

    @GetMapping("/current-user")
    public ResponseEntity<?> currentUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String headerValue
    ){
        try{
            String token = headerValue.substring("Bearer ".length());
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String username = securityContext.getAuthentication().getName();
            User user = userService.findByUsername(username);
            GenericUserDTO  userDto =mapper.toDTO(user, null);
            userDto.setToken(token);
            return ResponseEntity.ok(userDto);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


    @GetMapping("/tokens/refresh")
    public ResponseEntity<?> generateRefreshToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String headerValue) {
        ResponseHandler response;
        HttpStatus status = null;
        try {
            String token = headerValue.substring("Bearer ".length());
            // generate new access and refresh token then retired the former refresh token
            // NOTE: We must only call this endpoint with a refresh token
            // otherwise if one calls it with an access token , we won't get a chance to expire the earlier refresh token
            // So let's check if it is a refresh token else we refuse the request
            if(!tokenProviderService.isRefreshToken(token)){
                status =HttpStatus.FORBIDDEN;
                throw  new InternalAuthenticationServiceException("Wrong authentication token");
            }
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String username = securityContext.getAuthentication().getName();
            User user = userService.findByUsername(username);
            log.info(user+ " access tokens refreshed");
            return generateTokens(user);
        } catch (Exception e) {
            status = status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
            log.error(e.getMessage());
            response = ResponseHandler.builder()
                    .error(true)
                    .data(null)
                    .status(status)
                    .message(e.getMessage())
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
                log.warn(data.toString());
            } else {
                User user = userService.registerUser(registerForm);
                if (user.isEnabled() && user.isUsing2FA()) {
                    QrData qrData = qrDataFactory.newBuilder().label(user.getUsername()).secret(user.getSecret()).issuer("Auction").build();
                    qrCodeImage = getDataUriForImage(qrGenerator.generate(qrData), qrGenerator.getImageMimeType());
                }
                response = ResponseHandler.builder()
                        .error(false)
                        .data(mapper.toDTO(user, qrCodeImage))
                        .status(HttpStatus.OK)
                        .message("User " + user.getUsername() + " successfully registered")
                        .build();
                log.info(user + " successfully registered");
                return ResponseHandler.generateResponse(response);
            }

        } catch (Exception e) {
            messages = e.getMessage();
        }

        response = ResponseHandler.builder()
                .error(true)
                .data(data)
                .status(HttpStatus.BAD_REQUEST)
                .message(messages)
                .build();

        return ResponseHandler.generateResponse(response);
    }

    @GetMapping("/tokens/verify")
    public ResponseEntity<?> verifyJwtToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String headerValue
    ) {
        try{
            // By the time the request reaches here, we know we have a valid token, but let's return the User object
            String token = headerValue.substring("Bearer ".length());
            DecodedJWT decodedJWT = tokenProviderService.validateToken(token);
            String username = decodedJWT.getSubject();
            User user = (User) userService.loadUserByUsername(username);
            log.info(user.toString()+ "verified");
            return ResponseEntity.ok(user);
        }catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/tokens/otp-verify")
    @PreAuthorize("hasRole('ROLE_PRE_VERIFIED')")
    public ResponseEntity<?> verifyOtpCode(
            @RequestParam(value = "code") String code
    ) {
       try{
           SecurityContext securityContext = SecurityContextHolder.getContext();
           String username = securityContext.getAuthentication().getName();
           User user = userService.findByUsername(username);
           if (!verifier.isValidCode(user.getSecret(), code)) {
               log.error("Invalid or Expired OTP Code");
               return ResponseHandler.generateResponse(ResponseHandler.builder()
                       .error(true)
                       .data(null)
                       .status(HttpStatus.BAD_REQUEST)
                       .message("Invalid or Expired OTP Code")
                       .build());
           } else {
               log.info(user+ "generate tokens");
               return generateTokens(user);
           }
       }catch (Exception e){
           log.error(e.getMessage(), e);
           return ResponseEntity.internalServerError().body(e.getMessage());
       }
    }

    @GetMapping("/changeStatus/{username}")
    @RolesAllowed({"ROLE_ADMIN"})
    @PreAuthorize("#username != authentication.principal.username")
    public ResponseEntity<?> disableAccount(
            @PathVariable(value = "username") String username,
            @RequestParam(name = "enable") boolean enable
    ) {
        try{
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
            response = ResponseHandler.builder()
                    .error(error)
                    .status(statusCode)
                    .message(message)
                    .build();
            return ResponseHandler.generateResponse(response);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
