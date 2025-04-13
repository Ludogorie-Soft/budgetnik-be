package com.ludogorieSoft.budgetnik.component;

import com.ludogorieSoft.budgetnik.event.OnPaymentEvent;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.service.UserService;
import com.stripe.model.PaymentIntent;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentListener implements ApplicationListener<OnPaymentEvent> {

  private final JavaMailSender mailSender;
  private final UserService userService;

  @Value("${spring.mail.username}")
  private String gmail;

  @Override
  public void onApplicationEvent(OnPaymentEvent event) {

    User user = userService.findByEmail(event.getEmail());

    String recipientAddress = event.getEmail();
    String subject = "BUDGETникът - Payment";

    String message =
        "Уважаеми, "
            + user.getName()
            + "\n\n"
            + "Успешно извършихте плащане за абонамент в BUDGETникът!\n\n"
            + "Вашият абонамент е активен до "
            + LocalDate.now().plusMonths(1)
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
