package com.kafka1.demo.Form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorForm {
    private String email;
    private int pricePerMinute;
    private String speciality;
}
