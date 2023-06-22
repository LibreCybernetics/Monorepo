-- migrate:up

alter table cooperative add column byline text;

-- migrate:down

alter table cooperative drop column byline;
