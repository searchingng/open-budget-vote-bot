package uz.everbestlab.votebudget.repo;

import org.checkerframework.checker.nullness.Opt;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.everbestlab.votebudget.entity.PhoneToken;

import java.util.Optional;

public interface PhoneTokenRepository extends JpaRepository<PhoneToken, Long> {

    boolean existsByPhone(String phone);

    boolean existsByChatId(Long chatId);

    Optional<PhoneToken> findByChatId(Long chatId);

}
