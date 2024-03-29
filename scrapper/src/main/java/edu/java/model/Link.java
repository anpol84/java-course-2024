package edu.java.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Accessors(chain = true)
@Entity
@Setter
@Getter
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = URIToStringConverter.class)
    private URI url;

    @Column(name = "update_at")
    private OffsetDateTime updateAt;

    @Column(name = "last_api_update")
    private OffsetDateTime lastApiUpdate;

    @ManyToMany(mappedBy = "links")
    private List<Chat> chats;
}
