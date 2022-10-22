package com.oddjobs.auction.entities.users.forms;

import com.oddjobs.auction.services.users.UserService;
import com.oddjobs.auction.utils.Utils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;
import com.oddjobs.auction.entities.users.User;

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

    @NotNull
    private String accountType;

    @Size(min=14, max = 14, message = "Ugandan format national Id number must be 14 characters")
    private final String nin;
    private String firstname;
    private String lastname;
    private final Utils.ROLES role = Utils.ROLES.USER;
    private final Map<String, String> settings = new HashMap<>();
    private  Utils.Gender gender;
    private String identification;
    private String department;

    public boolean emailAlreadyExists(UserService userService){
        return  userService.findByEmail(this.email) != null;
    }

    public boolean usernameAlreadyExists(UserService userService){
        return  userService.findByUsername(this.username) != null;
    }
}
