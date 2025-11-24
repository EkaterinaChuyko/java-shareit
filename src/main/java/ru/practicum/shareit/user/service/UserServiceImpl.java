package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Creating user with email {}", userDto.getEmail());
        checkEmailUniqueness(userDto.getEmail());

        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        log.info("User created with id {}", savedUser.getId());

        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto getById(Long id) {
        log.info("Fetching user with id {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        log.info("Updating user with id {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + id));

        if (userDto.getEmail() != null && !userDto.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
            checkEmailUniqueness(userDto.getEmail());
            existingUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }

        User updatedUser = userRepository.update(existingUser);
        log.info("User updated with id {}", updatedUser.getId());
        return userMapper.toDto(updatedUser);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting user with id {}", id);
        if (userRepository.findById(id).isEmpty()) {
            throw new UserNotFoundException("User not found: " + id);
        }
        userRepository.deleteById(id);
        log.info("User deleted with id {}", id);
    }

    private void checkEmailUniqueness(String email) {
        boolean exists = userRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
        if (exists) {
            log.warn("Email conflict detected for email {}", email);
            throw new EmailConflictException("Email already exists: " + email);
        }
    }
}