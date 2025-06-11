package com.flexi.profile.config;

import com.flexi.profile.model.Role;
import com.flexi.profile.model.User;
import com.flexi.profile.repository.RoleRepository;
import com.flexi.profile.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@Profile("test")
public class TestDataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.insert-test-data:false}")
    private boolean insertTestData;

    @Override
    @Transactional
    public void run(String... args) {
        if (insertTestData) {
            insertDummyUsers();
        }
    }

    private void insertDummyUsers() {
        // Insert admin user
        createUserIfNotExists("admin@test.com", "Admin", "User", "password", "ROLE_ADMIN", "ROLE_USER");
        // Insert HR user
        createUserIfNotExists("hr@test.com", "HR", "Manager", "password", "ROLE_HR", "ROLE_USER");
        // Insert regular user
        createUserIfNotExists("user@test.com", "Regular", "User", "password", "ROLE_USER");
    }

    private void createUserIfNotExists(String email, String firstName, String lastName, String password, String... roleNames) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User();
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(passwordEncoder.encode(password));
            
            Set<Role> roles = new HashSet<>();
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
            user.setRoles(roles);
            
            userRepository.save(user);
        }
    }
}
