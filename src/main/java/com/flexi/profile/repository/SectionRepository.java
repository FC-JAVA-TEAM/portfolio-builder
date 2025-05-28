package com.flexi.profile.repository;

import com.flexi.profile.model.Profile;
import com.flexi.profile.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByProfileOrderByOrderIndexAsc(Profile profile);
    
    @Query("SELECT s FROM Section s LEFT JOIN FETCH s.subSections LEFT JOIN FETCH s.profile WHERE s.id = :id")
    Optional<Section> findByIdWithSubSections(@Param("id") Long id);
}
