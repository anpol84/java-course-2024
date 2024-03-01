package edu.java.service;

import edu.java.client.BotWebClient;
import edu.java.client.GithubWebClient;
import edu.java.client.StackOverflowWebClient;
import edu.java.clientDto.GithubResponse;
import edu.java.clientDto.LinkUpdateRequest;
import edu.java.clientDto.StackOverflowResponse;
import edu.java.model.Link;
import edu.java.repository.JdbcLinkRepository;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class JdbcLinkUpdater implements LinkUpdater {
    private final JdbcLinkRepository jdbcLinkRepository;
    private final GithubWebClient githubWebClient;
    private final StackOverflowWebClient stackOverflowWebClient;
    private final BotWebClient botWebClient;
    private final static int COUNT_UPDATES = 5;
    //апи возвращают время по гринвичу, сервер в мск (я), поэтому плюсую 3 часа
    private final static int MSK_TIME = 3;

    @Override
    public int update() throws URISyntaxException {
        List<Link> links = jdbcLinkRepository.findByLastActivity(COUNT_UPDATES);
        int count = 0;
        for (Link link : links) {
            if (isStackOverflowLink(link.getUrl())) {
                long number = Long.valueOf(processStackOverflowLink(link.getUrl()));
                Optional<StackOverflowResponse> stackOverflowResponse =
                    stackOverflowWebClient.fetchLatestAnswer(number);
                if (stackOverflowResponse.get().getLastActivityDate().plusHours(MSK_TIME)
                    .compareTo(link.getLastActivity()) > 0) {
                    StringBuilder description = new StringBuilder();
                    description.append("New answer for question №").append(stackOverflowResponse.get().getQuestionId())
                        .append("\nBy: ").append(stackOverflowResponse.get().getOwner().getDisplayName())
                        .append("\nAt: ").append(stackOverflowResponse.get().getLastActivityDate())
                        .append("\nWith body:\n").append(stackOverflowResponse.get().getBody());
                    List<Long> chatIds = jdbcLinkRepository.findChatIdsByUrl(link.getUrl());
                    botWebClient.sendUpdate(new LinkUpdateRequest(link.getId(), new URI(link.getUrl()),
                        description.toString(), chatIds));
                    count++;
                }
            } else if (isGitHubLink(link.getUrl())) {
                String[] args = processGitHubLink(link.getUrl());
                Optional<GithubResponse> githubResponse =
                    githubWebClient.fetchLatestRepositoryActivity(args[0], args[1]);
                if (githubResponse.get().getCreatedAt().plusHours(MSK_TIME).compareTo(link.getLastActivity()) > 0) {
                    StringBuilder description = new StringBuilder();
                    description.append(githubResponse.get().getType()).append("\n")
                        .append("In repository: ").append(githubResponse.get().getRepo()).append("\n")
                        .append("By: ").append(githubResponse.get().getAuthor()).append("\n")
                        .append("At: ").append(githubResponse.get().getCreatedAt());
                    List<Long> chatIds = jdbcLinkRepository.findChatIdsByUrl(link.getUrl());
                    botWebClient.sendUpdate(new LinkUpdateRequest(link.getId(), new URI(link.getUrl()),
                        description.toString(), chatIds));
                    count++;
                }
            }
            jdbcLinkRepository.updateTime(link.getUrl(), OffsetDateTime.now(ZoneOffset.UTC));
        }
        return count;
    }

    private boolean isStackOverflowLink(String url) {
        return url.startsWith("https://stackoverflow.com/questions/");
    }

    private boolean isGitHubLink(String url) {
        return url.startsWith("https://github.com/");
    }

    private String processStackOverflowLink(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }

    private String[] processGitHubLink(String url) {
        String[] parts = url.split("/");
        String repositoryName = parts[parts.length - 1];
        String accountName = parts[parts.length - 2];
        return new String[]{repositoryName, accountName};
    }
}
