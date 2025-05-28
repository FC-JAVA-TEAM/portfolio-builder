package com.flexi.profile.repository;

import com.flexi.profile.model.Profile;
import com.flexi.profile.model.Section;
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
public class SectionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SectionRepository sectionRepository;

    private User testUser;
    private Profile testProfile;
    private Section testSection;

    @BeforeEach
    public void setup() {
        // Create test user
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser = entityManager.persist(testUser);

        // Create test profile
        testProfile = new Profile();
        testProfile.setUser(testUser);
        testProfile.setName("Test User");
        testProfile.setBio("Test Bio");
        testProfile.setIsPublic(true);
        testProfile = entityManager.persist(testProfile);

        // Create test section
        testSection = new Section();
        testSection.setProfile(testProfile);
        testSection.setType("EXPERIENCE");
        testSection.setTitle("Work Experience");
        testSection.setDisplayOrder(1);
        testSection.setBackgroundUrl("https://example.com/bg.jpg");
        testSection = entityManager.persist(testSection);

        entityManager.flush();
    }

    // @Test
    // public void testSaveSection() {
    //     Section section = new Section();
    //     section.setProfile(testProfile);
    //     section.setType("EDUCATION");
    //     section.setTitle("Education");
    //     section.setDisplayOrder(2);
    //     section.setBackgroundUrl("https://example.com/edu.jpg");

    //     Section savedSection = sectionRepository.save(section);

    //     assertThat(savedSection.getId()).isNotNull();
    //     assertThat(savedSection.getProfile().getId()).isEqualTo(testProfile.getId());
    //     assertThat(savedSection.getType()).isEqualTo("EDUCATION");
    //     assertThat(savedSection.getTitle()).isEqualTo("Education");
    //     assertThat(savedSection.getDisplayOrder()).isEqualTo(2);
    //     assertThat(savedSection.getBackgroundUrl()).isEqualTo("https://example.com/edu.jpg");
    // }

    // @Test
    // public void testFindByProfileId() {
    //     Section section2 = new Section();
    //     section2.setProfile(testProfile);
    //     section2.setType("EDUCATION");
    //     section2.setTitle("Education");
    //     section2.setDisplayOrder(2);
    //     entityManager.persist(section2);
    //     entityManager.flush();

    //     List<Section> sections = sectionRepository.findByProfileIdOrderByDisplayOrder(testProfile.getId());

    //     assertThat(sections).hasSize(2);
    //     assertThat(sections.get(0).getTitle()).isEqualTo("Work Experience");
    //     assertThat(sections.get(1).getTitle()).isEqualTo("Education");
    // }

    // @Test
    // public void testDeleteSection() {
    //     sectionRepository.deleteById(testSection.getId());
    //     entityManager.flush();
    //     entityManager.clear();

    //     List<Section> sections = sectionRepository.findByProfileIdOrderByDisplayOrder(testProfile.getId());
    //     assertThat(sections).isEmpty();
    // }

    // @Test
    // public void testUpdateSectionOrder() {
    //     testSection.setDisplayOrder(3);
    //     Section updatedSection = sectionRepository.save(testSection);
    //     entityManager.flush();
    //     entityManager.clear();

    //     Section reloadedSection = sectionRepository.findById(updatedSection.getId()).orElse(null);
    //     assertThat(reloadedSection).isNotNull();
    //     assertThat(reloadedSection.getDisplayOrder()).isEqualTo(3);
    // }
}
