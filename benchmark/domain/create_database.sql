drop table if exists address cascade; 
drop table if exists city cascade; 
drop table if exists country cascade;
drop table if exists person cascade;

create table address (address_id bigint not null, city_id bigint, name varchar(255), primary key (address_id));
create table city (city_id bigint not null, country_id bigint, name varchar(255), primary key (city_id));
create table country (country_id bigint not null, name varchar(255), primary key (country_id));
create table person (person_id bigint not null, firstname varchar(255), lastname varchar(255), primary key (person_id));

alter table if exists address add constraint fk_address_city foreign key (city_id) references city;
alter table if exists address add constraint fk_address_person foreign key (address_id) references person;
alter table if exists city add constraint fk_city_country foreign key (country_id) references country;
