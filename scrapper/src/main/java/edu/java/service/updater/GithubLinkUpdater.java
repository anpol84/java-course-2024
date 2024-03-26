package edu.java.service.updater;

import edu.java.client.GithubWebClient;
import edu.java.clientDto.GithubResponse;
import edu.java.clientDto.LinkUpdateRequest;
import edu.java.model.Link;
import edu.java.repository.LinkRepository;
import edu.java.serviceDto.GithubInfo;
import edu.java.utils.LinkUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class GithubLinkUpdater implements LinkUpdater {
    private final GithubWebClient githubWebClient;
    private final LinkRepository linkRepository;
    private final SendUpdateService sendUpdateService;
    private final static String URL = "https://github.com/";

    @Override
    @Transactional
    public int process(Link link) {
        GithubInfo info = LinkUtils.extractGithubInfoFromUrl(link.getUrl().toString());
        if (link.getLastApiUpdate() == null) {
            return 0;
        }
        GithubResponse githubResponse =
            githubWebClient.fetchLatestRepositoryActivityWithRetry(info.getRepository(), info.getAccount());
        if (githubResponse.getCreatedAt().isAfter(link.getLastApiUpdate())) {
            List<Long> chatIds = linkRepository.findChatIdsByUrl(link.getUrl().toString());
            try {
                sendUpdateService.update(new LinkUpdateRequest()
                    .setId(link.getId())
                    .setUrl(link.getUrl())
                    .setDescription(getDescription(githubResponse))
                    .setTgChatIds(chatIds));
            } catch (Exception ignored) {
            }
            linkRepository.setLastApiUpdate(link.getUrl().toString(), githubResponse.getCreatedAt());
            return 1;
        }
        return 0;
    }



    @Override
    public boolean support(String url) {
        return url.startsWith(URL);
    }


    @Override
    public String getDomain() {
        return "github.com";
    }

    @Override
    @Transactional
    public void setLastUpdate(Link link) {
        GithubInfo info = LinkUtils.extractGithubInfoFromUrl(link.getUrl().toString());
        GithubResponse githubResponse =
            githubWebClient.fetchLatestRepositoryActivityWithRetry(info.getRepository(), info.getAccount());
        if (githubResponse == null) {
            return;
        }
        linkRepository.setLastApiUpdate(link.getUrl().toString(), githubResponse.getCreatedAt());
    }

    private String getDescription(GithubResponse githubResponse) {
        String link = URL + githubResponse.getRepo().getRepositoryName();
        GithubEventEnum eventType = GithubEventEnum.fromType(githubResponse.getType());
        String event = eventType.getDescription();
        return String.format("Link: %s\n%s\nIn repository: %s\nBy: %s", link, event,
            githubResponse.getRepo().getRepositoryName(), githubResponse.getAuthor().getAuthorName());
    }
}
