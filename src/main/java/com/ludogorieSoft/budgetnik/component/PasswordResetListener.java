package com.ludogorieSoft.budgetnik.component;

import com.ludogorieSoft.budgetnik.event.OnPasswordResetRequestEvent;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.service.UserService;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordResetListener implements ApplicationListener<OnPasswordResetRequestEvent> {

  private final JavaMailSender mailSender;
  private final UserService userService;

  @Value("${spring.mail.username}")
  private String gmail;

  @Override
  public void onApplicationEvent(OnPasswordResetRequestEvent event) {
    sendPasswordResetEmail(event);
  }

  private void sendPasswordResetEmail(OnPasswordResetRequestEvent event) {
    User user = userService.findByEmail(event.getEmail());
    Random random = new Random();
    int randomNumber = 100000 + random.nextInt(900000);
    String token = String.valueOf(randomNumber);
    userService.createVerificationToken(user, token);

    String recipientAddress = user.getEmail();
    String subject = "BUDGETникът - Промяна на парола";

    String message =
        "Уважаеми, "
            + user.getName()
            + "\n\n"
            + "Вие сте направили заявка за промяна на паролата в BUDGETникът.\n\n"
            + "За да промените своята парола, моля въведете шест цифрения код в приложението и следвайте инструкциите:\n"
            + token
            + "\n\n\n"
            + "Моля не отговаряйте на този имейл.\n\n"
            + "Поздрави,\n"
            + "Екипът на BUDGETникът!";

    SimpleMailMessage email = new SimpleMailMessage();
    email.setFrom(gmail);
    email.setReplyTo(gmail);
    email.setTo(recipientAddress);
    email.setSubject(subject);
    email.setText(message);

    mailSender.send(email);
  }
}
