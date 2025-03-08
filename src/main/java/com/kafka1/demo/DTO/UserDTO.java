package com.kafka1.demo.DTO;

import com.kafka1.demo.Models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private int id;
    private String email;
    private String password;
    private Role role;
    private String lastName;
    private String firstName;

    public UserDTO(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public UserDTO( String firstName,String lastName,String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.lastName = lastName;
        this.firstName = firstName;
    }
}
