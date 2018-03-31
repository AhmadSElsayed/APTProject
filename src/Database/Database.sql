-- Drop Tables
drop TABLE if EXISTS inverted_index;
DROP TABLE if EXISTS stemmed_word;
DROP TABLE if EXISTS original_word;
drop TABLE if EXISTS visited_links;
drop TABLE if EXISTS seed_set;
drop TABLE if EXISTS robots;
drop SEQUENCE if EXISTS stemmed_word_id_seq;
drop SEQUENCE if EXISTS original_word_id_seq;
DROP SEQUENCE if EXISTS inverted_index_id_seq;
drop SEQUENCE if EXISTS visited_links_id_seq;
drop SEQUENCE if EXISTS seed_set_id_seq;
DROP SEQUENCE if EXISTS robots_id_seq;
-- Crawler Database
-- Create Tables
create sequence visited_links_id_seq;
create sequence seed_set_id_seq;
create sequence robots_id_seq;
create table visited_links
(
  id BIGINT default nextval('visited_links_id_seq'::regclass) not null primary key,
  url text not null unique
);

create table seed_set
(
  id BIGINT default nextval('seed_set_id_seq'::regclass) not null primary key,
  url text not null unique
);

create table robots
(
  id BIGINT default nextval('robots_id_seq'::regclass) not null primary key,
  base_url text not null unique,
  regex text not null
);
-- Indexer Database
-- Create Tables
create SEQUENCE stemmed_word_id_seq;
CREATE SEQUENCE original_word_id_seq;
CREATE SEQUENCE inverted_index_id_seq;


CREATE TABLE stemmed_word
(
  id BIGINT PRIMARY KEY UNIQUE not null DEFAULT nextval('stemmed_word_id_seq'::regclass),
  word text UNIQUE NOT NULL
);
CREATE TABLE original_word
(
  id BIGINT PRIMARY KEY UNIQUE not null DEFAULT nextval('original_word_id_seq'::regclass),
  word text UNIQUE NOT NULL
);
CREATE TABLE inverted_index
(
  id BIGINT PRIMARY KEY UNIQUE not null DEFAULT nextval('inverted_index_id_seq'::regclass),
  stemmed_id BIGINT REFERENCES stemmed_word(id),
  original_id BIGINT REFERENCES original_word(id),
  url_id BIGINT REFERENCES  visited_links(id),
  position BIGINT
);
