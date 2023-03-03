--liquibase formatted sql

--preconditions onFail:HALT onError:HALT

--changeset yash:1 context:prod,dev,stage,docker,load,uat

CREATE TABLE node_runs (
                                  nodes_run_id bigint PRIMARY KEY AUTO_INCREMENT,
                                  update_by varchar(255) not null,
                                  job_id bigint not null COMMENT  'Id of job which node is part of',
                                  dag_run_id varchar (100),
                                  node_id bigint,
                                  node_name varchar(255),
                                  executed_query text,
                                  execution_summary text,
                                  destination varchar(255),
                                  node_status ENUM ('RUNNING', 'SUCCESS', 'FAILED') not null,
                                  created_at timestamp default CURRENT_TIMESTAMP not null,
                                  recupdated_at  timestamp on update CURRENT_TIMESTAMP not null,
                                  CONSTRAINT FK_NODE_RUNS_JOB_MASTER_JOB_NAME FOREIGN KEY (job_id) REFERENCES job_master (job_id)



);

CREATE INDEX Index_recupdatedAt ON node_runs (recupdated_at);
CREATE INDEX Index_JobNameDagRunId ON node_runs (job_id,dag_run_id);

--changeset yash:2 context:prod,dev,stage,docker,load,uat

ALTER TABLE node_runs ADD COLUMN metadata text NULL;

--changeset yash:3 context:prod,dev,stage,docker,load,uat

create index NODE_RUNS_CREATED_AT_IDX on node_runs (created_at);

create index NODE_RUNS_NODE_STATUS_IDX on node_runs (node_status);

create index NODE_RUNS_JOB_ID on node_runs (job_id);


