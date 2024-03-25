package edu.java.client;

import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;


@Data
@Accessors(chain = true)
public class RetryConfigDTO {

    private RetryPolicy retryPolicy;

    private int retryCount;

    private int linearFuncArg;

    private Set<HttpStatus> retryStatuses;
}
