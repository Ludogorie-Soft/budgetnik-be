package com.ludogorieSoft.budgetnik.component;

import com.ludogorieSoft.budgetnik.event.OnConfirmRegistrationEvent;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.service.UserService;
import jakarta.validation.constraints.NotNull;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfirmRegistrationListener implements ApplicationListener<OnConfirmRegistrationEvent> {

  private final UserService userService;
  private final JavaMailSender mailSender;

  @Override
  public void onApplicationEvent(@NotNull OnConfirmRegistrationEvent event) {
    this.confirmRegistration(event);
  }

  private void confirmRegistration(OnConfirmRegistrationEvent event) {
    User user = userService.findByEmail(event.getEmail());
    Random random = new Random();
    int randomNumber =  100000 + random.nextInt(900000);
    String token = String.valueOf(randomNumber);
    userService.createVerificationToken(user, token);

    String recipientAddress = user.getEmail();
    String subject = "Budgetnikat - Потвърждение на регистрация";

    String message =
        "Уважаеми, "
            + user.getName()
            + "\n\n"
            + "Благодарим Ви за регистрацията в Budgetnikat!\n\n"
            + "За да завършите вашата регистрация, моля въведете шест цифрения код в приложението:\n"
            + token
            + "\n\n\n"
            + "Моля не отговаряйте на този имейл.\n\n"
            + "Поздрави,\n"
            + "Екипът на Budgetnikat!";

    SimpleMailMessage email = new SimpleMailMessage();
    email.setFrom("no-reply");
    email.setReplyTo("no-reply");
    email.setTo(recipientAddress);
    email.setSubject(subject);
    email.setText(message);

    mailSender.send(email);
  }
}
