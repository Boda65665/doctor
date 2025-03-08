package com.kafka1.demo.DTO;

import lombok.Data;

@Data
public class ReviewDTO {
    private int id;
    private String text;
    private int stars;
    private DoctorDTO doctor;
    private UserDTO user;
}
