package ru.alex.bot.botapi;

import lombok.Getter;
import lombok.Setter;
import ru.alex.bot.model.User;

/**
 * Это объект который дает три доругих объекта
 * т.е. это комплекный объект котороый содерожит ссылки на все объекты которые могут понадобиться
 */
@Getter
@Setter
public class BotContext {
  // Этот библтотечный объект реализует бота
  private final ChatBot chatBot;
  // Это Сущность пользователя для которого описаны этит состояния
  private final User user;
  // Это то что пользователь ввел на данном этапе в ответ на вопрос
  private final String input;

  public static BotContext of (ChatBot chatBot, User user, String text) {
    return new BotContext(chatBot, user, text);
  }

  public BotContext(ChatBot chatBot, User user, String input) {
    this.chatBot = chatBot;
    this.user = user;
    this.input = input;
  }
}
