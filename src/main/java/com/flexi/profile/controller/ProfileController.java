package com.flexi.profile.controller;

import com.flexi.profile.dto.ProfileDTO;
import com.flexi.profile.dto.SectionDTO;
import com.flexi.profile.service.ProfileService;
import com.flexi.profile.util.LogUtil;
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
        LogUtil.logMethodEntry(logger, "createProfile", profileDTO, authentication);
        String userEmail = authentication.getName();
        ProfileDTO createdProfile = profileService.createProfile(profileDTO, userEmail);
        LogUtil.logMethodExit(logger, "createProfile", createdProfile);
        return ResponseEntity.ok(createdProfile);
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileDTO> getProfile(@PathVariable Long profileId) {
        LogUtil.logMethodEntry(logger, "getProfile", profileId);
        try {
            ProfileDTO profile = profileService.getProfile(profileId);
            LogUtil.logMethodExit(logger, "getProfile", profile);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            LogUtil.logError(logger, "Error fetching profile", e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ProfileDTO>> getAllProfiles() {
        LogUtil.logMethodEntry(logger, "getAllProfiles");
        List<ProfileDTO> profiles = profileService.getAllProfiles();
        LogUtil.logMethodExit(logger, "getAllProfiles", profiles);
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProfileDTO>> getProfilesByUserId(@PathVariable Long userId) {
        LogUtil.logMethodEntry(logger, "getProfilesByUserId", userId);
        List<ProfileDTO> profiles = profileService.getProfilesByUserId(userId);
        LogUtil.logMethodExit(logger, "getProfilesByUserId", profiles);
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/my-profiles")
    public ResponseEntity<List<ProfileDTO>> getMyProfiles(Authentication authentication) {
        LogUtil.logMethodEntry(logger, "getMyProfiles", authentication);
        String userEmail = authentication.getName();
        List<ProfileDTO> profiles = profileService.getProfilesByUserEmail(userEmail);
        LogUtil.logMethodExit(logger, "getMyProfiles", profiles);
        return ResponseEntity.ok(profiles);
    }

    @PutMapping("/{profileId}")
    public ResponseEntity<ProfileDTO> updateProfile(
            @PathVariable Long profileId,
            @RequestBody ProfileDTO profileDTO) {
        LogUtil.logMethodEntry(logger, "updateProfile", profileId, profileDTO);
        ProfileDTO updatedProfile = profileService.updateProfile(profileId, profileDTO);
        LogUtil.logMethodExit(logger, "updateProfile", updatedProfile);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long profileId) {
        LogUtil.logMethodEntry(logger, "deleteProfile", profileId);
        try {
            profileService.deleteProfile(profileId);
            LogUtil.logMethodExit(logger, "deleteProfile");
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            LogUtil.logError(logger, "Error deleting profile", e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/public")
    public ResponseEntity<List<ProfileDTO>> getPublicProfiles() {
        LogUtil.logMethodEntry(logger, "getPublicProfiles");
        List<ProfileDTO> publicProfiles = profileService.getPublicProfiles();
        LogUtil.logMethodExit(logger, "getPublicProfiles", publicProfiles);
        return ResponseEntity.ok(publicProfiles);
    }

    @PostMapping("/{profileId}/sections")
    public ResponseEntity<SectionDTO> createSection(@PathVariable Long profileId, @RequestBody SectionDTO sectionDTO) {
        LogUtil.logMethodEntry(logger, "createSection", profileId, sectionDTO);
        sectionDTO.setProfileId(profileId);
        SectionDTO createdSection = profileService.createSection(sectionDTO);
        LogUtil.logMethodExit(logger, "createSection", createdSection);
        return ResponseEntity.ok(createdSection);
    }

    @GetMapping("/{profileId}/sections/{sectionId}")
    public ResponseEntity<SectionDTO> getSection(@PathVariable Long profileId, @PathVariable Long sectionId) {
        LogUtil.logMethodEntry(logger, "getSection", profileId, sectionId);
        SectionDTO section = profileService.getSection(sectionId);
        LogUtil.logMethodExit(logger, "getSection", section);
        return ResponseEntity.ok(section);
    }

    @PutMapping("/{profileId}/sections/{sectionId}")
    public ResponseEntity<SectionDTO> updateSection(@PathVariable Long profileId, @PathVariable Long sectionId, @RequestBody SectionDTO sectionDTO) {
        LogUtil.logMethodEntry(logger, "updateSection", profileId, sectionId, sectionDTO);
        sectionDTO.setProfileId(profileId);
        SectionDTO updatedSection = profileService.updateSection(sectionId, sectionDTO);
        LogUtil.logMethodExit(logger, "updateSection", updatedSection);
        return ResponseEntity.ok(updatedSection);
    }

    @DeleteMapping("/{profileId}/sections/{sectionId}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long profileId, @PathVariable Long sectionId) {
        LogUtil.logMethodEntry(logger, "deleteSection", profileId, sectionId);
        profileService.deleteSection(sectionId);
        LogUtil.logMethodExit(logger, "deleteSection");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{profileId}/sections/reorder")
    public ResponseEntity<Void> reorderSections(@PathVariable Long profileId, @RequestBody List<Long> sectionIds) {
        LogUtil.logMethodEntry(logger, "reorderSections", profileId, sectionIds);
        profileService.reorderSections(profileId, sectionIds);
        LogUtil.logMethodExit(logger, "reorderSections");
        return ResponseEntity.ok().build();
    }
}
