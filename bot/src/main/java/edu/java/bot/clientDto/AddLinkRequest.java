package edu.java.bot.clientDto;

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
