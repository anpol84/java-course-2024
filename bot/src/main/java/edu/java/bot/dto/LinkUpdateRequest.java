package edu.java.bot.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
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
