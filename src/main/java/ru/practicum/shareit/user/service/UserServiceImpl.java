package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserServiceImpl implements UserService {
    private final Map<Long, User> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public UserDto create(UserDto dto) {
        boolean emailExists = storage.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(dto.getEmail()));
        if (emailExists) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        User user = UserMapper.toUser(dto);
        user.setId(idGenerator.getAndIncrement());
        storage.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long id, UserDto dto) {
        User user = storage.get(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }

        if (dto.getEmail() != null) {
            boolean emailExists = storage.values().stream()
                    .anyMatch(u -> !u.getId().equals(id) &&
                            u.getEmail().equalsIgnoreCase(dto.getEmail()));
            if (emailExists) {
                throw new RuntimeException("Пользователь с таким email уже существует");
            }
        }

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getById(Long id) {
        User user = storage.get(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return storage.values().stream().map(UserMapper::toUserDto).toList();
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }
}
