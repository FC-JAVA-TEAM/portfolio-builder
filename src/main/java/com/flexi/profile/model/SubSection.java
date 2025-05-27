package com.flexi.profile.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "subsections")
@Data
@NoArgsConstructor
public class SubSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Section section;

    private String title;
    private String content;
    
    @Column(name = "display_order")
    private Integer displayOrder;
}
