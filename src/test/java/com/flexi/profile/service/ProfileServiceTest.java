package com.flexi.profile.service;

import com.flexi.profile.dto.ProfileDTO;
import com.flexi.profile.model.Profile;
import com.flexi.profile.model.User;
import com.flexi.profile.repository.ProfileRepository;
import com.flexi.profile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

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
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        testProfile = new Profile();
        testProfile.setId(1L);
        testProfile.setUser(testUser);
        testProfile.setName("Test User");
        testProfile.setBio("Test Bio");
        testProfile.setIsPublic(true);

        testProfileDTO = new ProfileDTO();
        testProfileDTO.setId(1L);
        testProfileDTO.setUserId("1");
        testProfileDTO.setUserEmail("test@example.com");
        testProfileDTO.setName("Test User");
        testProfileDTO.setBio("Test Bio");
        testProfileDTO.setIsPublic(true);
    }

    @Test
    void testCreateProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.save(any(Profile.class))).thenReturn(testProfile);

        ProfileDTO result = profileService.createProfile(testProfileDTO);

        assertThat(result.getUserId()).isEqualTo(testProfileDTO.getUserId());
        assertThat(result.getUserEmail()).isEqualTo(testProfileDTO.getUserEmail());
        assertThat(result.getName()).isEqualTo(testProfileDTO.getName());
        assertThat(result.getBio()).isEqualTo(testProfileDTO.getBio());
        assertThat(result.getIsPublic()).isEqualTo(testProfileDTO.getIsPublic());

        verify(userRepository, times(1)).findById(1L);
        verify(profileRepository, times(1)).save(any(Profile.class));
    }

    @Test
    void testGetProfile() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));

        ProfileDTO result = profileService.getProfile(1L);

        assertThat(result.getId()).isEqualTo(testProfile.getId());
        assertThat(result.getUserId()).isEqualTo(testProfile.getUser().getId().toString());
        assertThat(result.getUserEmail()).isEqualTo(testProfile.getUser().getEmail());
        assertThat(result.getName()).isEqualTo(testProfile.getName());

        verify(profileRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProfilesByUserId() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.findByUser(testUser)).thenReturn(Arrays.asList(testProfile));

        List<ProfileDTO> results = profileService.getProfilesByUserId("1");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getUserId()).isEqualTo("1");
        assertThat(results.get(0).getUserEmail()).isEqualTo("test@example.com");

        verify(userRepository, times(1)).findById(1L);
        verify(profileRepository, times(1)).findByUser(testUser);
    }

    @Test
    void testUpdateProfile() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        when(profileRepository.save(any(Profile.class))).thenReturn(testProfile);

        ProfileDTO updatedDTO = new ProfileDTO();
        updatedDTO.setName("Updated Name");
        updatedDTO.setBio("Updated Bio");
        updatedDTO.setIsPublic(false);

        ProfileDTO result = profileService.updateProfile(1L, updatedDTO);

        assertThat(result.getName()).isEqualTo(updatedDTO.getName());
        assertThat(result.getBio()).isEqualTo(updatedDTO.getBio());
        assertThat(result.getIsPublic()).isEqualTo(updatedDTO.getIsPublic());

        verify(profileRepository, times(1)).findById(1L);
        verify(profileRepository, times(1)).save(any(Profile.class));
    }

    @Test
    void testGetPublicProfiles() {
        when(profileRepository.findByIsPublic(true)).thenReturn(Arrays.asList(testProfile));

        List<ProfileDTO> results = profileService.getPublicProfiles();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIsPublic()).isTrue();

        verify(profileRepository, times(1)).findByIsPublic(true);
    }

    @Test
    void testDeleteProfile() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        doNothing().when(profileRepository).delete(testProfile);

        profileService.deleteProfile(1L);

        verify(profileRepository, times(1)).findById(1L);
        verify(profileRepository, times(1)).delete(testProfile);
    }

    @Test
    void testDeleteProfileNotFound() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.deleteProfile(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Profile not found");

        verify(profileRepository, times(1)).findById(1L);
        verify(profileRepository, never()).delete(any(Profile.class));
    }
}
