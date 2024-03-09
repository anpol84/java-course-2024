package edu.java.service.updater;

import edu.java.client.BotWebClient;
import edu.java.client.GithubWebClient;
import edu.java.clientDto.GithubResponse;
import edu.java.clientDto.LinkUpdateRequest;
import edu.java.model.Link;
import edu.java.repository.LinkRepository;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class GithubLinkUpdater implements LinkUpdater {
    private final GithubWebClient githubWebClient;
    private final LinkRepository linkRepository;
    private final BotWebClient botWebClient;

    @Override
    public int process(Link link) {
        String[] args = processLink(link.getUrl());
        GithubResponse githubResponse =
            githubWebClient.fetchLatestRepositoryActivity(args[0], args[1]);
        if (githubResponse.getCreatedAt().isAfter(link.getLastApiUpdate())) {
            List<Long> chatIds = linkRepository.findChatIdsByUrl(link.getUrl());
            try {
                botWebClient.sendUpdate(new LinkUpdateRequest(link.getId(), new URI(link.getUrl()),
                    getDescription(githubResponse), chatIds));
            } catch (Exception ignored) {
            }
            linkRepository.setLastApiUpdate(link.getUrl(), githubResponse.getCreatedAt());
            return 1;
        }
        return 0;
    }

    @Override
    public boolean support(String url) {
        return url.startsWith("https://github.com/");
    }

    @Override
    public String[] processLink(String url) {
        String[] parts = url.split("/");
        String repositoryName = parts[parts.length - 1];
        String accountName = parts[parts.length - 2];
        return new String[]{repositoryName, accountName};
    }

    @Override
    public String getDomain() {
        return "github.com";
    }

    @Override
    public void setLastUpdate(Link link) {
        String[] args = processLink(link.getUrl());
        GithubResponse githubResponse =
            githubWebClient.fetchLatestRepositoryActivity(args[0], args[1]);
        linkRepository.setLastApiUpdate(link.getUrl(), githubResponse.getCreatedAt());
    }

    private String getDescription(GithubResponse githubResponse) {
        return githubResponse.getType() + "\n"
            + "In repository: " + githubResponse.getRepo() + "\n"
            + "By: " + githubResponse.getAuthor();
    }
}
