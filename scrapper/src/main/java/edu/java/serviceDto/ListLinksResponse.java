package edu.java.serviceDto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ListLinksResponse {

    private List<LinkResponse> links;

    private int size;
}
