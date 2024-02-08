package model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.model.BotImpl;
import org.junit.jupiter.api.Test;
import java.util.List;

public class BotImplTest {

    @Test
    public void testProcess() {

        BotImpl bot = spy(new BotImpl("SomeToken"));
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("Some command");
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);

        int res = bot.process(List.of(update));
        assertEquals(res, 0);

    }

}
