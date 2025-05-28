package com.flexi.profile.service;

import com.flexi.profile.dto.ProfileDTO;
import com.flexi.profile.dto.SectionDTO;
import com.flexi.profile.dto.SubSectionDTO;
import com.flexi.profile.model.Profile;
import com.flexi.profile.model.Section;
import com.flexi.profile.model.SubSection;
import com.flexi.profile.model.User;
import com.flexi.profile.repository.ProfileRepository;
import com.flexi.profile.repository.SectionRepository;
import com.flexi.profile.repository.SubSectionRepository;
import com.flexi.profile.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfileServiceImpl implements ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileServiceImpl.class);

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubSectionRepository subSectionRepository;

    @Override
    public ProfileDTO createProfile(ProfileDTO profileDTO) {
        User user = userRepository.findByEmail(profileDTO.getUserEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        Profile profile = new Profile();
        profile.setUser(user);
        profile.setName(profileDTO.getName());
        profile.setBio(profileDTO.getBio());
        profile.setIsPublic(profileDTO.getIsPublic());
        
        Profile savedProfile = profileRepository.save(profile);
        logger.info("Created new profile for user: {}", user.getEmail());
        return convertToDTO(savedProfile);
    }

    private ProfileDTO convertToDTO(Profile profile) {
        ProfileDTO dto = new ProfileDTO();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUser().getId().toString());
        dto.setUserEmail(profile.getUser().getEmail());
        dto.setName(profile.getName());
        dto.setBio(profile.getBio());
        dto.setIsPublic(profile.getIsPublic());
        if (profile.getSections() != null) {
            dto.setSections(profile.getSections().stream()
                .map(this::convertToSectionDTO)
                .collect(Collectors.toList()));
        }
        return dto;
    }

    @Override
    public ProfileDTO getProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
        return convertToDTO(profile);
    }

    @Override
    public List<ProfileDTO> getAllProfiles() {
        return profileRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProfileDTO> getProfilesByUserId(String userId) {
        User user = userRepository.findByEmail(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return profileRepository.findByUser(user).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public ProfileDTO updateProfile(Long profileId, ProfileDTO profileDTO) {
        Profile profile = profileRepository.findById(profileId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        profile.setName(profileDTO.getName());
        profile.setBio(profileDTO.getBio());
        profile.setIsPublic(profileDTO.getIsPublic());
        
        Profile updatedProfile = profileRepository.save(profile);
        return convertToDTO(updatedProfile);
    }

    @Override
    public void deleteProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
        profileRepository.delete(profile);
    }

    @Override
    public List<ProfileDTO> getPublicProfiles() {
        return profileRepository.findByIsPublic(true).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    // Section-related methods
    @Override
    public SectionDTO createSection(SectionDTO sectionDTO) {
        Profile profile = profileRepository.findById(sectionDTO.getProfileId())
            .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        Section section = new Section();
        section.setProfile(profile);
        section.setType(sectionDTO.getType());
        section.setTitle(sectionDTO.getTitle());
        section.setDisplayOrder(sectionDTO.getOrder());
        section.setBackgroundUrl(sectionDTO.getBackgroundUrl());
        
        Section savedSection = sectionRepository.save(section);
        return convertToSectionDTO(savedSection);
    }

    @Override
    public SectionDTO getSection(Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
            .orElseThrow(() -> new RuntimeException("Section not found"));
        return convertToSectionDTO(section);
    }

    @Override
    public SectionDTO updateSection(Long sectionId, SectionDTO sectionDTO) {
        Section section = sectionRepository.findById(sectionId)
            .orElseThrow(() -> new RuntimeException("Section not found"));
        
        section.setTitle(sectionDTO.getTitle());
        section.setType(sectionDTO.getType());
        section.setDisplayOrder(sectionDTO.getOrder());
        section.setBackgroundUrl(sectionDTO.getBackgroundUrl());
        
        Section updatedSection = sectionRepository.save(section);
        return convertToSectionDTO(updatedSection);
    }

    @Override
    public void deleteSection(Long sectionId) {
        sectionRepository.deleteById(sectionId);
    }

    @Override
    @Transactional
    public void reorderSections(Long profileId, List<Long> sectionIds) {
        for (int i = 0; i < sectionIds.size(); i++) {
            Section section = sectionRepository.findById(sectionIds.get(i))
                .orElseThrow(() -> new RuntimeException("Section not found"));
            section.setDisplayOrder(i);
            sectionRepository.save(section);
        }
    }

    @Override
    public List<SectionDTO> getSectionsByProfile(Long profileId) {
        return sectionRepository.findByProfileIdOrderByDisplayOrder(profileId).stream()
            .map(this::convertToSectionDTO)
            .collect(Collectors.toList());
    }

    @Override
    public SubSectionDTO createSubSection(Long sectionId, SubSectionDTO subSectionDTO) {
        Section section = sectionRepository.findById(sectionId)
            .orElseThrow(() -> new RuntimeException("Section not found"));

        SubSection subSection = new SubSection();
        subSection.setTitle(subSectionDTO.getTitle());
        subSection.setContent(subSectionDTO.getContent());
        subSection.setDisplayOrder(subSectionDTO.getOrder());
        subSection.setSection(section);

        SubSection savedSubSection = subSectionRepository.save(subSection);
        return convertToSubSectionDTO(savedSubSection);
    }

    // Helper methods
    private SectionDTO convertToSectionDTO(Section section) {
        SectionDTO dto = new SectionDTO();
        dto.setId(section.getId());
        dto.setProfileId(section.getProfile().getId());
        dto.setType(section.getType());
        dto.setTitle(section.getTitle());
        dto.setOrder(section.getDisplayOrder());
        dto.setBackgroundUrl(section.getBackgroundUrl());
        if (section.getSubsections() != null) {
            dto.setSubsections(section.getSubsections().stream()
                .map(this::convertToSubSectionDTO)
                .collect(Collectors.toList()));
        }
        return dto;
    }

    private SubSectionDTO convertToSubSectionDTO(SubSection subSection) {
        SubSectionDTO dto = new SubSectionDTO();
        dto.setId(subSection.getId());
        dto.setSectionId(subSection.getSection().getId());
        dto.setTitle(subSection.getTitle());
        dto.setContent(subSection.getContent());
        dto.setOrder(subSection.getDisplayOrder());
        return dto;
    }
}
