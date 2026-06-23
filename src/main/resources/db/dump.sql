--
-- PostgreSQL database dump
--

\restrict dLp8xzb7dp0ffydbyIAPSBK8sHURJLf54W0tCdujxgrsBHPIuH0kw5KiR8jtGYa

-- Dumped from database version 18.1
-- Dumped by pg_dump version 18.0

-- Started on 2026-06-20 19:20:41

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 262 (class 1255 OID 83583)
-- Name: set_product_price_on_sale(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.set_product_price_on_sale() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    SELECT pp.price
    INTO NEW.product_price_on_sale_date
    FROM product_price pp
    JOIN sale s ON s.id = NEW.sale_id
    WHERE pp.product_id = NEW.product_id
      AND pp.price_change_date::date <= s.date
    ORDER BY pp.price_change_date DESC
    LIMIT 1;

     -- 2. Se não encontrou (preço nulo), busca o preço mais antigo disponível
    IF NEW.product_price_on_sale_date IS NULL THEN
        SELECT pp.price
        INTO NEW.product_price_on_sale_date
        FROM product_price pp
        WHERE pp.product_id = NEW.product_id
        ORDER BY pp.price_change_date ASC -- Pega o primeiro registro histórico
        LIMIT 1;
    END IF;

    -- Opcional: Se AINDA assim for nulo, significa que o produto não existe na tabela de preços
    IF NEW.product_price_on_sale_date IS NULL THEN
        RAISE EXCEPTION 'Produto % não possui nenhum preço em toda a base de dados', NEW.product_id;
    END IF;

    RETURN NEW;
END;
$$;


ALTER FUNCTION public.set_product_price_on_sale() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 219 (class 1259 OID 83584)
-- Name: city; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.city (
    id smallint NOT NULL,
    name text NOT NULL
);


ALTER TABLE public.city OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 83591)
-- Name: city_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.city_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.city_id_seq OWNER TO postgres;

--
-- TOC entry 5255 (class 0 OID 0)
-- Dependencies: 220
-- Name: city_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.city_id_seq OWNED BY public.city.id;


--
-- TOC entry 221 (class 1259 OID 83592)
-- Name: company_partner; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.company_partner (
    id uuid NOT NULL,
    cnpj character varying(14)
);


ALTER TABLE public.company_partner OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 83596)
-- Name: partner; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.partner (
    id uuid NOT NULL,
    name text NOT NULL,
    email text,
    phone text,
    city_id smallint CONSTRAINT partner_city_not_null NOT NULL,
    role_id smallint CONSTRAINT partner_category_not_null NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    deleted_at timestamp with time zone,
    confirmed boolean DEFAULT false CONSTRAINT partner_is_closed_not_null NOT NULL
);


ALTER TABLE public.partner OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 83609)
-- Name: personal_partner; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.personal_partner (
    id uuid NOT NULL,
    cpf character varying(11)
);


ALTER TABLE public.personal_partner OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 83613)
-- Name: client_full; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.client_full AS
 SELECT p.id,
    p.name,
    pp.cpf,
    cp.cnpj,
    p.phone,
    p.email,
    c.name AS city,
        CASE
            WHEN (pp.id IS NOT NULL) THEN 'p'::text
            WHEN (cp.id IS NOT NULL) THEN 'c'::text
            ELSE 'u'::text
        END AS category,
    p.confirmed
   FROM (((public.partner p
     LEFT JOIN public.personal_partner pp ON ((pp.id = p.id)))
     LEFT JOIN public.company_partner cp ON ((cp.id = p.id)))
     JOIN public.city c ON ((p.city_id = c.id)))
  WHERE ((p.role_id = 1) AND (p.deleted_at IS NULL));


ALTER VIEW public.client_full OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 83618)
-- Name: employee; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.employee (
    id uuid NOT NULL,
    name text NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    deleted_at timestamp with time zone,
    confirmed boolean DEFAULT false CONSTRAINT employee_is_closed_not_null NOT NULL
);


ALTER TABLE public.employee OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 83629)
-- Name: employee_payment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.employee_payment (
    id integer CONSTRAINT employee_wage_id_not_null NOT NULL,
    payment numeric(38,2) CONSTRAINT employee_wage_wage_not_null NOT NULL,
    employee_id uuid CONSTRAINT employee_wage_employee_id_not_null NOT NULL,
    payment_change_date date CONSTRAINT employee_wage_wage_change_date_not_null NOT NULL,
    spent_category_id smallint DEFAULT 2 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    deleted_at timestamp with time zone,
    confirmed boolean DEFAULT false NOT NULL
);


ALTER TABLE public.employee_payment OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 83642)
-- Name: employee_wage_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.employee_wage_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.employee_wage_id_seq OWNER TO postgres;

--
-- TOC entry 5256 (class 0 OID 0)
-- Dependencies: 227
-- Name: employee_wage_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.employee_wage_id_seq OWNED BY public.employee_payment.id;


--
-- TOC entry 228 (class 1259 OID 83643)
-- Name: measurement_unit; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.measurement_unit (
    id smallint NOT NULL,
    name text NOT NULL,
    plural_name text NOT NULL,
    symbol character varying(3) NOT NULL,
    unit text
);


ALTER TABLE public.measurement_unit OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 83652)
-- Name: measurement_unit_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.measurement_unit_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.measurement_unit_id_seq OWNER TO postgres;

--
-- TOC entry 5257 (class 0 OID 0)
-- Dependencies: 229
-- Name: measurement_unit_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.measurement_unit_id_seq OWNED BY public.measurement_unit.id;


--
-- TOC entry 230 (class 1259 OID 83653)
-- Name: partner_role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.partner_role (
    id smallint NOT NULL,
    name text NOT NULL
);


ALTER TABLE public.partner_role OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 83660)
-- Name: partner_role_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.partner_role_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.partner_role_id_seq OWNER TO postgres;

--
-- TOC entry 5258 (class 0 OID 0)
-- Dependencies: 231
-- Name: partner_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.partner_role_id_seq OWNED BY public.partner_role.id;


--
-- TOC entry 232 (class 1259 OID 83661)
-- Name: product; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.product (
    id smallint NOT NULL,
    name text NOT NULL,
    description text NOT NULL,
    category_id smallint CONSTRAINT product_category_id_not_null1 NOT NULL,
    measurement_unit_id smallint NOT NULL,
    quantity numeric(38,3) NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    deleted_at timestamp with time zone,
    confirmed boolean DEFAULT false CONSTRAINT product_is_closed_not_null NOT NULL
);


ALTER TABLE public.product OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 83676)
-- Name: product_category; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.product_category (
    id smallint NOT NULL,
    name text NOT NULL
);


ALTER TABLE public.product_category OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 83683)
-- Name: product_category_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.product_category_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.product_category_id_seq OWNER TO postgres;

--
-- TOC entry 5259 (class 0 OID 0)
-- Dependencies: 234
-- Name: product_category_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.product_category_id_seq OWNED BY public.product_category.id;


--
-- TOC entry 235 (class 1259 OID 83684)
-- Name: product_price; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.product_price (
    id integer NOT NULL,
    product_id smallint NOT NULL,
    price numeric(38,2) NOT NULL,
    price_change_date timestamp with time zone NOT NULL
);


ALTER TABLE public.product_price OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 83691)
-- Name: product_full; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.product_full AS
 SELECT p.id,
    p.name,
    p.description,
    pc.name AS category,
    p.quantity,
    mu.unit AS measurement_unit_unit,
    mu.name AS measurement_unit_name,
    mu.plural_name AS measurement_unit_plural_name,
    mu.symbol AS measurement_unit_symbol,
    pp.price AS current_price,
    p.confirmed
   FROM (((public.product p
     JOIN public.measurement_unit mu ON ((p.measurement_unit_id = mu.id)))
     JOIN public.product_category pc ON ((p.category_id = pc.id)))
     LEFT JOIN LATERAL ( SELECT product_price.price
           FROM public.product_price
          WHERE (product_price.product_id = p.id)
          ORDER BY product_price.price_change_date DESC
         LIMIT 1) pp ON (true))
  WHERE (p.deleted_at IS NULL);


ALTER VIEW public.product_full OWNER TO postgres;

--
-- TOC entry 237 (class 1259 OID 83696)
-- Name: product_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.product_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.product_id_seq OWNER TO postgres;

--
-- TOC entry 5260 (class 0 OID 0)
-- Dependencies: 237
-- Name: product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.product_id_seq OWNED BY public.product.id;


--
-- TOC entry 238 (class 1259 OID 83697)
-- Name: product_price_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.product_price_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.product_price_id_seq OWNER TO postgres;

--
-- TOC entry 5261 (class 0 OID 0)
-- Dependencies: 238
-- Name: product_price_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.product_price_id_seq OWNED BY public.product_price.id;


--
-- TOC entry 239 (class 1259 OID 83698)
-- Name: production; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.production (
    id integer NOT NULL,
    product_id smallint NOT NULL,
    created_at timestamp with time zone NOT NULL,
    deleted_at timestamp with time zone,
    confirmed boolean DEFAULT false CONSTRAINT production_is_closed_not_null NOT NULL,
    quantity_produced numeric(38,3) NOT NULL,
    date date NOT NULL,
    gross_quantity_produced numeric(38,3) NOT NULL,
    gqp_measurement_unit_id smallint,
    raw_material_purchase_date date CONSTRAINT production_raw_matrial_purchase_date_not_null NOT NULL,
    quantity_used numeric(38,3) CONSTRAINT production_quantity_ued_not_null NOT NULL
);


ALTER TABLE public.production OWNER TO postgres;

--
-- TOC entry 240 (class 1259 OID 83711)
-- Name: production_full; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.production_full AS
SELECT
    NULL::integer AS id,
    NULL::text AS product_name,
    NULL::text AS product_description,
    NULL::numeric(38,3) AS product_quantity,
    NULL::numeric(38,2) AS product_current_price,
    NULL::character varying(3) AS product_quantity_measurement_unit,
    NULL::numeric(38,3) AS quantity_produced,
    NULL::numeric(38,3) AS gross_quantity_produced,
    NULL::text AS gross_quantity_measurement_unit_unit,
    NULL::text AS gross_quantity_measurement_unit_name,
    NULL::text AS gross_quantity_measurement_unit_plural_name,
    NULL::character varying(3) AS gross_quantity_measurement_unit_symbol,
    NULL::numeric AS avg_raw_material_unit_price,
    NULL::date AS raw_material_purchase_date,
    NULL::numeric(38,3) AS quantity_used,
    NULL::date AS date,
    NULL::timestamp with time zone AS created_at,
    NULL::timestamp with time zone AS deleted_at,
    NULL::boolean AS confirmed;


ALTER VIEW public.production_full OWNER TO postgres;

--
-- TOC entry 241 (class 1259 OID 83715)
-- Name: production_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.production_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.production_id_seq OWNER TO postgres;

--
-- TOC entry 5262 (class 0 OID 0)
-- Dependencies: 241
-- Name: production_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.production_id_seq OWNED BY public.production.id;


--
-- TOC entry 242 (class 1259 OID 83716)
-- Name: purchase; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.purchase (
    id integer NOT NULL,
    partner_id uuid NOT NULL,
    raw_material_id smallint NOT NULL,
    quantity numeric(38,3) NOT NULL,
    measurement_unit_id smallint NOT NULL,
    price_per_unit numeric(38,2) CONSTRAINT purchase_price_not_null NOT NULL,
    date date NOT NULL,
    created_at timestamp with time zone DEFAULT now() CONSTRAINT purchase_create_at_not_null NOT NULL,
    deleted_at timestamp with time zone,
    confirmed boolean DEFAULT false CONSTRAINT purchase_is_closed_not_null NOT NULL,
    note text,
    spent_category_id smallint DEFAULT 1 CONSTRAINT purchase_spent_category_not_null NOT NULL
);


ALTER TABLE public.purchase OWNER TO postgres;

--
-- TOC entry 243 (class 1259 OID 83734)
-- Name: raw_material; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.raw_material (
    id smallint NOT NULL,
    name text NOT NULL,
    description text NOT NULL,
    created_at timestamp with time zone NOT NULL,
    deleted_at timestamp with time zone
);


ALTER TABLE public.raw_material OWNER TO postgres;

--
-- TOC entry 244 (class 1259 OID 83743)
-- Name: spent_category; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.spent_category (
    id smallint CONSTRAINT spent_category_id_not_null1 NOT NULL,
    name text NOT NULL
);


ALTER TABLE public.spent_category OWNER TO postgres;

--
-- TOC entry 245 (class 1259 OID 83750)
-- Name: purchase_full; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.purchase_full AS
 SELECT p.id,
    prs.name AS partner_name,
        CASE
            WHEN (pp.id IS NOT NULL) THEN 'p'::text
            WHEN (cp.id IS NOT NULL) THEN 'c'::text
            ELSE 'u'::text
        END AS partner_category,
    rm.name AS raw_material_name,
    rm.description AS raw_material_description,
    p.quantity,
    mu.unit AS measurement_unit_unit,
    mu.name AS measurement_unit_name,
    mu.plural_name AS measurement_unit_plural_name,
    mu.symbol AS measurement_unit_symbol,
    sc.id AS spent_category_id,
    p.price_per_unit,
    p.date,
    p.note,
    p.created_at,
    p.deleted_at,
    p.confirmed
   FROM ((((((public.purchase p
     JOIN public.partner prs ON ((prs.id = p.partner_id)))
     JOIN public.spent_category sc ON ((sc.id = p.spent_category_id)))
     LEFT JOIN public.personal_partner pp ON ((pp.id = p.partner_id)))
     LEFT JOIN public.company_partner cp ON ((cp.id = p.partner_id)))
     JOIN public.raw_material rm ON ((rm.id = p.raw_material_id)))
     JOIN public.measurement_unit mu ON ((mu.id = p.measurement_unit_id)))
  WHERE (p.deleted_at IS NULL);


ALTER VIEW public.purchase_full OWNER TO postgres;

--
-- TOC entry 246 (class 1259 OID 83755)
-- Name: purchase_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.purchase_id_seq
    START WITH 1001
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.purchase_id_seq OWNER TO postgres;

--
-- TOC entry 5263 (class 0 OID 0)
-- Dependencies: 246
-- Name: purchase_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.purchase_id_seq OWNED BY public.purchase.id;


--
-- TOC entry 247 (class 1259 OID 83756)
-- Name: raw_material_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.raw_material_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.raw_material_id_seq OWNER TO postgres;

--
-- TOC entry 5264 (class 0 OID 0)
-- Dependencies: 247
-- Name: raw_material_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.raw_material_id_seq OWNED BY public.raw_material.id;


--
-- TOC entry 248 (class 1259 OID 83757)
-- Name: sale; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sale (
    id integer NOT NULL,
    client_id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    deleted_at timestamp with time zone,
    confirmed boolean DEFAULT false CONSTRAINT sale_is_closed_not_null NOT NULL,
    date date NOT NULL
);


ALTER TABLE public.sale OWNER TO postgres;

--
-- TOC entry 249 (class 1259 OID 83767)
-- Name: sale_full; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.sale_full AS
 SELECT s.id,
    cf.id AS client_id,
    cf.name AS client_name,
    cf.category AS client_category,
    cf.cpf AS client_cpf,
    cf.cnpj AS client_cnpj,
    cf.city AS client_city,
    s.date,
    s.created_at,
    s.deleted_at,
    s.confirmed
   FROM (public.sale s
     JOIN public.client_full cf ON ((cf.id = s.client_id)))
  WHERE (s.deleted_at IS NULL);


ALTER VIEW public.sale_full OWNER TO postgres;

--
-- TOC entry 250 (class 1259 OID 83771)
-- Name: sale_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.sale_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sale_id_seq OWNER TO postgres;

--
-- TOC entry 5265 (class 0 OID 0)
-- Dependencies: 250
-- Name: sale_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.sale_id_seq OWNED BY public.sale.id;


--
-- TOC entry 251 (class 1259 OID 83772)
-- Name: sale_product; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sale_product (
    id integer NOT NULL,
    product_id integer NOT NULL,
    quantity numeric(38,3) NOT NULL,
    sale_id integer NOT NULL,
    product_price_on_sale_date numeric(38,2) DEFAULT 1 CONSTRAINT sale_product_product_price_on_date_not_null NOT NULL
);


ALTER TABLE public.sale_product OWNER TO postgres;

--
-- TOC entry 257 (class 1259 OID 230943)
-- Name: sale_product_full; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.sale_product_full AS
 SELECT sp.id,
    sp.sale_id,
    prod.id AS product_id,
    prod.name AS product_name,
    prod.description AS product_description,
    prod.quantity AS product_weight,
    mu.unit AS product_measurement_unit_unit,
    mu.name AS product_measurement_unit_name,
    mu.plural_name AS product_measurement_unit_plural_name,
    mu.symbol AS product_measurement_unit_symbol,
    sp.product_price_on_sale_date,
    sp.quantity AS product_quantity_sold
   FROM (((public.sale_product sp
     JOIN public.product prod ON ((prod.id = sp.product_id)))
     JOIN public.measurement_unit mu ON ((prod.measurement_unit_id = mu.id)))
     LEFT JOIN LATERAL ( SELECT product_price.price
           FROM public.product_price
          WHERE (product_price.product_id = prod.id)
          ORDER BY product_price.price_change_date DESC
         LIMIT 1) pp ON (true));


ALTER VIEW public.sale_product_full OWNER TO postgres;

--
-- TOC entry 252 (class 1259 OID 83786)
-- Name: sale_product_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.sale_product_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sale_product_id_seq OWNER TO postgres;

--
-- TOC entry 5266 (class 0 OID 0)
-- Dependencies: 252
-- Name: sale_product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.sale_product_id_seq OWNED BY public.sale_product.id;


--
-- TOC entry 253 (class 1259 OID 83787)
-- Name: spent; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.spent (
    id integer NOT NULL,
    description text NOT NULL,
    amount_paid numeric(38,2) CONSTRAINT spent_price_not_null NOT NULL,
    category_id smallint NOT NULL,
    date date NOT NULL,
    created_at timestamp with time zone DEFAULT now() CONSTRAINT "spent_createdAt_not_null" NOT NULL,
    deleted_at timestamp with time zone,
    confirmed boolean DEFAULT false CONSTRAINT spent_is_closed_not_null NOT NULL
);


ALTER TABLE public.spent OWNER TO postgres;

--
-- TOC entry 254 (class 1259 OID 83801)
-- Name: spent_category_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.spent_category_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.spent_category_id_seq OWNER TO postgres;

--
-- TOC entry 5267 (class 0 OID 0)
-- Dependencies: 254
-- Name: spent_category_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.spent_category_id_seq OWNED BY public.spent_category.id;


--
-- TOC entry 255 (class 1259 OID 83802)
-- Name: spent_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.spent_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.spent_id_seq OWNER TO postgres;

--
-- TOC entry 5268 (class 0 OID 0)
-- Dependencies: 255
-- Name: spent_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.spent_id_seq OWNED BY public.spent.id;


--
-- TOC entry 256 (class 1259 OID 83803)
-- Name: supplier_full; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.supplier_full AS
 SELECT p.id,
    p.name,
    pp.cpf,
    cp.cnpj,
    p.phone,
    p.email,
    c.name AS city,
        CASE
            WHEN (pp.id IS NOT NULL) THEN 'p'::text
            WHEN (cp.id IS NOT NULL) THEN 'c'::text
            ELSE 'u'::text
        END AS category,
    p.confirmed
   FROM (((public.partner p
     LEFT JOIN public.personal_partner pp ON ((pp.id = p.id)))
     LEFT JOIN public.company_partner cp ON ((cp.id = p.id)))
     JOIN public.city c ON ((p.city_id = c.id)))
  WHERE ((p.role_id = 2) AND (p.deleted_at IS NULL));


ALTER VIEW public.supplier_full OWNER TO postgres;

--
-- TOC entry 258 (class 1259 OID 230951)
-- Name: vehicle; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vehicle (
    number_plate text NOT NULL,
    model text NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    deleted_at timestamp with time zone,
    confirmed boolean DEFAULT false NOT NULL
);


ALTER TABLE public.vehicle OWNER TO postgres;

--
-- TOC entry 259 (class 1259 OID 230978)
-- Name: vehicle_spent; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vehicle_spent (
    id bigint NOT NULL,
    date date NOT NULL,
    note text,
    category_id smallint CONSTRAINT vehicle_spent_vehicle_spent_category_id_not_null NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    deleted_at timestamp with time zone,
    confirmed boolean DEFAULT false NOT NULL,
    quantity numeric(38,3) NOT NULL,
    amount_paid numeric(38,2) CONSTRAINT vehicle_spent_cost_not_null NOT NULL,
    vehicle_number_plate text CONSTRAINT vehicle_spent_vehicle_not_null NOT NULL
);


ALTER TABLE public.vehicle_spent OWNER TO postgres;

--
-- TOC entry 261 (class 1259 OID 231035)
-- Name: vehicle_spent_full; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.vehicle_spent_full AS
 SELECT vs.id,
    vs.date,
    vs.note,
    vs.category_id AS spent_category_id,
    vs.created_at,
    vs.deleted_at,
    vs.quantity,
    vs.amount_paid,
    v.number_plate AS vehicle_number_plate,
    v.model
   FROM (public.vehicle_spent vs
     JOIN public.vehicle v ON ((v.number_plate = vs.vehicle_number_plate)))
  WHERE (vs.deleted_at IS NULL);


ALTER VIEW public.vehicle_spent_full OWNER TO postgres;

--
-- TOC entry 260 (class 1259 OID 230981)
-- Name: vehicle_spent_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.vehicle_spent_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.vehicle_spent_id_seq OWNER TO postgres;

--
-- TOC entry 5269 (class 0 OID 0)
-- Dependencies: 260
-- Name: vehicle_spent_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.vehicle_spent_id_seq OWNED BY public.vehicle_spent.id;


--
-- TOC entry 4979 (class 2604 OID 83808)
-- Name: city id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.city ALTER COLUMN id SET DEFAULT nextval('public.city_id_seq'::regclass);


--
-- TOC entry 4984 (class 2604 OID 83809)
-- Name: employee_payment id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employee_payment ALTER COLUMN id SET DEFAULT nextval('public.employee_wage_id_seq'::regclass);


--
-- TOC entry 4988 (class 2604 OID 83810)
-- Name: measurement_unit id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.measurement_unit ALTER COLUMN id SET DEFAULT nextval('public.measurement_unit_id_seq'::regclass);


--
-- TOC entry 4989 (class 2604 OID 83811)
-- Name: partner_role id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.partner_role ALTER COLUMN id SET DEFAULT nextval('public.partner_role_id_seq'::regclass);


--
-- TOC entry 4990 (class 2604 OID 83812)
-- Name: product id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product ALTER COLUMN id SET DEFAULT nextval('public.product_id_seq'::regclass);


--
-- TOC entry 4993 (class 2604 OID 83813)
-- Name: product_category id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_category ALTER COLUMN id SET DEFAULT nextval('public.product_category_id_seq'::regclass);


--
-- TOC entry 4994 (class 2604 OID 83814)
-- Name: product_price id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_price ALTER COLUMN id SET DEFAULT nextval('public.product_price_id_seq'::regclass);


--
-- TOC entry 4995 (class 2604 OID 83815)
-- Name: production id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.production ALTER COLUMN id SET DEFAULT nextval('public.production_id_seq'::regclass);


--
-- TOC entry 4997 (class 2604 OID 83816)
-- Name: purchase id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase ALTER COLUMN id SET DEFAULT nextval('public.purchase_id_seq'::regclass);


--
-- TOC entry 5001 (class 2604 OID 83817)
-- Name: raw_material id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.raw_material ALTER COLUMN id SET DEFAULT nextval('public.raw_material_id_seq'::regclass);


--
-- TOC entry 5003 (class 2604 OID 83818)
-- Name: sale id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale ALTER COLUMN id SET DEFAULT nextval('public.sale_id_seq'::regclass);


--
-- TOC entry 5006 (class 2604 OID 83819)
-- Name: sale_product id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale_product ALTER COLUMN id SET DEFAULT nextval('public.sale_product_id_seq'::regclass);


--
-- TOC entry 5008 (class 2604 OID 83820)
-- Name: spent id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.spent ALTER COLUMN id SET DEFAULT nextval('public.spent_id_seq'::regclass);


--
-- TOC entry 5002 (class 2604 OID 83821)
-- Name: spent_category id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.spent_category ALTER COLUMN id SET DEFAULT nextval('public.spent_category_id_seq'::regclass);


--
-- TOC entry 5013 (class 2604 OID 230982)
-- Name: vehicle_spent id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vehicle_spent ALTER COLUMN id SET DEFAULT nextval('public.vehicle_spent_id_seq'::regclass);


--
-- TOC entry 5021 (class 2606 OID 83823)
-- Name: city city_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.city
    ADD CONSTRAINT city_pkey PRIMARY KEY (id);


--
-- TOC entry 5023 (class 2606 OID 83825)
-- Name: city city_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.city
    ADD CONSTRAINT city_unique UNIQUE (name);


--
-- TOC entry 5016 (class 2606 OID 83826)
-- Name: company_partner cnpj_format; Type: CHECK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.company_partner
    ADD CONSTRAINT cnpj_format CHECK (((cnpj IS NULL) OR ((cnpj)::text ~ '^[0-9]{14}$'::text))) NOT VALID;


--
-- TOC entry 5025 (class 2606 OID 83828)
-- Name: company_partner company_partner_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.company_partner
    ADD CONSTRAINT company_partner_pkey PRIMARY KEY (id);


--
-- TOC entry 5017 (class 2606 OID 83829)
-- Name: personal_partner cpf_format; Type: CHECK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.personal_partner
    ADD CONSTRAINT cpf_format CHECK (((cpf IS NULL) OR ((cpf)::text ~ '^[0-9]{11}$'::text))) NOT VALID;


--
-- TOC entry 5036 (class 2606 OID 83831)
-- Name: employee employee_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employee
    ADD CONSTRAINT employee_pkey PRIMARY KEY (id);


--
-- TOC entry 5038 (class 2606 OID 83833)
-- Name: employee_payment employee_wage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employee_payment
    ADD CONSTRAINT employee_wage_pkey PRIMARY KEY (id);


--
-- TOC entry 5041 (class 2606 OID 83835)
-- Name: measurement_unit measurement_unit_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.measurement_unit
    ADD CONSTRAINT measurement_unit_pkey PRIMARY KEY (id);


--
-- TOC entry 5030 (class 2606 OID 83837)
-- Name: partner partner_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.partner
    ADD CONSTRAINT partner_pkey PRIMARY KEY (id);


--
-- TOC entry 5043 (class 2606 OID 83839)
-- Name: partner_role partner_role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.partner_role
    ADD CONSTRAINT partner_role_pkey PRIMARY KEY (id);


--
-- TOC entry 5032 (class 2606 OID 83841)
-- Name: personal_partner personal_partner_cpf_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.personal_partner
    ADD CONSTRAINT personal_partner_cpf_key UNIQUE (cpf);


--
-- TOC entry 5034 (class 2606 OID 83843)
-- Name: personal_partner personal_partner_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.personal_partner
    ADD CONSTRAINT personal_partner_pkey PRIMARY KEY (id);


--
-- TOC entry 5048 (class 2606 OID 83845)
-- Name: product_category product_category_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_category
    ADD CONSTRAINT product_category_pkey PRIMARY KEY (id);


--
-- TOC entry 5046 (class 2606 OID 83847)
-- Name: product product_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT product_pkey PRIMARY KEY (id);


--
-- TOC entry 5050 (class 2606 OID 83849)
-- Name: product_price product_price_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_price
    ADD CONSTRAINT product_price_pkey PRIMARY KEY (id);


--
-- TOC entry 5052 (class 2606 OID 83851)
-- Name: production production_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.production
    ADD CONSTRAINT production_pkey PRIMARY KEY (id);


--
-- TOC entry 5056 (class 2606 OID 83853)
-- Name: purchase purchase_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT purchase_pkey PRIMARY KEY (id);


--
-- TOC entry 5058 (class 2606 OID 83855)
-- Name: raw_material raw_material_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.raw_material
    ADD CONSTRAINT raw_material_pkey PRIMARY KEY (id);


--
-- TOC entry 5063 (class 2606 OID 83857)
-- Name: sale sale_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale
    ADD CONSTRAINT sale_pkey PRIMARY KEY (id);


--
-- TOC entry 5066 (class 2606 OID 83859)
-- Name: sale_product sale_production_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale_product
    ADD CONSTRAINT sale_production_pkey PRIMARY KEY (id);


--
-- TOC entry 5019 (class 2606 OID 83860)
-- Name: purchase spent_category_id_checker; Type: CHECK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.purchase
    ADD CONSTRAINT spent_category_id_checker CHECK ((spent_category_id = 1)) NOT VALID;


--
-- TOC entry 5018 (class 2606 OID 83861)
-- Name: employee_payment spent_category_id_cheker; Type: CHECK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.employee_payment
    ADD CONSTRAINT spent_category_id_cheker CHECK ((spent_category_id = 2)) NOT VALID;


--
-- TOC entry 5060 (class 2606 OID 83863)
-- Name: spent_category spent_category_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.spent_category
    ADD CONSTRAINT spent_category_pkey PRIMARY KEY (id);


--
-- TOC entry 5068 (class 2606 OID 83865)
-- Name: spent spent_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.spent
    ADD CONSTRAINT spent_pkey PRIMARY KEY (id);


--
-- TOC entry 5070 (class 2606 OID 230958)
-- Name: vehicle vehicle_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vehicle
    ADD CONSTRAINT vehicle_pkey PRIMARY KEY (number_plate);


--
-- TOC entry 5075 (class 2606 OID 230998)
-- Name: vehicle_spent vehicle_spent_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vehicle_spent
    ADD CONSTRAINT vehicle_spent_pkey PRIMARY KEY (id);


--
-- TOC entry 5044 (class 1259 OID 83866)
-- Name: fki_category_fkey; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fki_category_fkey ON public.product USING btree (category_id);


--
-- TOC entry 5027 (class 1259 OID 83867)
-- Name: fki_city_fkey; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fki_city_fkey ON public.partner USING btree (city_id);


--
-- TOC entry 5061 (class 1259 OID 83868)
-- Name: fki_client_fkey; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fki_client_fkey ON public.sale USING btree (client_id);


--
-- TOC entry 5039 (class 1259 OID 83869)
-- Name: fki_employee_fkey; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fki_employee_fkey ON public.employee_payment USING btree (employee_id);


--
-- TOC entry 5053 (class 1259 OID 83870)
-- Name: fki_measurement_unit_fkey; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fki_measurement_unit_fkey ON public.purchase USING btree (measurement_unit_id);


--
-- TOC entry 5026 (class 1259 OID 83871)
-- Name: fki_partner_fkey; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fki_partner_fkey ON public.company_partner USING btree (id);


--
-- TOC entry 5064 (class 1259 OID 83872)
-- Name: fki_product_fkey; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fki_product_fkey ON public.sale_product USING btree (product_id);


--
-- TOC entry 5054 (class 1259 OID 83873)
-- Name: fki_raw_material_fkey; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fki_raw_material_fkey ON public.purchase USING btree (raw_material_id);


--
-- TOC entry 5028 (class 1259 OID 83874)
-- Name: fki_role_fkey; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fki_role_fkey ON public.partner USING btree (role_id);


--
-- TOC entry 5071 (class 1259 OID 231018)
-- Name: fki_spent_category_fkey; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fki_spent_category_fkey ON public.vehicle_spent USING btree (category_id);


--
-- TOC entry 5072 (class 1259 OID 231025)
-- Name: fki_vehicle_fkey; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fki_vehicle_fkey ON public.vehicle_spent USING btree (vehicle_number_plate);


--
-- TOC entry 5073 (class 1259 OID 231004)
-- Name: fki_vehicle_spent_category_fkey; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fki_vehicle_spent_category_fkey ON public.vehicle_spent USING btree (category_id);


--
-- TOC entry 5244 (class 2618 OID 83714)
-- Name: production_full _RETURN; Type: RULE; Schema: public; Owner: postgres
--

CREATE OR REPLACE VIEW public.production_full AS
 SELECT p.id,
    prod.name AS product_name,
    prod.description AS product_description,
    prod.quantity AS product_quantity,
    pp.price AS product_current_price,
    muu.symbol AS product_quantity_measurement_unit,
    p.quantity_produced,
    p.gross_quantity_produced,
    mu.unit AS gross_quantity_measurement_unit_unit,
    mu.name AS gross_quantity_measurement_unit_name,
    mu.plural_name AS gross_quantity_measurement_unit_plural_name,
    mu.symbol AS gross_quantity_measurement_unit_symbol,
    avg(pur.price_per_unit) AS avg_raw_material_unit_price,
    p.raw_material_purchase_date,
    p.quantity_used,
    p.date,
    p.created_at,
    p.deleted_at,
    p.confirmed
   FROM (((((public.production p
     JOIN public.product prod ON ((prod.id = p.product_id)))
     JOIN public.measurement_unit mu ON ((mu.id = p.gqp_measurement_unit_id)))
     JOIN public.measurement_unit muu ON ((muu.id = prod.measurement_unit_id)))
     LEFT JOIN LATERAL ( SELECT product_price.price
           FROM public.product_price
          WHERE (product_price.product_id = prod.id)
          ORDER BY product_price.price_change_date DESC
         LIMIT 1) pp ON (true))
     JOIN public.purchase pur ON ((pur.date = p.raw_material_purchase_date)))
  WHERE (p.deleted_at IS NULL)
  GROUP BY p.id, prod.name, prod.description, prod.quantity, pp.price, muu.symbol, p.quantity_produced, p.gross_quantity_produced, mu.unit, mu.name, mu.plural_name, mu.symbol, p.date, p.created_at, p.deleted_at, p.confirmed;


--
-- TOC entry 5094 (class 2620 OID 83876)
-- Name: sale_product trg_set_product_price_on_sale; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_set_product_price_on_sale BEFORE INSERT ON public.sale_product FOR EACH ROW EXECUTE FUNCTION public.set_product_price_on_sale();


--
-- TOC entry 5080 (class 2606 OID 83877)
-- Name: employee_payment category_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employee_payment
    ADD CONSTRAINT category_fkey FOREIGN KEY (spent_category_id) REFERENCES public.spent_category(id) NOT VALID;


--
-- TOC entry 5082 (class 2606 OID 83882)
-- Name: product category_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT category_fkey FOREIGN KEY (category_id) REFERENCES public.product_category(id) NOT VALID;


--
-- TOC entry 5085 (class 2606 OID 83887)
-- Name: purchase category_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT category_fkey FOREIGN KEY (spent_category_id) REFERENCES public.spent_category(id) NOT VALID;


--
-- TOC entry 5091 (class 2606 OID 83892)
-- Name: spent category_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.spent
    ADD CONSTRAINT category_fkey FOREIGN KEY (category_id) REFERENCES public.spent_category(id) NOT VALID;


--
-- TOC entry 5077 (class 2606 OID 83897)
-- Name: partner city_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.partner
    ADD CONSTRAINT city_fkey FOREIGN KEY (city_id) REFERENCES public.city(id) NOT VALID;


--
-- TOC entry 5089 (class 2606 OID 83902)
-- Name: sale client_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale
    ADD CONSTRAINT client_fkey FOREIGN KEY (client_id) REFERENCES public.partner(id) NOT VALID;


--
-- TOC entry 5081 (class 2606 OID 83907)
-- Name: employee_payment employee_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employee_payment
    ADD CONSTRAINT employee_fkey FOREIGN KEY (employee_id) REFERENCES public.employee(id) NOT VALID;


--
-- TOC entry 5083 (class 2606 OID 83912)
-- Name: product measurement_unit_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT measurement_unit_fkey FOREIGN KEY (measurement_unit_id) REFERENCES public.measurement_unit(id) NOT VALID;


--
-- TOC entry 5084 (class 2606 OID 83917)
-- Name: production measurement_unit_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.production
    ADD CONSTRAINT measurement_unit_fkey FOREIGN KEY (gqp_measurement_unit_id) REFERENCES public.measurement_unit(id) NOT VALID;


--
-- TOC entry 5086 (class 2606 OID 83922)
-- Name: purchase measurement_unit_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT measurement_unit_fkey FOREIGN KEY (measurement_unit_id) REFERENCES public.measurement_unit(id) NOT VALID;


--
-- TOC entry 5076 (class 2606 OID 83927)
-- Name: company_partner partner_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.company_partner
    ADD CONSTRAINT partner_fkey FOREIGN KEY (id) REFERENCES public.partner(id) NOT VALID;


--
-- TOC entry 5079 (class 2606 OID 83932)
-- Name: personal_partner partner_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.personal_partner
    ADD CONSTRAINT partner_fkey FOREIGN KEY (id) REFERENCES public.partner(id) NOT VALID;


--
-- TOC entry 5087 (class 2606 OID 83937)
-- Name: purchase partner_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT partner_fkey FOREIGN KEY (partner_id) REFERENCES public.partner(id) NOT VALID;


--
-- TOC entry 5090 (class 2606 OID 83942)
-- Name: sale_product product_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale_product
    ADD CONSTRAINT product_fkey FOREIGN KEY (product_id) REFERENCES public.product(id) NOT VALID;


--
-- TOC entry 5088 (class 2606 OID 83947)
-- Name: purchase raw_material_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT raw_material_fkey FOREIGN KEY (raw_material_id) REFERENCES public.raw_material(id) NOT VALID;


--
-- TOC entry 5078 (class 2606 OID 83952)
-- Name: partner role_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.partner
    ADD CONSTRAINT role_fkey FOREIGN KEY (role_id) REFERENCES public.partner_role(id) NOT VALID;


--
-- TOC entry 5092 (class 2606 OID 231013)
-- Name: vehicle_spent spent_category_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vehicle_spent
    ADD CONSTRAINT spent_category_fkey FOREIGN KEY (category_id) REFERENCES public.spent_category(id) NOT VALID;


--
-- TOC entry 5093 (class 2606 OID 231020)
-- Name: vehicle_spent vehicle_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vehicle_spent
    ADD CONSTRAINT vehicle_fkey FOREIGN KEY (vehicle_number_plate) REFERENCES public.vehicle(number_plate) NOT VALID;


-- Completed on 2026-06-20 19:20:41

--
-- PostgreSQL database dump complete
--

\unrestrict dLp8xzb7dp0ffydbyIAPSBK8sHURJLf54W0tCdujxgrsBHPIuH0kw5KiR8jtGYa

