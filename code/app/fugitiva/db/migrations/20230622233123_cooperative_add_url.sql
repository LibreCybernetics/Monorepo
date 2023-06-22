-- migrate:up

alter table cooperative add column url text unique check (url ~ '^[\w\d_\-]+$');
update cooperative set url = replace(name, ' ', '_') where url is null;
alter table cooperative alter column url set not null;

-- migrate:down

alter table cooperative drop column url;
