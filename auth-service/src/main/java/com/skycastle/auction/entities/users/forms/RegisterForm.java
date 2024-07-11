package com.skycastle.auction.entities.users.forms;

import com.skycastle.auction.utils.Utils;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor(force = true)
public class RegisterForm extends BaseForm{

    @NotNull
    @Size(min=2, max=30)
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String email;
    private String address;
    private String name;


    private  boolean enable2Fa =  false;

    @NotNull
    private String accountType;

    @Size(min=14, max = 14, message = "Ugandan format national Id number must be 14 characters")
    private final String nin;
    private String firstname;
    private String lastname;
    private final Utils.ROLES role = Utils.ROLES.ROLE_ANONYMOUS;
    private final Map<String, String> settings = new HashMap<>();
    private  Utils.Gender gender;
    private String identification;
    private String department;
}
