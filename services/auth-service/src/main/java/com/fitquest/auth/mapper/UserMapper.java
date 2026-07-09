package com.fitquest.auth.mapper;

import com.fitquest.auth.dto.UserDto;
import com.fitquest.auth.dto.UserProfileDto;
import com.fitquest.auth.entity.User;
import com.fitquest.auth.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", expression = "java(mapRoles(user))")
    @Mapping(target = "profile", source = "profile")
    UserDto toDto(User user);

    UserProfileDto toProfileDto(UserProfile profile);

    default java.util.Set<String> mapRoles(User user) {
        return user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet());
    }
}
