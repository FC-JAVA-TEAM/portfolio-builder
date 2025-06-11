package com.flexi.profile.mapper;

import com.flexi.profile.dto.RoleRequestDTO;
import com.flexi.profile.model.RoleRequest;
import com.flexi.profile.model.User;
import com.flexi.profile.model.Role;
import com.flexi.profile.repository.UserRepository;
import com.flexi.profile.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoleRequestMapper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public RoleRequestDTO toDTO(RoleRequest entity) {
        if (entity == null) {
            return null;
        }
        
        return RoleRequestDTO.builder()
            .id(entity.getId())
            .roleId(entity.getRequestedRole().getId())
            .userId(entity.getUser().getId())
            .status(entity.getStatus().name())
            .justification(entity.getJustification())
            .requestDate(entity.getRequestedAt())
            .processedDate(entity.getProcessedAt())
            .processedBy(entity.getProcessedBy() != null ? entity.getProcessedBy().getId() : null)
            .build();
    }

    public List<RoleRequestDTO> toDTOList(List<RoleRequest> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public RoleRequest toEntity(RoleRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        RoleRequest entity = new RoleRequest();
        entity.setId(dto.getId());
        
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            entity.setUser(user);
        }
        
        if (dto.getRoleId() != null) {
            Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
            entity.setRequestedRole(role);
        }
        
        if (dto.getStatus() != null) {
            entity.setStatus(RoleRequest.RequestStatus.valueOf(dto.getStatus()));
        }
        
        entity.setJustification(dto.getJustification());
        entity.setRequestedAt(dto.getRequestDate());
        entity.setProcessedAt(dto.getProcessedDate());
        
        if (dto.getProcessedBy() != null) {
            User processedBy = userRepository.findById(dto.getProcessedBy())
                .orElseThrow(() -> new RuntimeException("Processing user not found"));
            entity.setProcessedBy(processedBy);
        }
        
        return entity;
    }
}
