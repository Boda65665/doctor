package com.kafka1.demo.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDTO {
    private int id;
    private String speciality;
    private UserDTO user;
    private int rating;
    private int pricePerMinute;
    private List<DoctorSchedulesDTO> doctorSchedulesDTO;
}
