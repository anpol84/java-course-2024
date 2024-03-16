package edu.java.service.updater;

import edu.java.client.BotWebClient;
import edu.java.client.StackOverflowWebClient;
import edu.java.clientDto.LinkUpdateRequest;
import edu.java.clientDto.StackOverflowResponse;
import edu.java.model.Link;
import edu.java.repository.LinkRepository;
import edu.java.utils.LinkUtils;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class StackOverflowLinkUpdater implements LinkUpdater {
    private final LinkRepository linkRepository;
    private final StackOverflowWebClient stackOverflowWebClient;
    private final BotWebClient botWebClient;
    private final static int MAXIMUM_BODY_SIZE = 20;
    private final static String URL = "https://stackoverflow.com/questions/";

    @Override
    @Transactional
    public int process(Link link) {
        String questionNumber = LinkUtils.extractStackOverflowInfoFromUrl(link.getUrl().toString());
        long number = Long.parseLong(questionNumber);
        StackOverflowResponse stackOverflowResponse =
            stackOverflowWebClient.fetchLatestAnswer(number);
        if (link.getLastApiUpdate() == null) {
            return 0;
        }
        if (stackOverflowResponse.getLastActivityDate().isAfter(link.getLastApiUpdate())) {
            List<Long> chatIds = linkRepository.findChatIdsByUrl(link.getUrl().toString());
            try {
                botWebClient.sendUpdate(new LinkUpdateRequest()
                    .setId(link.getId())
                    .setUrl(URI.create(link.getUrl()))
                    .setDescription(getDescription(stackOverflowResponse))
                    .setTgChatIds(chatIds));
            } catch (Exception ignored) {

            }
            linkRepository.setLastApiUpdate(link.getUrl().toString(), stackOverflowResponse.getLastActivityDate());
            return 1;
        }
        return 0;
    }

    @Override
    public boolean support(String url) {
        return  url.startsWith(URL);
    }

    @Override
    public String getDomain() {
        return "stackoverflow.com";
    }

    @Override
    @Transactional
    public void setLastUpdate(Link link) {
        String questionNumber = LinkUtils.extractStackOverflowInfoFromUrl(link.getUrl().toString());
        long number = Long.parseLong(questionNumber);
        StackOverflowResponse stackOverflowResponse =
            stackOverflowWebClient.fetchLatestAnswer(number);
        linkRepository.setLastApiUpdate(link.getUrl().toString(), stackOverflowResponse.getLastActivityDate());
    }

    private String getDescription(StackOverflowResponse stackOverflowResponse) {
        String link = URL + stackOverflowResponse.getQuestionId();
        String body = stackOverflowResponse.getBody();
        if (body.length() > MAXIMUM_BODY_SIZE) {
            body = body.substring(0, MAXIMUM_BODY_SIZE) + "...";
        }
        return String.format("Link: %s\nNew answer for question №%s\nBy: %s\nWith body:\n%s", link,
            stackOverflowResponse.getQuestionId(), stackOverflowResponse.getOwner().getDisplayName(), body);
    }
}
