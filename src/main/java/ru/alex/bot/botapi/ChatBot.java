package ru.alex.bot.botapi;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.alex.bot.model.User;
import ru.alex.bot.service.UserService;

@Component
@PropertySource("classpath:telegram.properties")
@Slf4j
public class ChatBot extends TelegramLongPollingBot {

  /**
   * Это админские команды для админа
   * broadcast - рассылка по всей базе, пробел нужен для того что после broadcast неоьходимо
   * ввести текст сообщения который будет отправлен все пользователям
   * users - вывести список пользователей
   */

  private static final String BROADCAST = "broadcast ";
  private static final String LIST_USERS = "users";

  @Value("${bot.name}")
  private String botName;

  @Value("${bot.token}")
  private String botToken;

  private final UserService userService;

  public ChatBot(UserService userService) {
    this.userService = userService;
  }


  /**
   * Этот метод вызывается автоматом когда пользователь что-то вводит
   * бот может работать с пользователем только после того как пользователь ему что-то отправит,
   * например нажмет кнопку старт
   * @param update
   */
  @Override
  public void onUpdateReceived(Update update) {
    // Убедимся что пришло не пустое сообщение
    if (!update.hasMessage() || !update.getMessage().hasText()) {
      return;
    }
    final String text = update.getMessage().getText();
    final long chatId = update.getMessage().getChatId();

    User user = userService.findByChatId(chatId);

    if (checkIfAdminCommand(user, text)) {
      return;
    }


    BotContext context;
    BotState state;

    /** Если этого пользователя еще нет в базе, те это новый пользователь */
    if (user == null) {
      state = BotState.getInitialState();
      // Указываем что пользователь находится в начальном состояниии
      user = new User(chatId, state.ordinal());
      userService.addUser(user);

      // Схраняем все неоьходимое для обработки state
      context = BotContext.of(this, user, text);
      // Войти в это состояние
      state.enter(context);
      log.info("add new user : " + chatId);
    } else {
      // Так же формируем контекст
      context = BotContext.of(this, user, text);
      // Но состояние берем уже из базы номер его состояние и по номеру получаем объект
      state = BotState.byId(user.getStateId());
      log.info("Update recive styate " + chatId);
    }

    // Обработать то что ввел пользователь , когда он нахходится в каком то состоянии
    state.handleInput(context);
    /**
     * цикл нужен для того что есть состояниния в которых не нужно ждать ввода ползователя
     * т.е. мы проскакиваем все состояния пока не дойдем до того в котором есть ожидание ввода
     * */
    do {
      /**   * получаем следующее состояние    */
      state = state.nextState();
      /**   * переходим в него    */
      state.enter(context);
    } while (!state.isInputNeeded());
    /**   назначим пользователю текущий state как рабочий   */
    user.setStateId(state.ordinal());
    /**   * и состояем это состояние в базе    */
    userService.updateUser(user);


  }

  @Override
  public String getBotUsername() {
    return botName;
  }

  @Override
  public String getBotToken() {
    return botToken;
  }

  /**
   * Обработка команд от админа
   * @param user
   * @param text
   * @return
   */
  private boolean checkIfAdminCommand(User user, String text) {
    // Убедимсся что пользователь админ
    if (user == null || !user.getAdmin()) {
      return false;
    }
    if (text.startsWith(BROADCAST)) {
      log.info("Admin command: " + BROADCAST);
      // вырежем тескт который идет полсе команды
      text = text.substring(BROADCAST.length());
      broadcast(text);
      return true;
    } else if (text.equals(LIST_USERS)) {
      log.info("Admin command: " + LIST_USERS);
      listUsers(user);
      return true;
    }
    return false;
  }

  /**
   * НА указанный id отпраляет текс сообощения
   * @param chatId
   * @param text
   */
  private void sendMessage(long chatId, String text) {
    SendMessage message = new SendMessage()
        .setChatId(chatId)
        .setText(text);
    try {
      // Этот метод и отправляет сообщение
      execute(message);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }

  /**
   * Выводит список пользователей
   * @param admin
   */
  private void listUsers(User admin) {
    StringBuilder sb = new StringBuilder("All users list: \r\n");
    List<User> users = userService.findAllUsers();

    users.forEach(user ->
        sb.append(user.getId())
          .append(" ")
          .append(user.getName())
          .append("\r\n")
    );
    // отправляем сообщение
    sendMessage(admin.getChatId(), sb.toString());
  }

  /**
   * Отправить сообщение всем польжвателям
   * @param text
   */
  private void broadcast(String text) {
    List<User> users = userService.findAllUsers();
    users.forEach(user ->
        sendMessage(user.getChatId(), text)
    );
  }

}
