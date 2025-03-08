package com.kafka1.demo.Body;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SchedulesBody {
    @NotNull
    private int id;
    @NotNull
    private String dayOfWeekString;
    @NotNull
    private String timeStartString;
    @NotNull
    private String timeEndString;
}
