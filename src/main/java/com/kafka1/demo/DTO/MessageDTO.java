package com.kafka1.demo.DTO;

import com.kafka1.demo.Models.Role;
import lombok.Data;

@Data
public class MessageDTO {
    private UserDTO userDTO;
    private DoctorDTO doctorDTO;
    private String text;
    private Role owner;
}
