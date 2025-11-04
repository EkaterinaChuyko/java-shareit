package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserMapper {

    public UserDto toUserDto(User user) {
        if (user == null) return null;
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User toUser(UserDto userDto) {
        if (userDto == null) return null;
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    public UserDto toDto(User user) {
        return toUserDto(user);
    }

    public User toEntity(UserDto userDto) {
        return toUser(userDto);
    }
}