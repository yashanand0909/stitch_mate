--liquibase formatted sql

--preconditions onFail:HALT onError:HALT

--changeset yash:1 context:dev,stage,docker,load,test,prod,uat

CREATE TABLE IF NOT EXISTS jobs_tags_relation(
    id bigint PRIMARY KEY AUTO_INCREMENT,
    job_id bigint not null COMMENT  'Id of Job',
    tag_id bigint NOT NULL,
    created_at   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   varchar(50)  NOT NULL,
    CONSTRAINT FK_JOB_TAG_RELATION_JOB_MASTER_JOB_ID FOREIGN KEY (job_id) REFERENCES job_master (job_id),
    CONSTRAINT FK_JOB_TAG_RELATION_JOB_MASTER_TAG_ID FOREIGN KEY (tag_id) REFERENCES tag_master (tag_id)
);

create unique index UNQ_JOB_TAG_IDX on jobs_tags_relation(job_id, tag_id)

