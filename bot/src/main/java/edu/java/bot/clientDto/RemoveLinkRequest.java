package edu.java.bot.clientDto;

import jakarta.validation.constraints.NotNull;
import java.net.URI;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RemoveLinkRequest {
    @NotNull
    private URI link;
}
