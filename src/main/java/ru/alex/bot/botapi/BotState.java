package ru.alex.bot.botapi;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
/**
 * Данная конструкция это упрощенная версия патерна стайт (State)
 */
public enum BotState {
  Start {
    @Override
    public void enter(BotContext context) {
      sendMessage(context, "Hello");
    }

    @Override
    public BotState nextState() {
      return EnterName;
    }
  },
  EnterName {
    @Override
    public void enter(BotContext context) {
      sendMessage(context, "Enter Phone");
    }

    @Override
    public void handleInput(BotContext context) {
      // Получаем контекст нашего ползователя и устанавливаем ему то что было введено пользователем
      context.getUser().setName(context.getInput());
    }

    @Override
    public BotState nextState() {
      return Approved;
    }
  },

  Approved( true)
  {
    @Override
    public void enter(BotContext context) {
      sendMessage(context, "Gut your Application");
    }

    @Override
      public BotState nextState() {
        return Start;
    }
  };

  // В этом массиве будут храниться все значения этого класса Numa т.е. все состояния
  private static BotState[] states;
  // Флаг говорит о том надо ли ждать ввода информации от ползователя
  private final boolean inputNeeded;

  BotState() {
    this.inputNeeded = true;
  }

  BotState(boolean inputNeeded) {
    this.inputNeeded = inputNeeded;
  }

  /**
   * При инициализвации переходим в нулевой состояние
   * @return
   */
  public static BotState getInitialState() {
    return byId(0);
  }

  /**
   * По номеру возвращает состояние
   * byId - это номер в этом массиве
   * @param id
   * @return
   */
  public static BotState byId(int id) {
    if (states == null) {
      // Этот метод берет все значения нумерайшена по очереди и формирует из них массив
      states = BotState.values();
    }
    return states[id];
  }

  /**
   * Этот метод позволяет отпарвить одно сообщение нашему пользователю
   * @param context
   * @param text
   */
  protected void sendMessage(BotContext context, String text) {
    SendMessage message = new SendMessage()
        .setChatId(context.getUser().getChatId())
        .setText(text);
    try {
      // Этот метод и отправляет сообщение
      context.getChatBot().execute(message);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }

  public boolean isInputNeeded() {
    return inputNeeded;
  }

  // Этот метод обрабатывает бот пользователя в текущем состоянии
  public void handleInput(BotContext context) {

  }

  /**
   * Это тметод вводит пользователя в опаределнное состояние
   * @param context
   */
  public abstract void enter(BotContext context);

  /**
   * Говорит в какое состояние нужно переходить после того как обработано тукущее
   * @return
   */
  public abstract BotState nextState();




}
