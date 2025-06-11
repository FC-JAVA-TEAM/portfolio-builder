package com.flexi.profile.service;

import com.flexi.profile.dto.RoleRequestDTO;
import com.flexi.profile.mapper.RoleRequestMapper;
import com.flexi.profile.model.Role;
import com.flexi.profile.model.RoleRequest;
import com.flexi.profile.model.User;
import com.flexi.profile.repository.RoleRepository;
import com.flexi.profile.repository.RoleRequestRepository;
import com.flexi.profile.repository.UserRepository;
import com.flexi.profile.exception.ResourceNotFoundException;
import com.flexi.profile.exception.UnauthorizedException;
import com.flexi.profile.exception.role.RoleRequestNotFoundException;
import com.flexi.profile.exception.role.UserAlreadyHasRoleException;
import com.flexi.profile.exception.role.InvalidRequestStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EnhancedRoleService {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedRoleService.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleRequestRepository roleRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRequestMapper roleRequestMapper;

    @Transactional
    public RoleRequestDTO requestRole(Long userId, Long roleId, String justification) {
        logger.info("Requesting role. UserId: {}, RoleId: {}", userId, roleId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with ID %d not found", userId)));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Role with ID %d not found", roleId)));

        logger.debug("User and Role found. User: {}, Role: {}", user.getEmail(), role.getName());

        if (user.getRoles().contains(role)) {
            logger.warn("User already has the requested role. UserId: {}, RoleId: {}", userId, roleId);
            throw new UserAlreadyHasRoleException(user.getEmail(), role.getName());
        }

        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setUser(user);
        roleRequest.setRequestedRole(role);
        roleRequest.setJustification(justification);
        roleRequest.setStatus(RoleRequest.RequestStatus.PENDING);

        logger.debug("Saving role request: {}", roleRequest);
        RoleRequest savedRequest = roleRequestRepository.save(roleRequest);
        logger.info("Role request saved successfully. RequestId: {}", savedRequest.getId());

        return roleRequestMapper.toDTO(savedRequest);
    }

    @Transactional
    public RoleRequestDTO approveRoleRequest(Long requestId, Long adminId) {
        RoleRequest request = roleRequestRepository.findById(requestId)
                .orElseThrow(() -> new RoleRequestNotFoundException(requestId));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Admin with ID %d not found", adminId)));

        if (request.getStatus() != RoleRequest.RequestStatus.PENDING) {
            throw new InvalidRequestStatusException(request.getStatus(), "PENDING");
        }

        request.approve(admin);
        User user = request.getUser();
        user.getRoles().add(request.getRequestedRole());
        userRepository.save(user);

        RoleRequest savedRequest = roleRequestRepository.save(request);
        return roleRequestMapper.toDTO(savedRequest);
    }

    @Transactional
    public RoleRequestDTO rejectRoleRequest(Long requestId, Long adminId) {
        RoleRequest request = roleRequestRepository.findById(requestId)
                .orElseThrow(() -> new RoleRequestNotFoundException(requestId));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Admin with ID %d not found", adminId)));

        if (request.getStatus() != RoleRequest.RequestStatus.PENDING) {
            throw new InvalidRequestStatusException(request.getStatus(), "PENDING");
        }

        request.reject(admin);
        RoleRequest savedRequest = roleRequestRepository.save(request);
        return roleRequestMapper.toDTO(savedRequest);
    }

    public List<RoleRequestDTO> getPendingRequests() {
        logger.info("Fetching pending role requests");
        List<RoleRequest> pendingRequests = roleRequestRepository.findByStatus(RoleRequest.RequestStatus.PENDING);
        logger.debug("Found {} pending requests", pendingRequests.size());
        return roleRequestMapper.toDTOList(pendingRequests);
    }

    public List<RoleRequestDTO> getUserRoleRequests(Long userId) {
        List<RoleRequest> userRequests = roleRequestRepository.findByUserId(userId);
        return roleRequestMapper.toDTOList(userRequests);
    }
}
