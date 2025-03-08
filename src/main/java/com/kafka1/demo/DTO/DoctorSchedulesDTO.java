package com.kafka1.demo.DTO;

import com.kafka1.demo.Models.DayOfWeek;
import com.kafka1.demo.Models.TimeInterval;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DoctorSchedulesDTO {
    private DayOfWeek day;
    private List<TimeInterval> timeIntervals;
}