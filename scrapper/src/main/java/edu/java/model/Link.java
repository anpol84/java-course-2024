package edu.java.model;

import java.net.URI;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class Link {
    private Long id;
    private URI url;
    private OffsetDateTime updateAt;
    private OffsetDateTime lastApiUpdate;
}
