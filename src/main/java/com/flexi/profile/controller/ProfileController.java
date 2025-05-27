package com.flexi.profile.controller;

import com.flexi.profile.dto.ProfileDTO;
import com.flexi.profile.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(origins = "http://localhost:1111")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @PostMapping
    public ResponseEntity<ProfileDTO> createProfile(@RequestBody ProfileDTO profileDTO) {
        ProfileDTO createdProfile = profileService.createProfile(profileDTO);
        return ResponseEntity.ok(createdProfile);
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileDTO> getProfile(@PathVariable Long profileId) {
        ProfileDTO profile = profileService.getProfile(profileId);
        return ResponseEntity.ok(profile);
    }

    @GetMapping
    public ResponseEntity<List<ProfileDTO>> getAllProfiles() {
        List<ProfileDTO> profiles = profileService.getAllProfiles();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProfileDTO>> getProfilesByUserId(@PathVariable String userId) {
        List<ProfileDTO> profiles = profileService.getProfilesByUserId(userId);
        return ResponseEntity.ok(profiles);
    }

    @PutMapping("/{profileId}")
    public ResponseEntity<ProfileDTO> updateProfile(
            @PathVariable Long profileId,
            @RequestBody ProfileDTO profileDTO) {
        ProfileDTO updatedProfile = profileService.updateProfile(profileId, profileDTO);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long profileId) {
        profileService.deleteProfile(profileId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/public")
    public ResponseEntity<List<ProfileDTO>> getPublicProfiles() {
        List<ProfileDTO> publicProfiles = profileService.getPublicProfiles();
        return ResponseEntity.ok(publicProfiles);
    }
}
