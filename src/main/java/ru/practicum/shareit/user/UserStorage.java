package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    List<UserDto> getAllUsers();

    User getUser(int id);

    void deleteUser(int id);

    User patchUser(User user);
}
