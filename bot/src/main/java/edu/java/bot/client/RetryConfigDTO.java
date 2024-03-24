package edu.java.bot.client;

import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;


@Data
@Accessors(chain = true)
public class RetryConfigDTO {

    private RetryPolicy retryPolicy;

    private int constantRetryCount;

    private int linearRetryCount;
    private int exponentialRetryCount;

    private int linearFuncArg;

    private Set<HttpStatus> retryStatuses;
}
