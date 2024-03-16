package edu.java.serviceDto;

import java.net.URI;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class LinkResponse {
    private Long id;
    private URI url;
}
