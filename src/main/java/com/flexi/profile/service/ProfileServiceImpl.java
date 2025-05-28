package com.flexi.profile.service;

import com.flexi.profile.exception.SectionNotFoundException;
import com.flexi.profile.exception.profile.ProfileNotFoundException;
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
import com.flexi.profile.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileServiceImpl.class);

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private SubSectionRepository subSectionRepository;

    @Override
    public ProfileDTO createProfile(ProfileDTO profileDTO, String userEmail) {
        LogUtil.logMethodEntry(logger, "createProfile", profileDTO, userEmail);
        try {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ProfileNotFoundException("User not found with email: " + userEmail));

            Profile profile = new Profile();
            profile.setName(profileDTO.getName());
            profile.setBio(profileDTO.getBio());
            profile.setIsPublic(profileDTO.getIsPublic());
            profile.setUser(user);

            Profile savedProfile = profileRepository.save(profile);
            LogUtil.logDebug(logger, "Created new profile with ID: " + savedProfile.getId());
            
            ProfileDTO result = convertToDTO(savedProfile);
            LogUtil.logMethodExit(logger, "createProfile", result);
            return result;
        } catch (Exception e) {
            LogUtil.logError(logger, "Error creating profile", e);
            throw e;
        }
    }

    @Override
    public ProfileDTO getProfile(Long profileId) {
        LogUtil.logMethodEntry(logger, "getProfile", profileId);
        try {
            Profile profile = profileRepository.findById(profileId)
                    .orElseThrow(() -> new ProfileNotFoundException("Profile not found with id: " + profileId));
            ProfileDTO result = convertToDTO(profile);
            LogUtil.logMethodExit(logger, "getProfile", result);
            return result;
        } catch (Exception e) {
            LogUtil.logError(logger, "Error fetching profile", e);
            throw e;
        }
    }

    @Override
    public List<ProfileDTO> getAllProfiles() {
        LogUtil.logMethodEntry(logger, "getAllProfiles");
        try {
            List<Profile> profiles = profileRepository.findAll();
            List<ProfileDTO> result = profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
            LogUtil.logMethodExit(logger, "getAllProfiles", result);
            return result;
        } catch (Exception e) {
            LogUtil.logError(logger, "Error fetching all profiles", e);
            throw e;
        }
    }

    @Override
    public List<ProfileDTO> getProfilesByUserId(Long userId) {
        LogUtil.logMethodEntry(logger, "getProfilesByUserId", userId);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ProfileNotFoundException("User not found with id: " + userId));
            List<Profile> profiles = profileRepository.findByUser(user);
            List<ProfileDTO> result = profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
            LogUtil.logMethodExit(logger, "getProfilesByUserId", result);
            return result;
        } catch (Exception e) {
            LogUtil.logError(logger, "Error fetching profiles by user ID", e);
            throw e;
        }
    }

    @Override
    public List<ProfileDTO> getProfilesByUserEmail(String userEmail) {
        LogUtil.logMethodEntry(logger, "getProfilesByUserEmail", userEmail);
        try {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ProfileNotFoundException("User not found with email: " + userEmail));
            List<Profile> profiles = profileRepository.findByUser(user);
            List<ProfileDTO> result = profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
            LogUtil.logMethodExit(logger, "getProfilesByUserEmail", result);
            return result;
        } catch (Exception e) {
            LogUtil.logError(logger, "Error fetching profiles by user email", e);
            throw e;
        }
    }

    @Override
    public ProfileDTO updateProfile(Long profileId, ProfileDTO profileDTO) {
        LogUtil.logMethodEntry(logger, "updateProfile", profileId, profileDTO);
        try {
            Profile profile = profileRepository.findById(profileId)
                    .orElseThrow(() -> new ProfileNotFoundException("Profile not found with id: " + profileId));

            profile.setName(profileDTO.getName());
            profile.setBio(profileDTO.getBio());
            profile.setIsPublic(profileDTO.getIsPublic());

            Profile updatedProfile = profileRepository.save(profile);
            LogUtil.logDebug(logger, "Updated profile with ID: " + profileId);
            
            ProfileDTO result = convertToDTO(updatedProfile);
            LogUtil.logMethodExit(logger, "updateProfile", result);
            return result;
        } catch (Exception e) {
            LogUtil.logError(logger, "Error updating profile", e);
            throw e;
        }
    }

    @Override
    public void deleteProfile(Long profileId) {
        LogUtil.logMethodEntry(logger, "deleteProfile", profileId);
        try {
            profileRepository.deleteById(profileId);
            LogUtil.logDebug(logger, "Deleted profile with ID: " + profileId);
            LogUtil.logMethodExit(logger, "deleteProfile");
        } catch (Exception e) {
            LogUtil.logError(logger, "Error deleting profile", e);
            throw e;
        }
    }

    @Override
    public List<ProfileDTO> getPublicProfiles() {
        LogUtil.logMethodEntry(logger, "getPublicProfiles");
        try {
            List<Profile> publicProfiles = profileRepository.findByIsPublicTrue();
            List<ProfileDTO> result = publicProfiles.stream().map(this::convertToDTO).collect(Collectors.toList());
            LogUtil.logMethodExit(logger, "getPublicProfiles", result);
            return result;
        } catch (Exception e) {
            LogUtil.logError(logger, "Error fetching public profiles", e);
            throw e;
        }
    }

    @Override
    public SectionDTO createSection(SectionDTO sectionDTO) {
        LogUtil.logMethodEntry(logger, "createSection", sectionDTO);
        try {
            Profile profile = profileRepository.findById(sectionDTO.getProfileId())
                    .orElseThrow(() -> new ProfileNotFoundException("Profile not found with id: " + sectionDTO.getProfileId()));

            Section section = new Section();
            section.setTitle(sectionDTO.getTitle());
            section.setContent(sectionDTO.getContent());
            section.setProfile(profile);

            Section savedSection = sectionRepository.save(section);
            LogUtil.logDebug(logger, "Created new section with ID: " + savedSection.getId());
            
            SectionDTO result = convertToDTO(savedSection, false);
            LogUtil.logMethodExit(logger, "createSection", result);
            return result;
        } catch (Exception e) {
            LogUtil.logError(logger, "Error creating section", e);
            throw e;
        }
    }

    @Override
    public SectionDTO getSection(Long sectionId) {
        LogUtil.logMethodEntry(logger, "getSection", sectionId);
        try {
            Section section = sectionRepository.findByIdWithSubSections(sectionId)
                    .orElseThrow(() -> new SectionNotFoundException("Section not found with id: " + sectionId));
            SectionDTO result = convertToDTO(section, true);
            LogUtil.logMethodExit(logger, "getSection", result);
            return result;
        } catch (Exception e) {
            LogUtil.logError(logger, "Error fetching section", e);
            throw e;
        }
    }

    @Override
    public SectionDTO updateSection(Long sectionId, SectionDTO sectionDTO) {
        LogUtil.logMethodEntry(logger, "updateSection", sectionId, sectionDTO);
        try {
            Section section = sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new SectionNotFoundException("Section not found with id: " + sectionId));

            section.setTitle(sectionDTO.getTitle());
            section.setContent(sectionDTO.getContent());

            Section updatedSection = sectionRepository.save(section);
            LogUtil.logDebug(logger, "Updated section with ID: " + sectionId);
            
            SectionDTO result = convertToDTO(updatedSection, false);
            LogUtil.logMethodExit(logger, "updateSection", result);
            return result;
        } catch (Exception e) {
            LogUtil.logError(logger, "Error updating section", e);
            throw e;
        }
    }

    @Override
    public void deleteSection(Long sectionId) {
        LogUtil.logMethodEntry(logger, "deleteSection", sectionId);
        try {
            sectionRepository.deleteById(sectionId);
            LogUtil.logDebug(logger, "Deleted section with ID: " + sectionId);
            LogUtil.logMethodExit(logger, "deleteSection");
        } catch (Exception e) {
            LogUtil.logError(logger, "Error deleting section", e);
            throw e;
        }
    }

    @Override
    public void reorderSections(Long profileId, List<Long> sectionIds) {
        LogUtil.logMethodEntry(logger, "reorderSections", profileId, sectionIds);
        try {
            Profile profile = profileRepository.findById(profileId)
                    .orElseThrow(() -> new ProfileNotFoundException("Profile not found with id: " + profileId));

            List<Section> sections = sectionRepository.findAllById(sectionIds);

            for (int i = 0; i < sections.size(); i++) {
                Section section = sections.get(i);
                section.setOrderIndex(i);
                sectionRepository.save(section);
                LogUtil.logDebug(logger, "Updated order index for section " + section.getId() + " to " + i);
            }
            LogUtil.logMethodExit(logger, "reorderSections");
        } catch (Exception e) {
            LogUtil.logError(logger, "Error reordering sections", e);
            throw e;
        }
    }

    @Override
    public List<SectionDTO> getSectionsByProfile(Long profileId) {
        LogUtil.logMethodEntry(logger, "getSectionsByProfile", profileId);
        try {
            Profile profile = profileRepository.findById(profileId)
                    .orElseThrow(() -> new ProfileNotFoundException("Profile not found with id: " + profileId));
            List<Section> sections = sectionRepository.findByProfileOrderByOrderIndexAsc(profile);
            List<SectionDTO> result = sections.stream().map(s -> convertToDTO(s, false)).collect(Collectors.toList());
            LogUtil.logMethodExit(logger, "getSectionsByProfile", result);
            return result;
        } catch (Exception e) {
            LogUtil.logError(logger, "Error fetching sections by profile", e);
            throw e;
        }
    }

    @Override
    public SubSectionDTO createSubSection(Long sectionId, SubSectionDTO subSectionDTO) {
        LogUtil.logMethodEntry(logger, "createSubSection", sectionId, subSectionDTO);
        try {
            Section section = sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new SectionNotFoundException("Section not found with id: " + sectionId));

            SubSection subSection = new SubSection();
            subSection.setTitle(subSectionDTO.getTitle());
            subSection.setContent(subSectionDTO.getContent());
            subSection.setSection(section);

            SubSection savedSubSection = subSectionRepository.save(subSection);
            LogUtil.logDebug(logger, "Created new subsection with ID: " + savedSubSection.getId());
            
            SubSectionDTO result = convertToDTO(savedSubSection);
            LogUtil.logMethodExit(logger, "createSubSection", result);
            return result;
        } catch (Exception e) {
            LogUtil.logError(logger, "Error creating subsection", e);
            throw e;
        }
    }

    private ProfileDTO convertToDTO(Profile profile) {
        LogUtil.logMethodEntry(logger, "convertToDTO", profile);
        ProfileDTO dto = new ProfileDTO();
        dto.setId(profile.getId());
        dto.setName(profile.getName());
        dto.setBio(profile.getBio());
        dto.setIsPublic(profile.getIsPublic());
        dto.setUserId(profile.getUser().getId());
        LogUtil.logMethodExit(logger, "convertToDTO", dto);
        return dto;
    }

    private SectionDTO convertToDTO(Section section, boolean includeSubSections) {
        LogUtil.logMethodEntry(logger, "convertToDTO", section, includeSubSections);
        SectionDTO dto = new SectionDTO();
        dto.setId(section.getId());
        dto.setTitle(section.getTitle());
        dto.setContent(section.getContent());
        dto.setProfileId(section.getProfile().getId());
        dto.setOrderIndex(section.getOrderIndex());
        
        if (includeSubSections) {
            List<SubSectionDTO> subSections = section.getSubSections().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            dto.setSubSections(subSections);
            LogUtil.logDebug(logger, "Included " + subSections.size() + " subsections for section " + section.getId());
        }
        
        LogUtil.logMethodExit(logger, "convertToDTO", dto);
        return dto;
    }

    private SubSectionDTO convertToDTO(SubSection subSection) {
        LogUtil.logMethodEntry(logger, "convertToDTO", subSection);
        SubSectionDTO dto = new SubSectionDTO();
        dto.setId(subSection.getId());
        dto.setTitle(subSection.getTitle());
        dto.setContent(subSection.getContent());
        dto.setSectionId(subSection.getSection().getId());
        LogUtil.logMethodExit(logger, "convertToDTO", dto);
        return dto;
    }
}
