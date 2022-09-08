package uz.everbestlab.votebudget.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.everbestlab.votebudget.service.BotService;

@RestController
@RequestMapping("/api/telegram")
@RequiredArgsConstructor
public class BotController {

    private final BotService botService;

    @PostMapping
    public void getUpdates(@RequestBody Update update){
        botService.getUpdate(update);
    }

}
