package com.flexi.profile.controller;

import com.flexi.profile.dto.SectionDTO;
import com.flexi.profile.dto.SubSectionDTO;
import com.flexi.profile.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sections")
@CrossOrigin(origins = "http://localhost:1111")
@PreAuthorize("isAuthenticated()")
public class SectionController {

    @Autowired
    private ProfileService profileService;

    @PostMapping
    public ResponseEntity<SectionDTO> createSection(@RequestBody SectionDTO sectionDTO) {
        SectionDTO createdSection = profileService.createSection(sectionDTO);
        return ResponseEntity.ok(createdSection);
    }

    @PostMapping("/{sectionId}/subsections")
    public ResponseEntity<SubSectionDTO> createSubSection(@PathVariable Long sectionId, @RequestBody SubSectionDTO subSectionDTO) {
        SubSectionDTO createdSubSection = profileService.createSubSection(sectionId, subSectionDTO);
        return ResponseEntity.ok(createdSubSection);
    }

    @GetMapping("/{sectionId}")
    public ResponseEntity<SectionDTO> getSection(@PathVariable Long sectionId) {
        SectionDTO section = profileService.getSection(sectionId);
        return ResponseEntity.ok(section);
    }

    @PutMapping("/{sectionId}")
    public ResponseEntity<SectionDTO> updateSection(
            @PathVariable Long sectionId,
            @RequestBody SectionDTO sectionDTO) {
        SectionDTO updatedSection = profileService.updateSection(sectionId, sectionDTO);
        return ResponseEntity.ok(updatedSection);
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long sectionId) {
        profileService.deleteSection(sectionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reorder/{profileId}")
    public ResponseEntity<Void> reorderSections(
            @PathVariable Long profileId,
            @RequestBody List<Long> sectionIds) {
        profileService.reorderSections(profileId, sectionIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<List<SectionDTO>> getSectionsByProfile(@PathVariable Long profileId) {
        List<SectionDTO> sections = profileService.getSectionsByProfile(profileId);
        return ResponseEntity.ok(sections);
    }
}
