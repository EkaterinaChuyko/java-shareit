package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserService {

    private final RestTemplate restTemplate;
    private final String serverUrl = "http://localhost:9090/users";

    public UserDto create(UserDto userDto) {
        return restTemplate.postForObject(serverUrl, userDto, UserDto.class);
    }

    public UserDto getById(Long userId) {
        return restTemplate.getForObject(serverUrl + "/" + userId, UserDto.class);
    }

    public List<UserDto> getAll() {
        ResponseEntity<UserDto[]> response = restTemplate.getForEntity(serverUrl, UserDto[].class);
        return Arrays.asList(response.getBody());
    }

    public UserDto update(Long userId, UserDto userDto) {
        restTemplate.patchForObject(serverUrl + "/" + userId, userDto, UserDto.class);
        return getById(userId);
    }

    public void delete(Long userId) {
        restTemplate.delete(serverUrl + "/" + userId);
    }
}