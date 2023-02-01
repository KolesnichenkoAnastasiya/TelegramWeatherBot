package bot;
import bot.service.TelegramBotNew;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@Component
@Configuration
public class FirstChatBotApplication {

    @SneakyThrows
    public static void main(String[] args) {
        TelegramBotNew bot = new TelegramBotNew();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        System.out.println(bot.getBotToken());
        System.out.println(bot.getBotUsername());
        telegramBotsApi.registerBot(bot);
    }
}
