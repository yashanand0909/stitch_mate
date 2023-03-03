--liquibase formatted sql

--preconditions onFail:HALT onError:HALT

--changeset yash:1 context:prod,dev,stage,docker,load,uat

CREATE TABLE config_master (
                                    id bigint PRIMARY KEY AUTO_INCREMENT,
                                    config_key varchar(255) not null,
                                    config_value text not null,
                                    created_by   varchar(50)  NOT NULL,
                                    updated_by   varchar(50)  NOT NULL,
                                    created_at timestamp default CURRENT_TIMESTAMP not null,
                                    recupdated_at timestamp on update CURRENT_TIMESTAMP not null
                                 );


CREATE INDEX Index_recupdatedAt ON config_master (recupdated_at);

--changeset yash:2 context:prod,dev,stage,docker,load,uat

CREATE INDEX CONFIG_MASTER_CONFIG_KEY_IDX ON config_master (config_key);
