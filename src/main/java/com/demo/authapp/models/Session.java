package com.demo.authapp.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
public class Session {
    private String value;
    private String userName;
    private LocalDateTime expiredTime;
}
