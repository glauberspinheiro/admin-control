package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.condicionante.EmailDispatchTaskModel;
import com.revitalize.admincontrol.models.enums.EmailDispatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface EmailDispatchTaskRepository extends JpaRepository<EmailDispatchTaskModel, UUID> {

    List<EmailDispatchTaskModel> findTop50ByStatusAndScheduledForLessThanEqualOrderByScheduledForAsc(
            EmailDispatchStatus status,
            OffsetDateTime limite);

    long countByStatus(EmailDispatchStatus status);
}
