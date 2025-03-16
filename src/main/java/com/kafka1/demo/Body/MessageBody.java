package com.kafka1.demo.Body;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageBody {
    @NotNull(message = "поле text не может быть равно null")
    private String text;
    @NotNull(message = "поле userId не может быть равно null")
    private int idRecipient;
}
