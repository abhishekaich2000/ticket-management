package com.ticket.management.dto;

import java.time.LocalDateTime;

public record ErrorRecordDto(String apiPath, String errorMessage, String errorCode,
     LocalDateTime timestamp) {

}
