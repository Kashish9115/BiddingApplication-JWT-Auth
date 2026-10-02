package com.example.BiddingApplication.repo;
import com.example.BiddingApplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User,Integer> {
 Optional<User> findByEmailIgnoreCaseAndDeletedFalse(String email);
}
