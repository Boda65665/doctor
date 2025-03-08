package com.kafka1.demo.Models;

import java.time.LocalTime;

public record TimeInterval(LocalTime start,LocalTime end,int id) {
    public TimeInterval(LocalTime start,LocalTime end) {
        this(start, end,-1);
    }
}
