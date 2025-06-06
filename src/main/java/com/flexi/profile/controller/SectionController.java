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
        SectionDTO createdSection = profileService.createSection(sectionDTO);
        LogUtil.logMethodExit(logger, "createSection", createdSection);
        return ResponseEntity.ok(createdSection);
    }

    @PostMapping("/{sectionId}/subsections")
    public ResponseEntity<SubSectionDTO> createSubSection(@PathVariable Long sectionId, @RequestBody SubSectionDTO subSectionDTO) {
        LogUtil.logMethodEntry(logger, "createSubSection", sectionId, subSectionDTO);
        SubSectionDTO createdSubSection = profileService.createSubSection(sectionId, subSectionDTO);
        LogUtil.logMethodExit(logger, "createSubSection", createdSubSection);
        return ResponseEntity.ok(createdSubSection);
    }

    @GetMapping("/{sectionId}")
    public ResponseEntity<SectionDTO> getSection(@PathVariable Long sectionId) {
        LogUtil.logMethodEntry(logger, "getSection", sectionId);
        SectionDTO section = profileService.getSection(sectionId);
        LogUtil.logMethodExit(logger, "getSection", section);
        return ResponseEntity.ok(section);
    }

    @PutMapping("/{sectionId}")
    public ResponseEntity<SectionDTO> updateSection(
            @PathVariable Long sectionId,
            @RequestBody SectionDTO sectionDTO) {
        LogUtil.logMethodEntry(logger, "updateSection", sectionId, sectionDTO);
        SectionDTO updatedSection = profileService.updateSection(sectionId, sectionDTO);
        LogUtil.logMethodExit(logger, "updateSection", updatedSection);
        return ResponseEntity.ok(updatedSection);
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long sectionId) {
        LogUtil.logMethodEntry(logger, "deleteSection", sectionId);
        profileService.deleteSection(sectionId);
        LogUtil.logMethodExit(logger, "deleteSection");
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reorder/{profileId}")
    public ResponseEntity<Void> reorderSections(
            @PathVariable Long profileId,
            @RequestBody List<Long> sectionIds) {
        LogUtil.logMethodEntry(logger, "reorderSections", profileId, sectionIds);
        profileService.reorderSections(profileId, sectionIds);
        LogUtil.logMethodExit(logger, "reorderSections");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<List<SectionDTO>> getSectionsByProfile(@PathVariable Long profileId) {
        LogUtil.logMethodEntry(logger, "getSectionsByProfile", profileId);
        List<SectionDTO> sections = profileService.getSectionsByProfile(profileId);
        LogUtil.logMethodExit(logger, "getSectionsByProfile", sections);
        return ResponseEntity.ok(sections);
    }

    @GetMapping("/{sectionId}/subsections/{subsectionId}")
    public ResponseEntity<SubSectionDTO> getSubSection(
            @PathVariable Long sectionId,
            @PathVariable Long subsectionId) {
        LogUtil.logMethodEntry(logger, "getSubSection", sectionId, subsectionId);
        SubSectionDTO subSection = profileService.getSubSection(sectionId, subsectionId);
        LogUtil.logMethodExit(logger, "getSubSection", subSection);
        return ResponseEntity.ok(subSection);
    }

    @PutMapping("/{sectionId}/subsections/{subsectionId}")
    public ResponseEntity<SubSectionDTO> updateSubSection(
            @PathVariable Long sectionId,
            @PathVariable Long subsectionId,
            @RequestBody SubSectionDTO subSectionDTO) {
        LogUtil.logMethodEntry(logger, "updateSubSection", sectionId, subsectionId, subSectionDTO);
        SubSectionDTO updatedSubSection = profileService.updateSubSection(sectionId, subsectionId, subSectionDTO);
        LogUtil.logMethodExit(logger, "updateSubSection", updatedSubSection);
        return ResponseEntity.ok(updatedSubSection);
    }

    @DeleteMapping("/{sectionId}/subsections/{subsectionId}")
    public ResponseEntity<Void> deleteSubSection(
            @PathVariable Long sectionId,
            @PathVariable Long subsectionId) {
        LogUtil.logMethodEntry(logger, "deleteSubSection", sectionId, subsectionId);
        profileService.deleteSubSection(sectionId, subsectionId);
        LogUtil.logMethodExit(logger, "deleteSubSection");
        return ResponseEntity.noContent().build();
    }
}
