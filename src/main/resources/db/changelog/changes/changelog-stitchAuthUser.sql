--liquibase formatted sql

--preconditions onFail:HALT onError:HALT

--changeset yash:1 context:prod,dev,stage,docker,load,uat

CREATE TABLE user (
                                            id bigint PRIMARY KEY AUTO_INCREMENT,
                                            username varchar(500) UNIQUE not null,
                                            password varchar(500),
                                            is_active boolean default 1,
                                            created_at timestamp default CURRENT_TIMESTAMP not null,
                                            recupdated_at timestamp on update CURRENT_TIMESTAMP not null
);
