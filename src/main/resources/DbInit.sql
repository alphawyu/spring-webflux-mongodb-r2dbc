CREATE TABLE stock(
        id bigserial NOT NULL,
        tick character varying(15) ,
        name character varying(255) ,
        CONSTRAINT pk_stock_id PRIMARY KEY (id)
);