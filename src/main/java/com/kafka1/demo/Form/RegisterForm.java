package com.kafka1.demo.Form;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class RegisterForm {
    @Email
    @NotEmpty(message = "не может быть пустым")
    private String email;
    @Size(min = 8,max = 20,message = "size password must be from 8 to 20 characters")
    private String password;
    @Size(min = 3,max = 20,message = "Длинна фамилии от 3 до 20 символов")
    private String lastName;
    @Size(min = 3,max = 20,message = "Длинна имя от 3 до 20 символов")
    private String firstName;
}
