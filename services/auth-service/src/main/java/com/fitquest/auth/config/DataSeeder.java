package com.fitquest.auth.config;

import com.fitquest.auth.entity.Permission;
import com.fitquest.auth.entity.Role;
import com.fitquest.auth.entity.User;
import com.fitquest.auth.entity.UserProfile;
import com.fitquest.auth.repository.PermissionRepository;
import com.fitquest.auth.repository.RoleRepository;
import com.fitquest.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedData() {
        return args -> {
            Permission readProfile = permissionRepository.findAll().stream()
                    .filter(p -> "READ_PROFILE".equals(p.getName()))
                    .findFirst()
                    .orElseGet(() -> permissionRepository.save(Permission.builder().name("READ_PROFILE").description("Read profile").build()));
            Permission writeProfile = permissionRepository.findAll().stream()
                    .filter(p -> "WRITE_PROFILE".equals(p.getName()))
                    .findFirst()
                    .orElseGet(() -> permissionRepository.save(Permission.builder().name("WRITE_PROFILE").description("Write profile").build()));

            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").permissions(Set.of(readProfile, writeProfile)).build()));
            Role coachRole = roleRepository.findByName("ROLE_COACH")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_COACH").permissions(Set.of(readProfile, writeProfile)).build()));
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_ADMIN").permissions(Set.of(readProfile, writeProfile)).build()));

            if (!userRepository.existsByEmail("demo@fitquest.com")) {
                User demo = User.builder()
                        .email("demo@fitquest.com")
                        .username("demouser")
                        .password(passwordEncoder.encode("Demo1234!"))
                        .roles(Set.of(userRole))
                        .build();
                demo.setProfile(UserProfile.builder()
                        .user(demo)
                        .displayName("Demo Champion")
                        .fitnessGoal("MUSCLE_GAIN")
                        .age(28)
                        .weightKg(75.0)
                        .heightCm(178.0)
                        .theme("light")
                        .build());
                userRepository.save(demo);
            }

            if (!userRepository.existsByEmail("coach@fitquest.com")) {
                User coach = User.builder()
                        .email("coach@fitquest.com")
                        .username("coach")
                        .password(passwordEncoder.encode("Coach1234!"))
                        .roles(Set.of(coachRole))
                        .build();
                coach.setProfile(UserProfile.builder()
                        .user(coach)
                        .displayName("Coach Taylor")
                        .fitnessGoal("COACHING")
                        .theme("light")
                        .build());
                userRepository.save(coach);
            }

            if (!userRepository.existsByEmail("admin@fitquest.com")) {
                User admin = User.builder()
                        .email("admin@fitquest.com")
                        .username("admin")
                        .password(passwordEncoder.encode("Admin1234!"))
                        .roles(Set.of(adminRole))
                        .build();
                admin.setProfile(UserProfile.builder().user(admin).displayName("FitQuest Admin").build());
                userRepository.save(admin);
            }

            log.info("Auth service seed data loaded (demo@fitquest.com / Demo1234!, coach@fitquest.com / Coach1234!)");
        };
    }
}
