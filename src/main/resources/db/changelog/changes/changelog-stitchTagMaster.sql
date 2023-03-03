--liquibase formatted sql

--preconditions onFail:HALT onError:HALT

--changeset yash:1 context:dev,stage,docker,load,test,prod,uat

CREATE TABLE IF NOT EXISTS tag_master(
    tag_id bigint PRIMARY KEY AUTO_INCREMENT,
    tag   varchar(255) NOT NULL,
    tag_description mediumtext,
    created_at   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   varchar(50)  NOT NULL
)

