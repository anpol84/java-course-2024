package edu.java.serviceDto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GithubInfo {
    private String repository;
    private String account;
}
