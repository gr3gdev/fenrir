drop table if exists users cascade;

create table users (user_id varchar(128) not null, name varchar(255), email varchar(255), primary key (user_id));
