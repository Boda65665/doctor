package com.kafka1.demo.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionDTO {
    private int id;
    private LocalTime timeStart;
    private LocalTime timeEnd;
    private DoctorDTO doctor;
    private UserDTO user;
    private String url;
    private LocalTime timeReallyStart;
}
