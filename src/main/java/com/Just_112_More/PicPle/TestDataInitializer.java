package com.Just_112_More.PicPle;

import com.Just_112_More.PicPle.user.domain.User;
import com.Just_112_More.PicPle.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.findByUsername("rudls").isEmpty()) {
            User user = new User();
            user.setUserName("rudals");
            user.setPassword(passwordEncoder.encode("2828")); // ✅ 암호화해서 저장
            user.setRole("ROLE_USER");
            userRepository.save(user);
            System.out.println("Test user created!");
        }
    }
}