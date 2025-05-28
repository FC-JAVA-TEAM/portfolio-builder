package com.flexi.profile.controller;

import com.flexi.profile.dto.SectionDTO;
import com.flexi.profile.dto.SubSectionDTO;
import com.flexi.profile.service.ProfileService;
import com.flexi.profile.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(SectionController.class);

    @Autowired
    private ProfileService profileService;

    @PostMapping
    public ResponseEntity<SectionDTO> createSection(@RequestBody SectionDTO sectionDTO) {
        LogUtil.logMethodEntry(logger, "createSection", sectionDTO);
        try {
            SectionDTO createdSection = profileService.createSection(sectionDTO);
            LogUtil.logMethodExit(logger, "createSection", createdSection);
            return ResponseEntity.ok(createdSection);
        } catch (Exception e) {
            LogUtil.logError(logger, "Error creating section", e);
            throw e;
        }
    }

    @PostMapping("/{sectionId}/subsections")
    public ResponseEntity<SubSectionDTO> createSubSection(@PathVariable Long sectionId, @RequestBody SubSectionDTO subSectionDTO) {
        LogUtil.logMethodEntry(logger, "createSubSection", sectionId, subSectionDTO);
        try {
            SubSectionDTO createdSubSection = profileService.createSubSection(sectionId, subSectionDTO);
            LogUtil.logMethodExit(logger, "createSubSection", createdSubSection);
            return ResponseEntity.ok(createdSubSection);
        } catch (Exception e) {
            LogUtil.logError(logger, "Error creating subsection", e);
            throw e;
        }
    }

    @GetMapping("/{sectionId}")
    public ResponseEntity<SectionDTO> getSection(@PathVariable Long sectionId) {
        LogUtil.logMethodEntry(logger, "getSection", sectionId);
        try {
            SectionDTO section = profileService.getSection(sectionId);
            LogUtil.logMethodExit(logger, "getSection", section);
            return ResponseEntity.ok(section);
        } catch (Exception e) {
            LogUtil.logError(logger, "Error fetching section", e);
            throw e;
        }
    }

    @PutMapping("/{sectionId}")
    public ResponseEntity<SectionDTO> updateSection(
            @PathVariable Long sectionId,
            @RequestBody SectionDTO sectionDTO) {
        LogUtil.logMethodEntry(logger, "updateSection", sectionId, sectionDTO);
        try {
            SectionDTO updatedSection = profileService.updateSection(sectionId, sectionDTO);
            LogUtil.logMethodExit(logger, "updateSection", updatedSection);
            return ResponseEntity.ok(updatedSection);
        } catch (Exception e) {
            LogUtil.logError(logger, "Error updating section", e);
            throw e;
        }
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long sectionId) {
        LogUtil.logMethodEntry(logger, "deleteSection", sectionId);
        try {
            profileService.deleteSection(sectionId);
            LogUtil.logMethodExit(logger, "deleteSection");
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            LogUtil.logError(logger, "Error deleting section", e);
            throw e;
        }
    }

    @PostMapping("/reorder/{profileId}")
    public ResponseEntity<Void> reorderSections(
            @PathVariable Long profileId,
            @RequestBody List<Long> sectionIds) {
        LogUtil.logMethodEntry(logger, "reorderSections", profileId, sectionIds);
        try {
            profileService.reorderSections(profileId, sectionIds);
            LogUtil.logMethodExit(logger, "reorderSections");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            LogUtil.logError(logger, "Error reordering sections", e);
            throw e;
        }
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<List<SectionDTO>> getSectionsByProfile(@PathVariable Long profileId) {
        LogUtil.logMethodEntry(logger, "getSectionsByProfile", profileId);
        try {
            List<SectionDTO> sections = profileService.getSectionsByProfile(profileId);
            LogUtil.logMethodExit(logger, "getSectionsByProfile", sections);
            return ResponseEntity.ok(sections);
        } catch (Exception e) {
            LogUtil.logError(logger, "Error fetching sections by profile", e);
            throw e;
        }
    }
}
