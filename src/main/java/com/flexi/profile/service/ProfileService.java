package com.flexi.profile.service;

import com.flexi.profile.dto.ProfileDTO;
import com.flexi.profile.dto.SectionDTO;
import com.flexi.profile.dto.SubSectionDTO;
import java.util.List;

public interface ProfileService {
    ProfileDTO createProfile(ProfileDTO profileDTO, String userEmail);
    ProfileDTO getProfile(Long profileId);
    List<ProfileDTO> getAllProfiles();
    List<ProfileDTO> getProfilesByUserId(Long userId);
    List<ProfileDTO> getProfilesByUserEmail(String userEmail);
    ProfileDTO updateProfile(Long profileId, ProfileDTO profileDTO);
    void deleteProfile(Long profileId);
    List<ProfileDTO> getPublicProfiles();

    // Section-related methods
    SectionDTO createSection(SectionDTO sectionDTO);
    SectionDTO getSection(Long sectionId);
    SectionDTO updateSection(Long sectionId, SectionDTO sectionDTO);
    void deleteSection(Long sectionId);
    void reorderSections(Long profileId, List<Long> sectionIds);
    List<SectionDTO> getSectionsByProfile(Long profileId);

    SubSectionDTO createSubSection(Long sectionId, SubSectionDTO subSectionDTO);
}
