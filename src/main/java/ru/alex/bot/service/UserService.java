package ru.alex.bot.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alex.bot.model.User;
import ru.alex.bot.repository.UserRepository;

@Service
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public User findByChatId(long id) {
    return userRepository.findByChatId(id);
  }

  @Transactional(readOnly = true)
  public List<User> findAllUsers() {
    return userRepository.findAll();
  }

  @Transactional
  public List<User> findSendUsers() {

    // Найдем всех подьзователей с флагом = false
    List<User> users = userRepository.findSendUsers();
    // Сразу же Установка всем этим пользователям флага  = true
    users.forEach((user) -> user.setSendInfo(true));
    userRepository.saveAll(users);
    return users;
  }

  @Transactional
  public void addUser(User user) {
    if (user.getName().equals("admin")) {
      user.setAdmin(true);
    }
    userRepository.save(user);
  }

  @Transactional
  public void updateUser(User user) {
    userRepository.save(user);
  }





}
