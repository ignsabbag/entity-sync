create table IF NOT EXISTS entity_version (id bigint auto_increment, entity varchar(255) null, version bigint null, commit_version bigint null, update_version bigint null);