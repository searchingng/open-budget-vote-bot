package uz.everbestlab.votebudget.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelegramResultDto {

    private boolean ok;
    private Message result;

}
