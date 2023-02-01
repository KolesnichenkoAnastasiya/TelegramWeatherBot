package bot.service;

import bot.entity.TimeInterval;
import bot.weather.weatherDayForecast;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.io.IOException;
import java.util.*;
import static jakarta.ws.rs.HttpMethod.GET;



@Slf4j
@EnableScheduling
@Configuration
public class TelegramBotNew extends TelegramLongPollingBot {
    private final TimeIntervalModeService timeIntervalModeService = TimeIntervalModeService.getInstance();
    private String botUsernameProp, botTokenProp;
    private String requestPart_1, requestPart_3;
    private String urlString;
    TimeInterval selectedTimeInterval;
    StringBuffer response = null;

    private void getProperty() {
        FileInputStream fis;
        Properties property = new Properties();
        try {
            fis = new FileInputStream("src/main/resources/bot/config/config.properties");
            property.load(fis);
            botUsernameProp= property.getProperty("bot.name");
            botTokenProp = property.getProperty("bot.token");
            requestPart_1 = property.getProperty("requestPart_1");
            requestPart_3 = property.getProperty("requestPart_3");
        }  catch (IOException e) {
            System.err.println("ОШИБКА: Файл свойств отсутствует!");
        }
    }

    private String urlRequest (String city) throws IOException {
        urlString = requestPart_1 + city + requestPart_3;
        URL urlObject = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod(GET);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = connection.getResponseCode();
        if (responseCode == 404) {
            throw new IllegalArgumentException();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return weatherOut(WorkWithJson.weatherAllDayList(response.toString()));
    }

    private String getTimeIntervalButton (TimeInterval saved, TimeInterval timeInterval) {
        selectedTimeInterval = saved;
        return saved==timeInterval ? timeInterval + "  ✅" : timeInterval.getText();
    }

    @Override
    public String getBotUsername() {
        getProperty();
        return botUsernameProp;
    }

    @Override
    public String getBotToken() {
        getProperty();
        return botTokenProp;
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()){
            handleCallback(update.getCallbackQuery());
        } else if (update.hasMessage()) {
            handleMessage(update.getMessage());
        }
    }

    @SneakyThrows
    private void handleCallback(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        String [] param = callbackQuery.getData().split(":");
        String action = param[0];
        TimeInterval newTimeInterval = TimeInterval.timeIntervalByText(param[1]);
        switch (action) {
            case "ORIGINAL":
                timeIntervalModeService.setOriginalTimeInterval(message.getChatId(), newTimeInterval);
                break;
        }
        List <List< InlineKeyboardButton>> buttons = new ArrayList<>();
        printButtonsForChoose(message, buttons);
        execute(
                EditMessageReplyMarkup.builder()
                        .chatId(message.getChatId().toString())
                        .messageId(message.getMessageId())
                        .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                        .build());
    }

    @SneakyThrows
    private void handleMessage(Message message) {
        if (message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntity =
                    message.getEntities().stream().filter(e-> "bot_command".equals(e.getType())).findFirst();
            if (commandEntity.isPresent()){
                String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                switch (command){
                    case "/start":
                    case "/set_locality":
                        List <List< InlineKeyboardButton>> buttons = new ArrayList<>();
                        printButtonsForChoose(message, buttons);
                        execute(
                                SendMessage.builder()
                                        .text("Узнать прогноз погоды на: ")
                                        .chatId(message.getChatId().toString())
                                        .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                                        .build());
                        execute(
                                SendMessage.builder()
                                        .text("Введите название населенного пункта: ")
                                        .chatId(message.getChatId().toString())
                                        .build());
                        break;
                    default:
                        execute(SendMessage.builder().text("Команда не распознана").chatId(message.getChatId().toString()).build());
                        return;
                }
            }
        }  else if (message.hasText()){
            String weather = urlRequest(message.getText());
            execute(
                    SendMessage.builder()
                            .text(weather)
                            .chatId(message.getChatId().toString())
                            .build());

        }
    }

    @SneakyThrows
    public void printButtonsForChoose (Message message,  List <List< InlineKeyboardButton>> buttons) {
        TimeInterval originalTimeInterval= timeIntervalModeService.getOriginalTimeInterval(message.getChatId());
        for (TimeInterval timeInterval: TimeInterval.values()) {
            buttons.add(
                    Arrays.asList(
                            InlineKeyboardButton.builder()
                                    .text(getTimeIntervalButton(originalTimeInterval, timeInterval))
                                    .callbackData("ORIGINAL:" + timeInterval)
                                    .build()));

        }
    }

    private String weatherOut (weatherDayForecast [] weatherDayForecastsFormatter){
            switch (selectedTimeInterval) {
            case NOW:
                return weatherDayForecastsFormatter[0].toString();
            case TOMORROW:
                return weatherDayForecastsFormatter[0].toString() + weatherDayForecastsFormatter[1].toString();
            case THREE_DAYS:
                return weatherDayForecastsFormatter[0].toString() + weatherDayForecastsFormatter[1].toString() +
                        weatherDayForecastsFormatter[2].toString();
            default:
                return weatherDayForecastsFormatter[0].toString() + weatherDayForecastsFormatter[1].toString() +
                        weatherDayForecastsFormatter[2].toString() + weatherDayForecastsFormatter[3].toString() +
                        weatherDayForecastsFormatter[4].toString() + weatherDayForecastsFormatter[5].toString();
            }
    }
}
