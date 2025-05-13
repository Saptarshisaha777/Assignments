package com.assignment.DocIngest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResp {

    private String apiPath;
    private String message;
    private HttpStatus errorCode;
    private LocalDateTime errorTime;

}
