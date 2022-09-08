package uz.everbestlab.votebudget.service.impl;

import jdk.jfr.RecordingState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.everbestlab.votebudget.dto.TelegramResultDto;
import uz.everbestlab.votebudget.service.BotService;
import uz.everbestlab.votebudget.service.SendMessageService;

@Service
@RequiredArgsConstructor
public class BotServiceImpl implements BotService {

    @Value("${bot-token}")
    private String TOKEN;

    private final String URL = "https://api.telegram.org/bot";

    private final RestTemplate restTemplate = new RestTemplate();
    private final SendMessageService sendMessageService;

    @Override
    public void getUpdate(Update update) {
        SendMessage sendMessage = new SendMessage();

        if (update.hasMessage()){
            Message message = update.getMessage();
            Long chatId = message.getChatId();

            if (message.hasText()) {
                String text = message.getText();

                switch (text){
                    case "/start":
                        sendMessage = sendMessageService.start(chatId);
                        break;

                    default:
                        if (text.length() != 6) {
                            sendMessage = sendMessageService.getPhoneNumber(chatId, text);
                        } else
                            sendMessage = sendMessageService.getSmsCode(chatId, text);
                        break;
                }
            }
        }

        String url = URL + TOKEN + "/sendMessage";
        restTemplate.postForObject(url, sendMessage, TelegramResultDto.class);

    }

}
