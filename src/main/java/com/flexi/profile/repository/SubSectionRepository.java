package com.flexi.profile.repository;

import com.flexi.profile.model.SubSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubSectionRepository extends JpaRepository<SubSection, Long> {
    List<SubSection> findBySectionIdOrderByDisplayOrder(Long sectionId);
}
