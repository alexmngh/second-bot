package ru.alex.bot.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.alex.bot.model.User;

//@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  @Query("SELECT u FROM User u WHERE u.sendInfo = false")
  List<User> findSendUsers();

  User findByChatId(long id);
}
