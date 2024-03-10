package edu.java.bot.clientDto;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ListLinksResponse {

    private List<LinkResponse> links;

    private int size;
}
