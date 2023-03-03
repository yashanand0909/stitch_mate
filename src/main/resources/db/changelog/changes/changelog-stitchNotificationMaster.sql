--liquibase formatted sql

--changeset yash:1 context:prod,dev,stage,docker,load,uat

CREATE TABLE notification_master (
                                            notification_id bigint PRIMARY KEY AUTO_INCREMENT,
                                            job_id bigint not null ,
                                            notification_config text,
                                            on_success boolean DEFAULT 0,
                                            on_failure boolean DEFAULT 0,
                                            created_at timestamp default CURRENT_TIMESTAMP not null,
                                            recupdated_at timestamp on update CURRENT_TIMESTAMP not null,
                                            CONSTRAINT fkNotificationMasterDagId FOREIGN KEY (job_id) REFERENCES
                                                job_master (job_id)
);


CREATE INDEX Index_recupdatedAt ON notification_master (recupdated_at);

