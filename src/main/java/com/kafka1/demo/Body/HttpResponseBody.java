package com.kafka1.demo.Body;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

@Getter
public class HttpResponseBody {
    @Setter
    private String message;
    private final HashMap<String,Object> data = new HashMap<>();
    @Setter
    private HttpStatus httpStatus;

    public void putData(String key, Object data){
        this.data.put(key,data);
    }

    public Object get(String key){
        return data.get(key);
    }
}
