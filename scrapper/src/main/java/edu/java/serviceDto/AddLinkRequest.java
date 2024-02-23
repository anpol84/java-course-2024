package edu.java.serviceDto;

import jakarta.validation.constraints.NotNull;
import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class AddLinkRequest {
    @NotNull
    private URI link;
}
