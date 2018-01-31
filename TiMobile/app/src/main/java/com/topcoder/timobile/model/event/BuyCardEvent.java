package com.topcoder.timobile.model.event;

import com.topcoder.timobile.model.Card;

import lombok.Data;

@Data
public class BuyCardEvent {
  Card card;
  String event;

  public BuyCardEvent(Card card, String event) {
    this.event = event;
    this.card = card;
  }
}
