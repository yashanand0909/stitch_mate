package com.mate.repositories;

import com.mate.models.entities.NotificationMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationMasterRepository extends JpaRepository<NotificationMaster, Long> {

  NotificationMaster findByNotificationId(long notificationId);

  NotificationMaster findByNotificationIdAndDagId(long notificationId, long dagId);
}
