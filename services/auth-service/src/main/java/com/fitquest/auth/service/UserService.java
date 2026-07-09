package com.fitquest.auth.service;

import com.fitquest.auth.dto.UpdateUserRequest;
import com.fitquest.auth.dto.UserDto;
import com.fitquest.auth.entity.User;
import com.fitquest.auth.entity.UserProfile;
import com.fitquest.auth.exception.BadRequestException;
import com.fitquest.auth.exception.NotFoundException;
import com.fitquest.auth.feign.GamificationClient;
import com.fitquest.auth.mapper.UserMapper;
import com.fitquest.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final GamificationClient gamificationClient;

    public UserDto getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    public GamificationClient.UserXpResponse getCurrentUserXp(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        return gamificationClient.getUserXp(userId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> listUsers(String roles) {
        requireCoach(roles);
        return userRepository.findAllByOrderByUsernameAsc().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public UserDto updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        UserProfile profile = user.getProfile();
        if (profile == null) {
            profile = UserProfile.builder().user(user).build();
            user.setProfile(profile);
        }
        if (request.getDisplayName() != null) profile.setDisplayName(request.getDisplayName());
        if (request.getAvatarUrl() != null) profile.setAvatarUrl(request.getAvatarUrl());
        if (request.getAge() != null) profile.setAge(request.getAge());
        if (request.getWeightKg() != null) profile.setWeightKg(request.getWeightKg());
        if (request.getHeightCm() != null) profile.setHeightCm(request.getHeightCm());
        if (request.getFitnessGoal() != null) profile.setFitnessGoal(request.getFitnessGoal());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getTheme() != null) profile.setTheme(request.getTheme());
        return userMapper.toDto(userRepository.save(user));
    }

    private void requireCoach(String roles) {
        if (!(hasRole(roles, "ROLE_COACH") || hasRole(roles, "ROLE_ADMIN"))) {
            throw new BadRequestException("Only coaches can list users");
        }
    }

    private boolean hasRole(String roles, String role) {
        return roles != null && List.of(roles.split(",")).contains(role);
    }
}
