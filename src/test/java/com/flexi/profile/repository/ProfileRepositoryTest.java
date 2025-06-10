package com.flexi.profile.repository;

import com.flexi.profile.model.Profile;
import com.flexi.profile.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ProfileRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProfileRepository profileRepository;

    private User testUser;
    private Profile testProfile;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser = entityManager.persist(testUser);

        testProfile = new Profile();
        testProfile.setUser(testUser);
        testProfile.setName("Test Profile");
        testProfile.setBio("Test Bio");
        testProfile.setIsPublic(true);
        testProfile = entityManager.persist(testProfile);

        entityManager.flush();
    }

    // @Test
    // public void testSaveProfile() {
    //     Profile savedProfile = profileRepository.findById(testProfile.getId()).orElse(null);

    //     assertThat(savedProfile).isNotNull();
    //     assertThat(savedProfile.getId()).isNotNull();
    //     assertThat(savedProfile.getUser().getId()).isEqualTo(testUser.getId());
    //     assertThat(savedProfile.getName()).isEqualTo("Test Profile");
    //     assertThat(savedProfile.getBio()).isEqualTo("Test Bio");
    //     assertThat(savedProfile.getIsPublic()).isTrue();
    // }

    // @Test
    // public void testFindByUser() {
    //     List<Profile> foundProfiles = profileRepository.findByUser(testUser);

    //     assertThat(foundProfiles).hasSize(1);
    //     assertThat(foundProfiles.get(0).getName()).isEqualTo("Test Profile");
    // }

    // @Test
    // public void testFindByIsPublic() {
    //     User privateUser = new User();
    //     privateUser.setEmail("private@example.com");
    //     privateUser.setPassword("password");
    //     privateUser.setFirstName("Private");
    //     privateUser.setLastName("User");
    //     privateUser = entityManager.persist(privateUser);

    //     Profile privateProfile = new Profile();
    //     privateProfile.setUser(privateUser);
    //     privateProfile.setName("Private Profile");
    //     privateProfile.setIsPublic(false);
    //     entityManager.persist(privateProfile);

    //     entityManager.flush();

    //     List<Profile> publicProfiles = profileRepository.findByIsPublic(true);

    //     assertThat(publicProfiles).hasSize(1);
    //     assertThat(publicProfiles.get(0).getName()).isEqualTo("Test Profile");
    // }
}
