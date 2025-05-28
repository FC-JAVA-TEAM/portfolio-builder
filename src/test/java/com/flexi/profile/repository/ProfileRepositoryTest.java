package com.flexi.profile.repository;

import com.flexi.profile.model.Profile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;

    // @Test
    // public void testSaveProfile() {
    //     Profile profile = new Profile();
    //     profile.setUserId("test@example.com");
    //     profile.setName("Test User");
    //     profile.setBio("Test Bio");
    //     profile.setIsPublic(true);

    //     Profile savedProfile = profileRepository.save(profile);

    //     assertThat(savedProfile.getId()).isNotNull();
    //     assertThat(savedProfile.getUserId()).isEqualTo("test@example.com");
    //     assertThat(savedProfile.getName()).isEqualTo("Test User");
    //     assertThat(savedProfile.getBio()).isEqualTo("Test Bio");
    //     assertThat(savedProfile.getIsPublic()).isTrue();
    // }

    // @Test
    // public void testFindByUserId() {
    //     Profile profile = new Profile();
    //     profile.setUserId("test@example.com");
    //     profile.setName("Test User");
    //     profileRepository.save(profile);

    //     List<Profile> foundProfiles = profileRepository.findByUserId("test@example.com");

    //     assertThat(foundProfiles).hasSize(1);
    //     assertThat(foundProfiles.get(0).getName()).isEqualTo("Test User");
    // }

    // @Test
    // public void testFindByIsPublic() {
    //     Profile publicProfile = new Profile();
    //     publicProfile.setUserId("public@example.com");
    //     publicProfile.setIsPublic(true);
    //     profileRepository.save(publicProfile);

    //     Profile privateProfile = new Profile();
    //     privateProfile.setUserId("private@example.com");
    //     privateProfile.setIsPublic(false);
    //     profileRepository.save(privateProfile);

    //     List<Profile> publicProfiles = profileRepository.findByIsPublic(true);

    //     assertThat(publicProfiles).hasSize(1);
    //     assertThat(publicProfiles.get(0).getUserId()).isEqualTo("public@example.com");
    // }
}
