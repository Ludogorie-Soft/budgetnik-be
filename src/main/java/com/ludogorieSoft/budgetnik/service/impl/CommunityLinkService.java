package com.ludogorieSoft.budgetnik.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Getter
@Service
@RequiredArgsConstructor
public class CommunityLinkService {

  @Value("${community.link.viber}")
  private String viberLink;

  @Value("${community.link.telegram}")
  private String telegramLink;

}
