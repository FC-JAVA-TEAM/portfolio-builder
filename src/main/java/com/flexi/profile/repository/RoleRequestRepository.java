package com.flexi.profile.repository;

import com.flexi.profile.model.RoleRequest;
import com.flexi.profile.model.RoleRequest.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRequestRepository extends JpaRepository<RoleRequest, Long> {
    List<RoleRequest> findByUserId(Long userId);
    List<RoleRequest> findByStatus(RequestStatus status);
    List<RoleRequest> findByUserIdAndStatus(Long userId, RequestStatus status);
    Optional<RoleRequest> findByUserIdAndRequestedRoleIdAndStatus(Long userId, Long roleId, RequestStatus status);
    boolean existsByUserIdAndRequestedRoleIdAndStatus(Long userId, Long roleId, RequestStatus status);
    List<RoleRequest> findByProcessedById(Long adminId);
}
