--liquibase formatted sql

--preconditions onFail:HALT onError:HALT

--changeset yash:1 context:prod,dev,stage,docker,load,uat

CREATE TABLE edge_master (
                                    edge_id bigint PRIMARY KEY AUTO_INCREMENT,
                                    job_id bigint not null,
                                    to_node bigint not null,
                                    from_node bigint not null,
                                    created_at timestamp default CURRENT_TIMESTAMP not null,
                                    recupdated_at timestamp on update CURRENT_TIMESTAMP not null,
                                    is_deleted smallint comment 'To mark if the edge is deleted of not',
                                    created_by VARCHAR(100) comment 'Detail of user who created the edge',
                                    deleted_by VARCHAR(100) comment 'Detail of user who deleted the edge',
                                    CONSTRAINT fkEdgeMasterToNode FOREIGN KEY (to_node) REFERENCES node_master (node_id),
                                    CONSTRAINT fkEdgeMasterFromNode FOREIGN KEY (from_node) REFERENCES node_master (node_id),
                                    CONSTRAINT FK_EDGE_MASTER_JOB_MASTER_JOB_ID FOREIGN KEY (job_id) REFERENCES job_master (job_id)
                                 );


CREATE INDEX Index_recupdatedAt ON edge_master (recupdated_at) ;

--changeset yash:2 context:prod,dev,stage,docker,load,uat

CREATE INDEX EDGE_MASTER_JOB_ID_IDX ON edge_master (job_id);
