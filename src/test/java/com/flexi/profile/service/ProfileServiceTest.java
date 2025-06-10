package com.flexi.profile.service;

import com.flexi.profile.dto.ProfileDTO;
import com.flexi.profile.model.Profile;
import com.flexi.profile.model.User;
import com.flexi.profile.repository.ProfileRepository;
import com.flexi.profile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private User testUser;
    private Profile testProfile;
    private ProfileDTO testProfileDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        testProfile = new Profile();
        testProfile.setId(1L);
        testProfile.setName("Test Profile");
        testProfile.setBio("Test Bio");
        testProfile.setIsPublic(true);
        testProfile.setUser(testUser);

        testProfileDTO = new ProfileDTO();
        testProfileDTO.setId(1L);
        testProfileDTO.setName("Test Profile");
        testProfileDTO.setBio("Test Bio");
        testProfileDTO.setIsPublic(true);
        testProfileDTO.setUserId(1L);
    }

    @Test
    void createProfile() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(profileRepository.save(any(Profile.class))).thenReturn(testProfile);

        ProfileDTO result = profileService.createProfile(testProfileDTO, "test@example.com");

        assertNotNull(result);
        assertEquals(testProfileDTO.getName(), result.getName());
        assertEquals(testProfileDTO.getBio(), result.getBio());
        assertEquals(testProfileDTO.getIsPublic(), result.getIsPublic());
        assertEquals(testUser.getId(), result.getUserId());

        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    void getProfilesByUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(profileRepository.findByUser(any(User.class))).thenReturn(Arrays.asList(testProfile));

        List<ProfileDTO> results = profileService.getProfilesByUserId(1L);

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(testProfileDTO.getName(), results.get(0).getName());

        verify(profileRepository).findByUser(any(User.class));
    }

    @Test
    void getPublicProfiles() {
        when(profileRepository.findByIsPublicTrue()).thenReturn(Arrays.asList(testProfile));

        List<ProfileDTO> results = profileService.getPublicProfiles();

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(testProfileDTO.getName(), results.get(0).getName());
        assertTrue(results.get(0).getIsPublic());

        verify(profileRepository).findByIsPublicTrue();
    }
}
