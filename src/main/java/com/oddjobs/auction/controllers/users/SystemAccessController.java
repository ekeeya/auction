package com.oddjobs.auction.controllers.users;

import com.oddjobs.auction.entities.users.User;
import com.oddjobs.auction.entities.users.dto.GenericUserDTO;
import com.oddjobs.auction.entities.users.dto.ResponseHandler;
import com.oddjobs.auction.services.users.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
public class SystemAccessController {
    private final Mapper mapper;
    private final UserService userService;

    @Autowired
    public SystemAccessController(Mapper mapper, UserService userService) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping("/users")
    public ResponseEntity<Object> getUserByUsername(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page

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
}
