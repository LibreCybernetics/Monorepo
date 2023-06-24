SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: cooperative; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cooperative (
    id smallint NOT NULL,
    name text NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    url text NOT NULL,
    byline text,
    CONSTRAINT cooperative_url_check CHECK ((url ~ '^[\w\d_\-]+$'::text))
);


--
-- Name: cooperative_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cooperative_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cooperative_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cooperative_id_seq OWNED BY public.cooperative.id;


--
-- Name: product_specification; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.product_specification (
    id integer NOT NULL,
    owner smallint,
    name text NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: product_specification_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.product_specification_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: product_specification_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.product_specification_id_seq OWNED BY public.product_specification.id;


--
-- Name: product_specification_inheritance; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.product_specification_inheritance (
    id integer NOT NULL,
    inherits_from integer NOT NULL
);


--
-- Name: product_specification_inheritance_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.product_specification_inheritance_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: product_specification_inheritance_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.product_specification_inheritance_id_seq OWNED BY public.product_specification_inheritance.id;


--
-- Name: product_specification_inheritance_inherits_from_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.product_specification_inheritance_inherits_from_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: product_specification_inheritance_inherits_from_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.product_specification_inheritance_inherits_from_seq OWNED BY public.product_specification_inheritance.inherits_from;


--
-- Name: schema_migrations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.schema_migrations (
    version character varying(128) NOT NULL
);


--
-- Name: cooperative id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cooperative ALTER COLUMN id SET DEFAULT nextval('public.cooperative_id_seq'::regclass);


--
-- Name: product_specification id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_specification ALTER COLUMN id SET DEFAULT nextval('public.product_specification_id_seq'::regclass);


--
-- Name: product_specification_inheritance id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_specification_inheritance ALTER COLUMN id SET DEFAULT nextval('public.product_specification_inheritance_id_seq'::regclass);


--
-- Name: product_specification_inheritance inherits_from; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_specification_inheritance ALTER COLUMN inherits_from SET DEFAULT nextval('public.product_specification_inheritance_inherits_from_seq'::regclass);


--
-- Name: cooperative cooperative_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cooperative
    ADD CONSTRAINT cooperative_pkey PRIMARY KEY (id);


--
-- Name: cooperative cooperative_url_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cooperative
    ADD CONSTRAINT cooperative_url_key UNIQUE (url);


--
-- Name: product_specification product_specification_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_specification
    ADD CONSTRAINT product_specification_pkey PRIMARY KEY (id);


--
-- Name: schema_migrations schema_migrations_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.schema_migrations
    ADD CONSTRAINT schema_migrations_pkey PRIMARY KEY (version);


--
-- Name: product_specification_inheritance product_specification_inheritance_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_specification_inheritance
    ADD CONSTRAINT product_specification_inheritance_id_fkey FOREIGN KEY (id) REFERENCES public.product_specification(id);


--
-- Name: product_specification_inheritance product_specification_inheritance_inherits_from_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_specification_inheritance
    ADD CONSTRAINT product_specification_inheritance_inherits_from_fkey FOREIGN KEY (inherits_from) REFERENCES public.product_specification(id);


--
-- Name: product_specification product_specification_owner_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_specification
    ADD CONSTRAINT product_specification_owner_fkey FOREIGN KEY (owner) REFERENCES public.cooperative(id);


--
-- PostgreSQL database dump complete
--


--
-- Dbmate schema migrations
--

INSERT INTO public.schema_migrations (version) VALUES
    ('20230617211306'),
    ('20230622233123'),
    ('20230622235120'),
    ('20230624005853');
