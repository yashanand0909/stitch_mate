--liquibase formatted sql

--preconditions onFail:HALT onError:HALT

--changeset yash:1 context:prod,dev,stage,docker,load,uat

CREATE TABLE node_master (
                                    node_id bigint PRIMARY KEY AUTO_INCREMENT,
                                    job_id bigint not null ,
                                    node_name varchar(255) not null,
                                    source ENUM ('REDSHIFT', 'ATHENA', 'TEST') not null,
                                    created_by varchar(100) not null,
                                    updated_by varchar (100) ,
                                    query text,
                                    is_edited boolean  DEFAULT 0,
                                    destination_config text,
                                    destination_table varchar(255),
                                    parameter_config text,
                                    is_deleted  boolean,
                                    metadata text,
                                    created_at timestamp default CURRENT_TIMESTAMP not null,
                                    recupdated_at timestamp on update CURRENT_TIMESTAMP not null,
                                    CONSTRAINT fkNodeMasterJobId FOREIGN KEY (job_id) REFERENCES job_master (job_id)
);


CREATE INDEX Index_recupdatedAt ON node_master (recupdated_at);

--changeset yash:2 context:prod,dev,stage,docker,load,uat

create index NODE_MASTER_JOB_ID_IDX on node_master (job_id);

create index NODE_MASTER_CREATED_AT_IDX on node_master (created_at);

create index NODE_MASTER_SOURCE_IDX ON node_master (source);

create index NODE_MASTER_CREATED_BY_IDX ON node_master (created_by);

