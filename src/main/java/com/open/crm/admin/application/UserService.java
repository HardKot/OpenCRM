package com.open.crm.admin.application;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.open.crm.admin.application.events.SendUserEmail;
import com.open.crm.admin.application.exceptions.UserException;
import com.open.crm.admin.application.interfaces.IUserRepository;
import com.open.crm.admin.entities.user.PasswordType;
import com.open.crm.admin.entities.user.User;
import com.open.crm.core.application.repositories.IEmployeeRepository;
import com.open.crm.core.entities.employee.Employee;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final IUserRepository userRepository;
    private final IEmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    private String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private Pattern EMAIL_REGEX = Pattern
            .compile("^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)*$");

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public User createUser(User data) {
        if (Objects.isNull(data.getUsername()) || data.getUsername().isBlank())
            throw new UserException("Username cannot be empty");
        if (data.getUsername().length() > 255)
            throw new UserException("Username cannot be longer than 255 characters");
        if (!EMAIL_REGEX.matcher(data.getUsername()).matches())
            throw new UserException("Username is not valid");
        if (userRepository.existsByUsername(data.getUsername()))
            throw new UserException("Username is already taken");

        User user = new User();
        user.setUsername(data.getUsername());
        user.setTenant(data.getTenant());
        user.setEmployeeId(data.getEmployeeId());
        if (Objects.nonNull(data.getPassword())) {
            PasswordType passwordType = getPasswordType(data.getPassword());
            if (passwordType == PasswordType.WEAK) {
                throw new UserException("Password is too weak");
            }
            user.setPassword(passwordEncoder.encode(data.getPassword()));
        }
        userRepository.save(user);

        Employee employee = employeeRepository.findById(data.getEmployeeId())
                .orElseThrow(() -> new UserException("Employee not found with id: " + data.getEmployeeId()));

        eventPublisher.publishEvent(
                new SendUserEmail(this,
                        employee.getEmail(),
                        "Welcome!",
                        "welcome-email",
                        Map.of("username", user.getUsername(), "password", data.getPassword())));

        return user;
    }

    public User updatePassword(User user, String password) {
        PasswordType passwordType = getPasswordType(password);
        if (passwordType == PasswordType.WEAK) {
            throw new UserException("Password is too weak");
        }
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public String generatePassword() {
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();

    }

    public PasswordType getPasswordType(String password) {
        int score = 0;

        if (password.length() >= 6)
            score++;
        if (password.length() >= 10)
            score++;
        if (password.matches("(?=.*[0-9]).*"))
            score++;
        if (password.matches("(?=.*[a-z]).*"))
            score++;
        if (password.matches("(?=.*[A-Z]).*"))
            score++;

        if (score >= 5) {
            return PasswordType.HARD;
        } else if (score >= 3) {
            return PasswordType.MEDIUM;
        } else if (score >= 1) {
            return PasswordType.SIMPLE;
        } else {
            return PasswordType.WEAK;
        }
    }

    public boolean matchPassword(String password, String hash) {
        return passwordEncoder.matches(password, hash);
    }
}
