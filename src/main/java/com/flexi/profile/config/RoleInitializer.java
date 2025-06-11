package com.flexi.profile.config;

import com.flexi.profile.model.Role;
import com.flexi.profile.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RoleInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializeRoles();
    }

    private void initializeRoles() {
        createRoleIfNotFound("ROLE_USER");
        createRoleIfNotFound("ROLE_HR");
        createRoleIfNotFound("ROLE_ADMIN");
    }

    private void createRoleIfNotFound(String name) {
        if (roleRepository.findByName(name).isEmpty()) {
            Role role = new Role(name);
            roleRepository.save(role);
        }
    }
}
