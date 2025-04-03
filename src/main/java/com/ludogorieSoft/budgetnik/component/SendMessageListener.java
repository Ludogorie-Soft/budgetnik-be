package com.ludogorieSoft.budgetnik.component;

import com.ludogorieSoft.budgetnik.event.OnSendMessageEvent;
import com.ludogorieSoft.budgetnik.model.Message;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.service.MessageService;
import com.ludogorieSoft.budgetnik.service.UserService;
import com.ludogorieSoft.budgetnik.service.impl.security.AuthServiceImpl;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SendMessageListener implements ApplicationListener<OnSendMessageEvent> {

  private final MessageService messageService;
  private final JavaMailSender mailSender;
  private final UserService userService;

  @Override
  public void onApplicationEvent(@NotNull OnSendMessageEvent event) {
    this.sendMessage(event);
  }

  private void sendMessage(OnSendMessageEvent event) {
    User user = userService.findByEmail(event.getEmail());
    Message promoMessage = messageService.findPromoMessageById(event.getMessageId());

    String recipientAddress = user.getEmail();
    String subject = "Как да спестиш с BUDGETникът";

    String message =
        "Здравей, "
            + user.getName().split(" ")[0]
            + "\n\n"
            + "Може да се възползваш от промоцията на "
            + promoMessage.getTitle()
            + "\n"
            + "За периода от "
            + promoMessage.getFromDate()
            + " до "
            + promoMessage.getToDate()
            + "\n"
            + "вземи отстъпка в размер на "
            + promoMessage.getDiscount()
            + "%"
            + "\n"
            + promoMessage.getLink()
            + "\n\n\n"
            + "Моля не отговаряйте на този имейл.\n\n"
            + "Поздрави,\n"
            + "Екипът на BUDGETникът!";

    SimpleMailMessage email = new SimpleMailMessage();
    email.setFrom("BUDGETникът");
    email.setReplyTo("BUDGETникът");
    email.setTo(recipientAddress);
    email.setSubject(subject);
    email.setText(message);

    mailSender.send(email);
  }
}
