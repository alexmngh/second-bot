package ru.alex.bot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
  @Id
  @GeneratedValue
  private Long id;
  // Идентификатор пользователя для бота
  private Long chatId;
  // текущее состояние бота
  private Integer stateId;
  private String name;

  public User() {
  }

  public User(Long chatId, Integer stateId) {
    this.chatId = chatId;
    this.stateId = stateId;
  }
}
