package edu.java.service.updater;

import edu.java.client.BotWebClient;
import edu.java.client.StackOverflowWebClient;
import edu.java.clientDto.LinkUpdateRequest;
import edu.java.clientDto.StackOverflowResponse;
import edu.java.model.Link;
import edu.java.repository.LinkRepository;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class StackOverflowLinkUpdater implements LinkUpdater {
    private final LinkRepository linkRepository;
    private final StackOverflowWebClient stackOverflowWebClient;
    private final BotWebClient botWebClient;

    @Override
    public int process(Link link) {
        String[] args = processLink(link.getUrl());
        long number = Long.parseLong(args[args.length - 1]);
        StackOverflowResponse stackOverflowResponse =
            stackOverflowWebClient.fetchLatestAnswer(number);
        if (link.getLastApiUpdate().equals(OffsetDateTime.MIN)) {
            linkRepository.setLastApiUpdate(link.getUrl(), stackOverflowResponse.getLastActivityDate());
        } else if (stackOverflowResponse.getLastActivityDate().isAfter(link.getLastApiUpdate())) {
            List<Long> chatIds = linkRepository.findChatIdsByUrl(link.getUrl());
            try {
                botWebClient.sendUpdate(new LinkUpdateRequest(link.getId(), new URI(link.getUrl()),
                    getDescription(stackOverflowResponse), chatIds));
            } catch (Exception ignored) {

            }
            linkRepository.setLastApiUpdate(link.getUrl(), stackOverflowResponse.getLastActivityDate());
            return 1;
        }
        return 0;
    }

    @Override
    public boolean support(String url) {
        return  url.startsWith("https://stackoverflow.com/questions/");
    }

    @Override
    public String[] processLink(String url) {
        return url.split("/");
    }

    @Override
    public String getDomain() {
        return "stackoverflow.com";
    }

    private String getDescription(StackOverflowResponse stackOverflowResponse) {
        return "New answer for question №" + stackOverflowResponse.getQuestionId()
            + "\nBy: " + stackOverflowResponse.getOwner().getDisplayName()
            + "\nWith body:\n" + stackOverflowResponse.getBody();
    }
}
