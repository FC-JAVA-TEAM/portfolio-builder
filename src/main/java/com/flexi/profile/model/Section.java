package com.flexi.profile.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sections")
@Data
@NoArgsConstructor
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    private Integer orderIndex;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubSection> subSections = new ArrayList<>();

    public void addSubSection(SubSection subSection) {
        subSections.add(subSection);
        subSection.setSection(this);
    }

    public void removeSubSection(SubSection subSection) {
        subSections.remove(subSection);
        subSection.setSection(null);
    }
}
