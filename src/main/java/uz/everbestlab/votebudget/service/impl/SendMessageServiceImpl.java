package uz.everbestlab.votebudget.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import uz.everbestlab.votebudget.dto.Code;
import uz.everbestlab.votebudget.dto.Phone;
import uz.everbestlab.votebudget.dto.Result;
import uz.everbestlab.votebudget.entity.PhoneToken;
import uz.everbestlab.votebudget.repo.PhoneTokenRepository;
import uz.everbestlab.votebudget.service.SendMessageService;

import javax.ws.rs.core.MultivaluedHashMap;
import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SendMessageServiceImpl implements SendMessageService {

    @Value("${application-id}")
    private String APP;
    @Value("${application-key}")
    private String KEY;

    private final String PHONE_URL = "https://admin.openbudget.uz/api/v1/user/validate_phone/";
    private final String CODE_URL = "https://admin.openbudget.uz/api/v1/user/temp/vote/";

    private final RestTemplate restTemplate = new RestTemplate();
    private final PhoneTokenRepository phoneTokenRepository;

    public SendMessage start(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setParseMode("HTML");

        sendMessage.setText("📞 Telefon raqamingizi kiriting <b>[991234567]</b>:");
        return sendMessage;
    }

    @Override
    public SendMessage getPhoneNumber(Long chatId, String text) {
        String response = sendPhoneNumber(chatId, text);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(response);
        return sendMessage;
    }

    @Override
    public SendMessage getSmsCode(Long chatId, String text) {
        String response = sendSmsCode(chatId, text);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(response);
        return sendMessage;
    }

    private String sendPhoneNumber(Long chatId, String text) {
        String response = "📞 Iltimos telefon raqamini <b>[991234567]</b> ko'rinishida kiriting";
        if (text.length() != 9)
            return response;

        String requestPhone = "998" + text;
        Phone phone = new Phone(APP, KEY, requestPhone);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RequestEntity<Result> entity = new RequestEntity(phone, headers, HttpMethod.POST, URI.create(PHONE_URL));
        Result result = null;

        try {
            ResponseEntity<Result> responseEntity = restTemplate.exchange(entity, Result.class);
            result = responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getResponseBodyAsString().equals("{\"detail\":\"This number was used to vote\"}")) {
                return "❗ Bu raqam allaqachon ovoz bergan";
            } else if (e.getResponseBodyAsString().startsWith("{\"detail\":\"Запрос был проигнорирован"))
                return "❗ Ko'p urindingiz. Iltimos bir ozdan keyin urunib ko'ring.";
            else
                return e.getMessage();
        } catch (HttpServerErrorException e) {
            return e.getMessage();
        }

        if (result == null)
            return response;

        savePhoneToken(chatId, requestPhone, result.getToken());
        return "📨 Sizga yuborilgan SMS kodni kiriting";

    }

    private String sendSmsCode(Long chatId, String text){
        String response = "❗ SMS kod xato. Kodni kiriting";

        if (!phoneTokenRepository.existsByChatId(chatId))
            return "❗ Telefon raqam kirtmagansiz";

        PhoneToken phoneToken = phoneTokenRepository.findByChatId(chatId).get();
        Code code = new Code(APP, text, phoneToken.getPhone(), phoneToken.getToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RequestEntity<Code> entity = new RequestEntity(code, headers, HttpMethod.POST, URI.create(CODE_URL));

        ResponseEntity<Result> responseEntity;

        try {
            responseEntity = restTemplate.exchange(entity, Result.class);
        } catch (HttpClientErrorException e){
            return response + "\n" + e.getMessage();
        } catch (HttpServerErrorException e) {
            return e.getMessage();
        }

        if (responseEntity.getStatusCodeValue() == 200)
            return "✅ Siz muvaffaqiyatli ovoz berdingiz!";

        return response;
    }

    private void savePhoneToken(Long chatId, String text, String token) {
        PhoneToken phoneToken;
        if (phoneTokenRepository.existsByChatId(chatId))
            phoneToken = phoneTokenRepository.findByChatId(chatId).get();
        else
            phoneToken = new PhoneToken();

        phoneToken.setPhone(text);
        phoneToken.setChatId(chatId);
        phoneToken.setToken(token);
        phoneTokenRepository.save(phoneToken);
    }

}
