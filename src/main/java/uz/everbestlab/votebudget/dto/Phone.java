package uz.everbestlab.votebudget.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phone {

    private String application;

    private String key;

    private String phone;

}
