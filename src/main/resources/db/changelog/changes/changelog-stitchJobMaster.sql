--liquibase formatted sql

--preconditions onFail:HALT onError:HALT

--changeset yash:1 context:prod,dev,stage,docker,load,uat


CREATE TABLE IF NOT EXISTS job_master (
                                   job_id bigint PRIMARY KEY AUTO_INCREMENT,
                                   job_name varchar(255) UNIQUE not null COMMENT  'Name of JOB',
                                   description text,
                                   last_execution_id int default 0,
                                   start_node text,
                                   created_by varchar(255) not null,
                                   schedule varchar(255),
                                   backfill boolean,
                                   start_date timestamp null ,
                                   end_date timestamp null ,
                                   notification_config text,
                                   job_status ENUM ('CREATED', 'RUNNING', 'ACTIVE', 'FAILED', 'PAUSED', 'TERMINATED') DEFAULT 'CREATED',
                                   next_dag_run_date timestamp null ,
                                   created_at timestamp default CURRENT_TIMESTAMP not null,
                                   recupdated_at timestamp on update CURRENT_TIMESTAMP not null
);

CREATE INDEX Index_recupdatedAt ON job_master (recupdated_at) ;

--changeset yash:2 context:prod,dev,stage,docker,load,uat

ALTER TABLE job_master DROP COLUMN job_status ;

ALTER TABLE job_master ADD COLUMN job_status ENUM ('DRAFT', 'RUNNING', 'ACTIVE', 'FAILED', 'PAUSED', 'EXPIRED')
    DEFAULT 'DRAFT';

--changeset yash:3 context:prod,dev,stage,docker,load,uat

ALTER TABLE job_master ADD COLUMN is_deleted  boolean default false;

ALTER TABLE job_master ADD COLUMN deleted_by  varchar(100);

--changeset yash:4 context:prod,dev,stage,docker,load,uat

CREATE INDEX JOB_MASTER_CREATED_AT_IDX on job_master  (created_at);

create index JOB_MASTER_CREATED_BY_IDX on job_master (created_by);

CREATE INDEX JOB_MASTER_JOB_NAME_IDX ON job_master (job_name);
