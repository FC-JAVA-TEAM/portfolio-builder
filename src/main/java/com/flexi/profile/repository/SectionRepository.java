package com.flexi.profile.repository;

import com.flexi.profile.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByProfileIdOrderByDisplayOrder(Long profileId);
}
