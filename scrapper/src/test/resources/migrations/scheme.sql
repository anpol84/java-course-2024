

SET TIME ZONE 'UTC';


CREATE TABLE chat (
                      id INTEGER,
                      PRIMARY KEY (id)
);

CREATE TABLE link (
                      id SERIAL,
                      url VARCHAR(255) NOT NULL UNIQUE ,
                      update_at TIMESTAMP,
                      last_api_update TIMESTAMP,
                      PRIMARY KEY (id)
);

CREATE TABLE chat_link (
                           chat_id INTEGER,
                           link_id INTEGER,
                           PRIMARY KEY (chat_id, link_id),
                           CONSTRAINT fk_chat_id FOREIGN KEY (chat_id) REFERENCES chat(id) ON DELETE CASCADE ,
                           CONSTRAINT fk_link_id FOREIGN KEY (link_id) REFERENCES link(id)
);
