package com.flexi.profile.controller;

import com.flexi.profile.dto.ProfileDTO;
import com.flexi.profile.dto.SectionDTO;
import com.flexi.profile.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(origins = "http://localhost:1111")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private ProfileService profileService;

    @PostMapping
    public ResponseEntity<ProfileDTO> createProfile(@RequestBody ProfileDTO profileDTO, Authentication authentication) {
        String userEmail = authentication.getName();
        logger.info("Creating new profile for user: {}", userEmail);
        profileDTO.setUserEmail(userEmail);
        ProfileDTO createdProfile = profileService.createProfile(profileDTO);
        return ResponseEntity.ok(createdProfile);
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileDTO> getProfile(@PathVariable Long profileId) {
        try {
            logger.info("Fetching profile with id: {}", profileId);
            ProfileDTO profile = profileService.getProfile(profileId);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            logger.error("Error fetching profile with id: {}", profileId, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ProfileDTO>> getAllProfiles() {
        logger.info("Fetching all profiles");
        List<ProfileDTO> profiles = profileService.getAllProfiles();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProfileDTO>> getProfilesByUserId(@PathVariable String userId) {
        logger.info("Fetching profiles for user: {}", userId);
        List<ProfileDTO> profiles = profileService.getProfilesByUserId(userId);
        return ResponseEntity.ok(profiles);
    }

    @PutMapping("/{profileId}")
    public ResponseEntity<ProfileDTO> updateProfile(
            @PathVariable Long profileId,
            @RequestBody ProfileDTO profileDTO) {
        logger.info("Updating profile with id: {}", profileId);
        ProfileDTO updatedProfile = profileService.updateProfile(profileId, profileDTO);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long profileId) {
        try {
            logger.info("Deleting profile with id: {}", profileId);
            profileService.deleteProfile(profileId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("Error deleting profile with id: {}", profileId, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/public")
    public ResponseEntity<List<ProfileDTO>> getPublicProfiles() {
        logger.info("Fetching all public profiles");
        List<ProfileDTO> publicProfiles = profileService.getPublicProfiles();
        return ResponseEntity.ok(publicProfiles);
    }

    @PostMapping("/{profileId}/sections")
    public ResponseEntity<SectionDTO> createSection(@PathVariable Long profileId, @RequestBody SectionDTO sectionDTO) {
        logger.info("Creating new section for profile: {}", profileId);
        sectionDTO.setProfileId(profileId);
        SectionDTO createdSection = profileService.createSection(sectionDTO);
        return ResponseEntity.ok(createdSection);
    }

    @GetMapping("/{profileId}/sections/{sectionId}")
    public ResponseEntity<SectionDTO> getSection(@PathVariable Long profileId, @PathVariable Long sectionId) {
        logger.info("Fetching section {} for profile {}", sectionId, profileId);
        SectionDTO section = profileService.getSection(sectionId);
        return ResponseEntity.ok(section);
    }

    @PutMapping("/{profileId}/sections/{sectionId}")
    public ResponseEntity<SectionDTO> updateSection(@PathVariable Long profileId, @PathVariable Long sectionId, @RequestBody SectionDTO sectionDTO) {
        logger.info("Updating section {} for profile {}", sectionId, profileId);
        sectionDTO.setProfileId(profileId);
        SectionDTO updatedSection = profileService.updateSection(sectionId, sectionDTO);
        return ResponseEntity.ok(updatedSection);
    }

    @DeleteMapping("/{profileId}/sections/{sectionId}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long profileId, @PathVariable Long sectionId) {
        logger.info("Deleting section {} from profile {}", sectionId, profileId);
        profileService.deleteSection(sectionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{profileId}/sections/reorder")
    public ResponseEntity<Void> reorderSections(@PathVariable Long profileId, @RequestBody List<Long> sectionIds) {
        logger.info("Reordering sections for profile {}", profileId);
        profileService.reorderSections(profileId, sectionIds);
        return ResponseEntity.ok().build();
    }
}
