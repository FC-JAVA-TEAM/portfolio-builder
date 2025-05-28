package com.flexi.profile.repository;

import com.flexi.profile.model.Profile;
import com.flexi.profile.model.Section;
import com.flexi.profile.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SectionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SectionRepository sectionRepository;

    private User testUser;
    private Profile testProfile;
    private Section testSection1;
    private Section testSection2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        entityManager.persist(testUser);

        testProfile = new Profile();
        testProfile.setName("Test Profile");
        testProfile.setBio("Test Bio");
        testProfile.setIsPublic(true);
        testProfile.setUser(testUser);
        entityManager.persist(testProfile);

        testSection1 = new Section();
        testSection1.setTitle("Section 1");
        testSection1.setContent("Content 1");
        testSection1.setProfile(testProfile);
        testSection1.setOrderIndex(0);
        entityManager.persist(testSection1);

        testSection2 = new Section();
        testSection2.setTitle("Section 2");
        testSection2.setContent("Content 2");
        testSection2.setProfile(testProfile);
        testSection2.setOrderIndex(1);
        entityManager.persist(testSection2);

        entityManager.flush();
    }

    @Test
    void findByProfileOrderByOrderIndexAsc() {
        List<Section> sections = sectionRepository.findByProfileOrderByOrderIndexAsc(testProfile);

        assertNotNull(sections);
        assertEquals(2, sections.size());
        assertEquals("Section 1", sections.get(0).getTitle());
        assertEquals("Section 2", sections.get(1).getTitle());
        assertEquals(0, sections.get(0).getOrderIndex());
        assertEquals(1, sections.get(1).getOrderIndex());
    }
}
