package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;

@Component
public class UserStorageImpl implements UserStorage {

    private final HashMap<Integer, User> users = new HashMap<>();
    private final Set<String> emailUniqSet = new HashSet<>();
    private int id = 1;


    @Override
    public User createUser(User user) {
        user.setId(id);
        check(user);
        id++;
        users.put(user.getId(), user);
        emailUniqSet.add(user.getEmail());
        return user;
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> ans = new ArrayList<>();
        for (User o : users.values()) {
            ans.add(UserMapper.toUserDto(o));
        }
        return ans;
    }

    @Override
    public User getUser(int id) {
        return users.get(id);
    }

    @Override
    public void deleteUser(int id) {
        User origUser = getUser(id);
        emailUniqSet.remove(origUser.getEmail());
        users.remove(id);
    }

    @Override
    public User patchUser(User user) {
        User orUser = getUser(user.getId());
        if (!orUser.getEmail().equals(user.getEmail())) {
            check(user);
        }
        users.put(user.getId(), user);
        emailUniqSet.add(user.getEmail());
        return user;
    }


    private void check(User user) {
        if (emailUniqSet.contains(user.getEmail())) {
            throw new RuntimeException("Почта не уникальна");
        }
        User origUser = getUser(user.getId());
        if (origUser != null) {
            emailUniqSet.remove(origUser.getEmail());
        }
    }
}
