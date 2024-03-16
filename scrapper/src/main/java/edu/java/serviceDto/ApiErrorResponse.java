package edu.java.serviceDto;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class ApiErrorResponse {
    private String description;
    private String code;
    private String exceptionName;
    private String exceptionMessage;
    private List<String> stacktrace;
}
