package edu.java.service.updater;

import edu.java.client.BotWebClient;
import edu.java.client.GithubWebClient;
import edu.java.clientDto.GithubResponse;
import edu.java.clientDto.LinkUpdateRequest;
import edu.java.model.Link;
import edu.java.repository.LinkRepository;
import edu.java.utils.LinkUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class GithubLinkUpdater implements LinkUpdater {
    private final GithubWebClient githubWebClient;
    private final LinkRepository jooqLinkRepository;
    private final BotWebClient botWebClient;
    private final static String URL = "https://github.com/";

    @Override
    @Transactional
    public int process(Link link) {
        String[] args = LinkUtils.extractGithubInfoFromUrl(link.getUrl().toString());
        if (link.getLastApiUpdate() == null) {
            return 0;
        }
        GithubResponse githubResponse =
            githubWebClient.fetchLatestRepositoryActivity(args[0], args[1]);
        if (githubResponse.getCreatedAt().isAfter(link.getLastApiUpdate())) {
            List<Long> chatIds = jooqLinkRepository.findChatIdsByUrl(link.getUrl().toString());
            try {
                botWebClient.sendUpdate(new LinkUpdateRequest().setId(link.getId()).setUrl(link.getUrl())
                    .setDescription(getDescription(githubResponse)).setTgChatIds(chatIds));
            } catch (Exception ignored) {
            }
            jooqLinkRepository.setLastApiUpdate(link.getUrl().toString(), githubResponse.getCreatedAt());
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
        String[] args = LinkUtils.extractGithubInfoFromUrl(link.getUrl().toString());
        GithubResponse githubResponse =
            githubWebClient.fetchLatestRepositoryActivity(args[0], args[1]);
        if (githubResponse == null) {
            return;
        }
        jooqLinkRepository.setLastApiUpdate(link.getUrl().toString(), githubResponse.getCreatedAt());
    }

    private String getDescription(GithubResponse githubResponse) {
        String link = URL + githubResponse.getRepo().getRepositoryName();
        String event = switch (githubResponse.getType()) {
            case "CommitCommentEvent":
                yield "A commit comment is created.";
            case "CreateEvent":
                yield "A Git branch or tag is created.";
            case "DeleteEvent":
                yield "A Git branch or tag is deleted.";
            case "ForkEvent":
                yield "A user forks a repository.";
            case "GollumEvent":
                yield "A wiki page is created or updated.";
            case "IssueCommentEvent":
                yield "Activity related to an issue or pull request comment.";
            case "IssuesEvent":
                yield "Activity related to an issue.";
            case "MemberEvent":
                yield "Activity related to repository collaborators.";
            case "PublicEvent":
                yield "Private repository is made public.";
            case "PullRequestEvent":
                yield "Activity with pull request.";
            case "PullRequestReviewEvent":
                yield "Activity with review pull request.";
            case "PullRequestReviewCommentEvent":
                yield "Activity with comment in review pull request.";
            case "PullRequestReviewThreadEvent":
                yield "Activity related to a comment thread on a pull request being marked as resolved or unresolved.";
            case "PushEvent":
                yield "One or more commits are pushed to a repository branch or tag.";
            case "WatchEvent":
                yield "Someone stars a repository.";
            default:
                yield "Unknown event";
        };
        return "Link: " + link + "\n" +  event + "\n"
            + "In repository: " + githubResponse.getRepo().getRepositoryName() + "\n"
            + "By: " + githubResponse.getAuthor().getAuthorName();
    }
}
