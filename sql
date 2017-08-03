create schema fh;

create table fh.player (id serial, first_name varchar(255), surname varchar(255), login varchar(255), total_chips integer);

insert into fh.player (first_name, surname, login, total_chips) values ('mark', 'folkard', 'mf7', 10000);