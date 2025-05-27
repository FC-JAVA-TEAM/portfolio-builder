package com.flexi.profile.repository;

import com.flexi.profile.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    List<Profile> findByUserId(String userId);
    List<Profile> findByIsPublic(Boolean isPublic);
}
