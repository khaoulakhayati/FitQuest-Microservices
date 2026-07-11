package com.fitquest.auth.mapper;

import com.fitquest.auth.dto.UserDto;
import com.fitquest.auth.dto.UserProfileDto;
import com.fitquest.auth.entity.User;
import com.fitquest.auth.entity.UserProfile;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T18:24:48+0100",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.profile( toProfileDto( user.getProfile() ) );
        userDto.id( user.getId() );
        userDto.email( user.getEmail() );
        userDto.username( user.getUsername() );

        userDto.roles( mapRoles(user) );

        return userDto.build();
    }

    @Override
    public UserProfileDto toProfileDto(UserProfile profile) {
        if ( profile == null ) {
            return null;
        }

        UserProfileDto.UserProfileDtoBuilder userProfileDto = UserProfileDto.builder();

        userProfileDto.displayName( profile.getDisplayName() );
        userProfileDto.avatarUrl( profile.getAvatarUrl() );
        userProfileDto.age( profile.getAge() );
        userProfileDto.weightKg( profile.getWeightKg() );
        userProfileDto.heightCm( profile.getHeightCm() );
        userProfileDto.fitnessGoal( profile.getFitnessGoal() );
        userProfileDto.bio( profile.getBio() );
        userProfileDto.theme( profile.getTheme() );

        return userProfileDto.build();
    }
}
