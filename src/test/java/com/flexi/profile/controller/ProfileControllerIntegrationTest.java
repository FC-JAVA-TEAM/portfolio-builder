package com.flexi.profile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexi.profile.dto.ProfileDTO;
import com.flexi.profile.model.Profile;
import com.flexi.profile.model.User;
import com.flexi.profile.repository.ProfileRepository;
import com.flexi.profile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private Profile testProfile;
    private User testUser;

    @BeforeEach
    void setUp() {
        profileRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser = userRepository.save(testUser);

        // Create test profile
        testProfile = new Profile();
        testProfile.setUser(testUser);
        testProfile.setName("Test User");
        testProfile.setBio("Test Bio");
        testProfile.setIsPublic(true);
        testProfile = profileRepository.save(testProfile);
    }

    // @Test
    // @WithMockUser(username = "test@example.com")
    // void testCreateProfile() throws Exception {
    //     ProfileDTO profileDTO = new ProfileDTO();
    //     profileDTO.setUserId(testUser.getId().toString());
    //     profileDTO.setName("New Profile");
    //     profileDTO.setBio("New Bio");
    //     profileDTO.setIsPublic(true);

    //     mockMvc.perform(post("/api/profiles")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(profileDTO)))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.userId", is(testUser.getId().toString())))
    //             .andExpect(jsonPath("$.userEmail", is(testUser.getEmail())))
    //             .andExpect(jsonPath("$.name", is("New Profile")))
    //             .andExpect(jsonPath("$.bio", is("New Bio")))
    //             .andExpect(jsonPath("$.isPublic", is(true)));
    // }

    // @Test
    // @WithMockUser(username = "test@example.com")
    // void testGetProfile() throws Exception {
    //     mockMvc.perform(get("/api/profiles/{id}", testProfile.getId()))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.id", is(testProfile.getId().intValue())))
    //             .andExpect(jsonPath("$.userId", is(testUser.getId().toString())))
    //             .andExpect(jsonPath("$.userEmail", is(testUser.getEmail())))
    //             .andExpect(jsonPath("$.name", is("Test User")))
    //             .andExpect(jsonPath("$.bio", is("Test Bio")))
    //             .andExpect(jsonPath("$.isPublic", is(true)));
    // }

    // @Test
    // @WithMockUser(username = "test@example.com")
    // void testUpdateProfile() throws Exception {
    //     ProfileDTO updatedProfile = new ProfileDTO();
    //     updatedProfile.setName("Updated User");
    //     updatedProfile.setBio("Updated Bio");
    //     updatedProfile.setIsPublic(false);

    //     mockMvc.perform(put("/api/profiles/{id}", testProfile.getId())
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(updatedProfile)))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.id", is(testProfile.getId().intValue())))
    //             .andExpect(jsonPath("$.userId", is(testUser.getId().toString())))
    //             .andExpect(jsonPath("$.userEmail", is(testUser.getEmail())))
    //             .andExpect(jsonPath("$.name", is("Updated User")))
    //             .andExpect(jsonPath("$.bio", is("Updated Bio")))
    //             .andExpect(jsonPath("$.isPublic", is(false)));
    // }

    // @Test
    // @WithMockUser(username = "test@example.com")
    // void testDeleteProfile() throws Exception {
    //     mockMvc.perform(delete("/api/profiles/{id}", testProfile.getId()))
    //             .andExpect(status().isNoContent());

    //     mockMvc.perform(get("/api/profiles/{id}", testProfile.getId()))
    //             .andExpect(status().isNotFound());
    // }

    // @Test
    // @WithMockUser(username = "test@example.com")
    // void testGetProfilesByUserId() throws Exception {
    //     mockMvc.perform(get("/api/profiles/user/{userId}", testUser.getId()))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$", hasSize(1)))
    //             .andExpect(jsonPath("$[0].userId", is(testUser.getId().toString())))
    //             .andExpect(jsonPath("$[0].userEmail", is(testUser.getEmail())))
    //             .andExpect(jsonPath("$[0].name", is("Test User")))
    //             .andExpect(jsonPath("$[0].isPublic", is(true)));
    // }

    // @Test
    // void testGetPublicProfiles() throws Exception {
    //     mockMvc.perform(get("/api/profiles/public"))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$", hasSize(1)))
    //             .andExpect(jsonPath("$[0].userId", is(testUser.getId().toString())))
    //             .andExpect(jsonPath("$[0].userEmail", is(testUser.getEmail())))
    //             .andExpect(jsonPath("$[0].name", is("Test User")))
    //             .andExpect(jsonPath("$[0].isPublic", is(true)));
    // }

    // @Test
    // @WithMockUser(username = "test@example.com")
    // void testDeleteNonExistentProfile() throws Exception {
    //     Long nonExistentId = 9999L;
    //     mockMvc.perform(delete("/api/profiles/{id}", nonExistentId))
    //             .andExpect(status().isNotFound());
    // }
}
