package com.flexi.profile;

import com.flexi.profile.dto.ProfileDTO;
import com.flexi.profile.model.Profile;
import com.flexi.profile.model.User;
import com.flexi.profile.repository.ProfileRepository;
import com.flexi.profile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("e2e")
public class EndToEndTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String baseUrl;
    private User testUser;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/api";
        profileRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser = userRepository.save(testUser);
    }

    @Test
    public void testCreateAndRetrieveProfile() {
        // Create a new profile
        ProfileDTO newProfile = new ProfileDTO();
        newProfile.setUserId(testUser.getId().toString());
        newProfile.setName("Test User");
        newProfile.setBio("This is a test bio");
        newProfile.setIsPublic(true);

        ResponseEntity<ProfileDTO> createResponse = restTemplate.postForEntity(
            baseUrl + "/profiles", 
            newProfile, 
            ProfileDTO.class
        );
        
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().getId()).isNotNull();
        assertThat(createResponse.getBody().getUserId()).isEqualTo(testUser.getId().toString());
        assertThat(createResponse.getBody().getUserEmail()).isEqualTo(testUser.getEmail());

        Long profileId = createResponse.getBody().getId();

        // Retrieve the created profile
        ResponseEntity<ProfileDTO> getResponse = restTemplate.getForEntity(
            baseUrl + "/profiles/" + profileId, 
            ProfileDTO.class
        );
        
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getName()).isEqualTo("Test User");
        assertThat(getResponse.getBody().getBio()).isEqualTo("This is a test bio");
        assertThat(getResponse.getBody().getIsPublic()).isTrue();
        assertThat(getResponse.getBody().getUserEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    public void testUpdateProfile() {
        // Create a new profile
        Profile profile = new Profile();
        profile.setUser(testUser);
        profile.setName("Test User");
        profile.setBio("Initial bio");
        profile.setIsPublic(true);
        Profile savedProfile = profileRepository.save(profile);

        // Update the profile
        ProfileDTO updateDTO = new ProfileDTO();
        updateDTO.setName("Updated User");
        updateDTO.setBio("Updated bio");
        updateDTO.setIsPublic(false);

        restTemplate.put(baseUrl + "/profiles/" + savedProfile.getId(), updateDTO);

        // Retrieve the updated profile
        ResponseEntity<ProfileDTO> getResponse = restTemplate.getForEntity(
            baseUrl + "/profiles/" + savedProfile.getId(), 
            ProfileDTO.class
        );
        
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getName()).isEqualTo("Updated User");
        assertThat(getResponse.getBody().getBio()).isEqualTo("Updated bio");
        assertThat(getResponse.getBody().getIsPublic()).isFalse();
        assertThat(getResponse.getBody().getUserEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    public void testDeleteProfile() {
        // Create a new profile
        Profile profile = new Profile();
        profile.setUser(testUser);
        profile.setName("Test User");
        Profile savedProfile = profileRepository.save(profile);

        // Delete the profile
        restTemplate.delete(baseUrl + "/profiles/" + savedProfile.getId());

        // Try to retrieve the deleted profile
        ResponseEntity<ProfileDTO> getResponse = restTemplate.getForEntity(
            baseUrl + "/profiles/" + savedProfile.getId(), 
            ProfileDTO.class
        );
        
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetProfilesByUserId() {
        // Create a new profile
        Profile profile = new Profile();
        profile.setUser(testUser);
        profile.setName("Test User");
        profile.setBio("Test Bio");
        profile.setIsPublic(true);
        profileRepository.save(profile);

        // Get profiles by user ID
        ResponseEntity<ProfileDTO[]> getResponse = restTemplate.getForEntity(
            baseUrl + "/profiles/user/" + testUser.getId(), 
            ProfileDTO[].class
        );
        
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().length).isEqualTo(1);
        assertThat(getResponse.getBody()[0].getUserId()).isEqualTo(testUser.getId().toString());
        assertThat(getResponse.getBody()[0].getUserEmail()).isEqualTo(testUser.getEmail());
        assertThat(getResponse.getBody()[0].getName()).isEqualTo("Test User");
    }
}
