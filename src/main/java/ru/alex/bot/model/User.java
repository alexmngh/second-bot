package ru.alex.bot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Id;

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
  private Boolean admin;
  // Пользователь которому следует отправялть информацию
  private Boolean sendInfo = true;
//  private Boolean sendInfo = false;

  public User() {
  }

  public User(Long chatId, Integer stateId) {
    this.chatId = chatId;
    this.stateId = stateId;
  }
}
