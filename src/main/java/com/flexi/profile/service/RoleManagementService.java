package com.flexi.profile.service;

import com.flexi.profile.dto.RolePromotionResponse;
import com.flexi.profile.exception.service.common.ResourceNotFoundException;
import com.flexi.profile.exception.service.hr.UserAlreadyRoleException;
import com.flexi.profile.exception.service.hr.UserNotEligibleException;
import com.flexi.profile.model.User;
import com.flexi.profile.model.UserRole;
import com.flexi.profile.repository.UserRepository;
import com.flexi.profile.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleManagementService {

    private static final Logger logger = LoggerFactory.getLogger(RoleManagementService.class);
    private static final String HR_ROLE = "ROLE_HR";
    private static final String FINANCE_ROLE = "ROLE_FINANCE";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditService auditService;

    @Transactional
    public RolePromotionResponse promoteUserToHR(User requestUser, String promotedByEmail) {
        return promoteUserToRole(requestUser, HR_ROLE, promotedByEmail);
    }

    @Transactional
    public RolePromotionResponse promoteUserToRole(User requestUser, String targetRole, String promotedByEmail) {
        LogUtil.logMethodEntry(logger, "promoteUserToRole", requestUser.getEmail(), targetRole);
        
        try {
            // Validate target role
            validateTargetRole(targetRole);
            
            // Find the user in database by email
            User existingUser = userRepository.findByEmail(requestUser.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + requestUser.getEmail()));

            // Validate user eligibility
            validateUserEligibility(existingUser, targetRole);

            // Check if user already has the target role
            boolean hasTargetRole = existingUser.getRoles().stream()
                    .anyMatch(role -> targetRole.equals(role.getRole()));

            if (hasTargetRole) {
                String roleName = targetRole.replace("ROLE_", "");
                throw new UserAlreadyRoleException("User already has " + roleName + " role: " + existingUser.getEmail());
            }

            // Remove all existing roles
            existingUser.getRoles().clear();

            // Add target role
            UserRole newRole = new UserRole(existingUser, targetRole);
            existingUser.addRole(newRole);

            // Update the user's updatedAt timestamp
            existingUser.setUpdatedAt(Instant.now());

            // Save the updated user
            User savedUser = userRepository.save(existingUser);

            // Log the promotion action
            String roleName = targetRole.replace("ROLE_", "");
            auditService.logTokenAction("USER_PROMOTED_TO_" + roleName, promotedByEmail, null, 
                    String.format("User %s promoted to %s by %s", savedUser.getEmail(), roleName, promotedByEmail));

            // Create response
            RolePromotionResponse.PromotedUserInfo promotedUserInfo = createPromotedUserInfo(savedUser);
            RolePromotionResponse response = new RolePromotionResponse(
                    true,
                    "User successfully promoted to " + roleName + " role",
                    promotedUserInfo,
                    Instant.now(),
                    promotedByEmail
            );

            logger.info("Successfully promoted user {} to {} role by {}", savedUser.getEmail(), roleName, promotedByEmail);
            LogUtil.logMethodExit(logger, "promoteUserToRole", response);
            
            return response;

        } catch (Exception e) {
            logger.error("Error promoting user {} to {} role", requestUser.getEmail(), targetRole, e);
            throw e;
        }
    }

    private void validateTargetRole(String targetRole) {
        if (!HR_ROLE.equals(targetRole) && !FINANCE_ROLE.equals(targetRole)) {
            throw new IllegalArgumentException("Invalid target role: " + targetRole + ". Supported roles: HR, FINANCE");
        }
    }

    private void validateUserEligibility(User user, String targetRole) {
        String roleName = targetRole.replace("ROLE_", "");
        
        // User must be active
        if (!user.isActive()) {
            throw new UserNotEligibleException("User is not active: " + user.getEmail());
        }

        // User must have at least one profile (business requirement)
        if (user.getProfiles() == null || user.getProfiles().isEmpty()) {
            throw new UserNotEligibleException("User must have at least one profile to be promoted to " + roleName + ": " + user.getEmail());
        }

        // User must have first name
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new UserNotEligibleException("User must have a first name to be promoted to " + roleName + ": " + user.getEmail());
        }
    }

    private RolePromotionResponse.PromotedUserInfo createPromotedUserInfo(User user) {
        List<String> roles = user.getRoles().stream()
                .map(UserRole::getRole)
                .collect(Collectors.toList());

        return new RolePromotionResponse.PromotedUserInfo(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles,
                user.isActive()
        );
    }
}
