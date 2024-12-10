package com.ludogorieSoft.budgetnik.component;

import com.ludogorieSoft.budgetnik.event.OnPasswordResetRequestEvent;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.service.UserService;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordResetListener implements ApplicationListener<OnPasswordResetRequestEvent> {

  private final JavaMailSender mailSender;
  private final UserService userService;

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
    String subject = "Budgetnikat - Промяна на парола";

    String message =
        "Уважаеми, "
            + user.getName()
            + "\n\n"
            + "Вие сте направили заявка за промяна на паролата в Budgetnikat.\n\n"
            + "За да промените своята парола, моля въведете шест цифрения код в приложението и следвайте инструкциите:\n"
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
