package edu.java.serviceDto;

import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class LinkResponse {
    private Long id;
    private URI url;
}