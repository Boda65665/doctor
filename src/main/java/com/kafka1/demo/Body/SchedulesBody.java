package com.kafka1.demo.Body;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SchedulesBody {
    @NotNull(message = "поле id не может быть равно null")
    private int id;
    @NotNull(message = "поле dayOfWeekString не может быть равно null")
    private String dayOfWeekString;
    @NotNull(message = "поле timeStartString не может быть равно null")
    private String timeStartString;
    @NotNull(message = "поле timeEndString не может быть равно null")
    private String timeEndString;
}
