package com.kafka1.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatDTO {
    private List<MessageDTO> messages;
    private UserDTO userDTO;
    private DoctorDTO doctorDTO;
}
