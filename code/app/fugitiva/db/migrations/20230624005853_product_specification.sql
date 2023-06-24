-- migrate:up

create table product_specification(
    id serial primary key,
    owner smallint references cooperative(id),
    name text not null,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now()
);

create table product_specification_inheritance(
    id serial references product_specification(id) not null,
    inherits_from serial references product_specification(id) not null
);

-- migrate:down

drop table product_specification_inheritance;
drop table product_specification;
