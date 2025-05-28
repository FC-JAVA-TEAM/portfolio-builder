package com.flexi.profile.repository;

import com.flexi.profile.model.Profile;
import com.flexi.profile.model.Section;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class SectionRepositoryTest {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private Profile testProfile;

    // @BeforeEach
    // public void setup() {
    //     testProfile = new Profile();
    //     testProfile.setUserId("test@example.com");
    //     testProfile.setName("Test User");
    //     testProfile = profileRepository.save(testProfile);
    // }

    // @Test
    // public void testSaveSection() {
    //     Section section = new Section();
    //     section.setProfile(testProfile);
    //     section.setType("EXPERIENCE");
    //     section.setTitle("Work Experience");
    //     section.setDisplayOrder(1);
    //     section.setBackgroundUrl("https://example.com/bg.jpg");

    //     Section savedSection = sectionRepository.save(section);

    //     assertThat(savedSection.getId()).isNotNull();
    //     assertThat(savedSection.getProfile().getId()).isEqualTo(testProfile.getId());
    //     assertThat(savedSection.getType()).isEqualTo("EXPERIENCE");
    //     assertThat(savedSection.getTitle()).isEqualTo("Work Experience");
    //     assertThat(savedSection.getDisplayOrder()).isEqualTo(1);
    //     assertThat(savedSection.getBackgroundUrl()).isEqualTo("https://example.com/bg.jpg");
    // }

    // @Test
    // public void testFindByProfileId() {
    //     Section section1 = new Section();
    //     section1.setProfile(testProfile);
    //     section1.setType("EXPERIENCE");
    //     section1.setTitle("Work Experience");
    //     section1.setDisplayOrder(1);
    //     sectionRepository.save(section1);

    //     Section section2 = new Section();
    //     section2.setProfile(testProfile);
    //     section2.setType("EDUCATION");
    //     section2.setTitle("Education");
    //     section2.setDisplayOrder(2);
    //     sectionRepository.save(section2);

    //     List<Section> sections = sectionRepository.findByProfileIdOrderByDisplayOrder(testProfile.getId());

    //     assertThat(sections).hasSize(2);
    //     assertThat(sections.get(0).getTitle()).isEqualTo("Work Experience");
    //     assertThat(sections.get(1).getTitle()).isEqualTo("Education");
    // }

    // @Test
    // public void testDeleteSection() {
    //     Section section = new Section();
    //     section.setProfile(testProfile);
    //     section.setType("EXPERIENCE");
    //     section.setTitle("Work Experience");
    //     section.setDisplayOrder(1);
    //     Section savedSection = sectionRepository.save(section);

    //     sectionRepository.deleteById(savedSection.getId());

    //     List<Section> sections = sectionRepository.findByProfileIdOrderByDisplayOrder(testProfile.getId());
    //     assertThat(sections).isEmpty();
    // }

    // @Test
    // public void testUpdateSectionOrder() {
    //     Section section = new Section();
    //     section.setProfile(testProfile);
    //     section.setType("EXPERIENCE");
    //     section.setTitle("Work Experience");
    //     section.setDisplayOrder(1);
    //     Section savedSection = sectionRepository.save(section);

    //     savedSection.setDisplayOrder(2);
    //     Section updatedSection = sectionRepository.save(savedSection);

    //     assertThat(updatedSection.getDisplayOrder()).isEqualTo(2);
    // }
}
