package DataManager.repository;

import DataManager.model.relDB.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = ?1")
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u")
    List<User> findUsers();

    @Modifying
    @Transactional
    @Query("DELETE FROM User WHERE id = ?1")
    void deleteById(Long id);


    @Query("SELECT u FROM User u WHERE u.id = ?1")
    Optional<User> findById(Long id);
}
