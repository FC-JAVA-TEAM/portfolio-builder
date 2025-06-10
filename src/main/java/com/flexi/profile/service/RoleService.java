package com.flexi.profile.service;

import com.flexi.profile.model.Role;
import com.flexi.profile.model.RoleRequest;
import com.flexi.profile.model.User;
import com.flexi.profile.repository.RoleRepository;
import com.flexi.profile.repository.RoleRequestRepository;
import com.flexi.profile.repository.UserRepository;
import com.flexi.profile.exception.ResourceNotFoundException;
import com.flexi.profile.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleRequestRepository roleRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public RoleRequest requestRole(Long userId, Long roleId, String justification) {
        logger.info("Requesting role. UserId: {}, RoleId: {}", userId, roleId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        logger.debug("User and Role found. User: {}, Role: {}", user.getEmail(), role.getName());

        if (user.getRoles().contains(role)) {
            logger.warn("User already has the requested role. UserId: {}, RoleId: {}", userId, roleId);
            throw new UnauthorizedException("User already has this role");
        }

        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setUser(user);
        roleRequest.setRequestedRole(role);
        roleRequest.setJustification(justification);
        roleRequest.setStatus(RoleRequest.RequestStatus.PENDING);

        logger.debug("Saving role request: {}", roleRequest);
        RoleRequest savedRequest = roleRequestRepository.save(roleRequest);
        logger.info("Role request saved successfully. RequestId: {}", savedRequest.getId());

        return savedRequest;
    }

    @Transactional
    public RoleRequest approveRoleRequest(Long requestId, Long adminId) {
        RoleRequest request = roleRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Role request not found"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (request.getStatus() != RoleRequest.RequestStatus.PENDING) {
            throw new UnauthorizedException("Request is not in pending state");
        }

        request.approve(admin);
        User user = request.getUser();
        user.getRoles().add(request.getRequestedRole());
        userRepository.save(user);

        return roleRequestRepository.save(request);
    }

    @Transactional
    public RoleRequest rejectRoleRequest(Long requestId, Long adminId) {
        RoleRequest request = roleRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Role request not found"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (request.getStatus() != RoleRequest.RequestStatus.PENDING) {
            throw new UnauthorizedException("Request is not in pending state");
        }

        request.reject(admin);
        return roleRequestRepository.save(request);
    }

    public List<RoleRequest> getPendingRequests() {
        logger.info("Fetching pending role requests");
        List<RoleRequest> pendingRequests = roleRequestRepository.findByStatus(RoleRequest.RequestStatus.PENDING);
        logger.debug("Found {} pending requests", pendingRequests.size());
        return pendingRequests;
    }

    public List<RoleRequest> getUserRoleRequests(Long userId) {
        return roleRequestRepository.findByUserId(userId);
    }
}
