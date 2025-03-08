package com.kafka1.demo.Models;

import lombok.Getter;

@Getter
public enum ResponseType {
    MESSAGE("message"),
    ERROR("error");

    private final String type;

    ResponseType(String type) {
        this.type=type;
    }
}
