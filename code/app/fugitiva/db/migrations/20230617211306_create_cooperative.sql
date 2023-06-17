-- migrate:up

create table cooperative (
    id smallserial primary key,
    name text not null,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now()
);

-- migrate:down

drop table if exists cooperative;
