package uz.everbestlab.votebudget.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Code {

    private String application;
    private String otp;
    private String phone;
    private String token;

}
