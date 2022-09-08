package uz.everbestlab.votebudget.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface SendMessageService {

    SendMessage start(Long chatId);

    SendMessage getPhoneNumber(Long chatId, String text);

    SendMessage getSmsCode(Long chatId, String text);
}
