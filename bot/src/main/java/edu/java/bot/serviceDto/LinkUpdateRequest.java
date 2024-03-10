package edu.java.bot.serviceDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LinkUpdateRequest {
    @NotNull
    private Long id;

    @NotNull
    private URI url;

    @NotNull
    @NotEmpty
    private String description;

    @NotNull
    @NotEmpty
    private List<Long> tgChatIds;
}
