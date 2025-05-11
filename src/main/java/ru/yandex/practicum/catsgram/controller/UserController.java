package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Email должен быть указан");
        } else if (users.values().stream().anyMatch(userInMap -> user.getEmail().equals(userInMap.getEmail()))) {
            throw new DuplicatedDataException("Этот Email уже используется");
        }

        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User replace(@RequestBody User user) {
        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        } else if (users.values()
                .stream()
                .filter(userInMap -> !user.getId().equals(userInMap.getId()))
                .anyMatch(userInMap -> user.getEmail().equals(userInMap.getEmail()))) {
            throw new DuplicatedDataException("Этот email уже используется");
        } else if (user.getEmail() == null && user.getUsername() == null && user.getPassword() == null) {
            throw new ConditionsNotMetException("Должны быть указаны: email, имя пользователя и пароль");
        }

        user.setRegistrationDate(users.get(user.getId()).getRegistrationDate());
        users.replace(user.getId(), user);
        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


}
