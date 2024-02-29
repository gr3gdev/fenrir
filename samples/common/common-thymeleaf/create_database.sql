drop table if exists users cascade;
drop table if exists prefs cascade;

create table users (user_id varchar(128) not null, name varchar(255), email varchar(255), primary key (user_id));
create table prefs (pref_id bigint not null, locale varchar(255), primary key (pref_id));
