CREATE TABLE biz_company
(
    id                 RAW(16)       NOT NULL,
    name               VARCHAR2(255) NOT NULL,
    date_created       TIMESTAMP,
    date_archived      TIMESTAMP,
    date_deleted       TIMESTAMP,
    created_by_user_id RAW(16),
    status             VARCHAR2(255),
    CONSTRAINT pk_biz_company PRIMARY KEY (id)
);

CREATE TABLE biz_permission
(
    id            RAW(16)       NOT NULL,
    key           VARCHAR2(255) NOT NULL,
    description   VARCHAR2(255) NOT NULL,
    domain        VARCHAR2(255) NOT NULL,
    date_created  TIMESTAMP NOT NULL,
    date_archived TIMESTAMP,
    date_deleted  TIMESTAMP,
    CONSTRAINT pk_biz_permission PRIMARY KEY (id)
);

CREATE TABLE biz_role
(
    id            RAW(16)       NOT NULL,
    company_id    RAW(16)       NOT NULL,
    name          VARCHAR2(255) NOT NULL,
    description   VARCHAR2(255),
    date_created  TIMESTAMP,
    date_archived TIMESTAMP,
    date_deleted  TIMESTAMP,
    CONSTRAINT pk_biz_role PRIMARY KEY (id)
);

CREATE TABLE biz_users
(
    id            RAW(16)       NOT NULL,
    first_name    VARCHAR2(255) NOT NULL,
    last_name     VARCHAR2(255) NOT NULL,
    email         VARCHAR2(255) NOT NULL,
    password_hash VARCHAR2(255) NOT NULL,
    date_created  TIMESTAMP,
    date_archived TIMESTAMP,
    CONSTRAINT pk_biz_users PRIMARY KEY (id)
);

CREATE TABLE fin_transaction
(
    id                  RAW(16)       NOT NULL,
    created_by_user_id  RAW(16)       NOT NULL,
    company             RAW(16)       NOT NULL,
    amount              DECIMAL NOT NULL,
    status              VARCHAR2(255) NOT NULL,
    date_of_transaction TIMESTAMP,
    description         VARCHAR2(200),
    date_created        TIMESTAMP,
    date_archived       TIMESTAMP,
    date_deleted        TIMESTAMP,
    CONSTRAINT pk_fin_transaction PRIMARY KEY (id)
);

CREATE TABLE inv_product
(
    id                 RAW(16)          NOT NULL,
    created_by_user_id RAW(16),
    company_id         RAW(16)          NOT NULL,
    sku                VARCHAR2(50)     NOT NULL,
    name               VARCHAR2(255)    NOT NULL,
    description        VARCHAR2(255),
    quantity_in_stock  INTEGER          NOT NULL,
    previous_price     DOUBLE PRECISION,
    current_price      DOUBLE PRECISION NOT NULL,
    next_price         DOUBLE PRECISION,
    sale_price         DOUBLE PRECISION,
    date_deleted       TIMESTAMP,
    date_archived      TIMESTAMP,
    date_created       TIMESTAMP,
    CONSTRAINT pk_inv_product PRIMARY KEY (id)
);

CREATE TABLE join_company_member
(
    id            RAW(16) NOT NULL,
    user_id       RAW(16) NOT NULL,
    company_id    RAW(16) NOT NULL,
    date_created  TIMESTAMP,
    date_archived TIMESTAMP,
    date_deleted  TIMESTAMP,
    CONSTRAINT pk_join_company_member PRIMARY KEY (id)
);

CREATE TABLE join_role_permission
(
    id            RAW(16) NOT NULL,
    role_id       RAW(16) NOT NULL,
    permission_id RAW(16) NOT NULL,
    date_created  TIMESTAMP,
    date_archived TIMESTAMP,
    date_deleted  TIMESTAMP,
    CONSTRAINT pk_join_role_permission PRIMARY KEY (id)
);

CREATE TABLE join_user_company_role
(
    id                RAW(16)   NOT NULL,
    company_member_id RAW(16)   NOT NULL,
    role_id           RAW(16)   NOT NULL,
    date_created      TIMESTAMP NOT NULL,
    date_archived     TIMESTAMP,
    date_deleted      TIMESTAMP,
    CONSTRAINT pk_join_user_company_role PRIMARY KEY (id)
);

ALTER TABLE join_role_permission
    ADD CONSTRAINT uc_5a37cbbb468cfe049ab7ab7e4 UNIQUE (role_id, permission_id);

ALTER TABLE join_company_member
    ADD CONSTRAINT uc_a6267a8b55e3a9b53b4f03d20 UNIQUE (user_id, company_id);

ALTER TABLE biz_role
    ADD CONSTRAINT uc_b81ce6485cdfcd490e065bc77 UNIQUE (company_id, name);

ALTER TABLE biz_permission
    ADD CONSTRAINT uc_biz_permission_key UNIQUE (key);

ALTER TABLE biz_users
    ADD CONSTRAINT uc_biz_users_lastname UNIQUE (last_name);

ALTER TABLE inv_product
    ADD CONSTRAINT uc_cc8a1e5e6d21f34bb0578c24d UNIQUE (sku);

ALTER TABLE join_user_company_role
    ADD CONSTRAINT uc_d7c88ec9f21719a27b25ede0d UNIQUE (company_member_id, role_id);

ALTER TABLE audit_transaction
    ADD CONSTRAINT FK_AUDIT_TRANSACTION_ON_PERFORMED_BY FOREIGN KEY (performed_by) REFERENCES biz_users (id);

ALTER TABLE audit_transaction
    ADD CONSTRAINT FK_AUDIT_TRANSACTION_ON_TRANSACTION FOREIGN KEY (transaction_id) REFERENCES fin_transaction (id);

ALTER TABLE biz_company
    ADD CONSTRAINT FK_BIZ_COMPANY_ON_CREATED_BY_USER FOREIGN KEY (created_by_user_id) REFERENCES biz_users (id);

ALTER TABLE biz_role
    ADD CONSTRAINT FK_BIZ_ROLE_ON_COMPANY FOREIGN KEY (company_id) REFERENCES biz_company (id);

ALTER TABLE fin_transaction
    ADD CONSTRAINT FK_FIN_TRANSACTION_ON_COMPANY FOREIGN KEY (company) REFERENCES biz_company (id);

ALTER TABLE fin_transaction
    ADD CONSTRAINT FK_FIN_TRANSACTION_ON_CREATED_BY_USER FOREIGN KEY (created_by_user_id) REFERENCES biz_users (id);

ALTER TABLE inv_product
    ADD CONSTRAINT FK_INV_PRODUCT_ON_COMPANY FOREIGN KEY (company_id) REFERENCES biz_company (id);

ALTER TABLE inv_product
    ADD CONSTRAINT FK_INV_PRODUCT_ON_CREATED_BY_USER FOREIGN KEY (created_by_user_id) REFERENCES biz_users (id);

ALTER TABLE join_company_member
    ADD CONSTRAINT FK_JOIN_COMPANY_MEMBER_ON_COMPANY FOREIGN KEY (company_id) REFERENCES biz_company (id);

ALTER TABLE join_company_member
    ADD CONSTRAINT FK_JOIN_COMPANY_MEMBER_ON_USER FOREIGN KEY (user_id) REFERENCES biz_users (id);

ALTER TABLE join_role_permission
    ADD CONSTRAINT FK_JOIN_ROLE_PERMISSION_ON_PERMISSION FOREIGN KEY (permission_id) REFERENCES biz_permission (id);

ALTER TABLE join_role_permission
    ADD CONSTRAINT FK_JOIN_ROLE_PERMISSION_ON_ROLE FOREIGN KEY (role_id) REFERENCES biz_role (id);

ALTER TABLE join_user_company_role
    ADD CONSTRAINT FK_JOIN_USER_COMPANY_ROLE_ON_COMPANY_MEMBER FOREIGN KEY (company_member_id) REFERENCES join_company_member (id);

CREATE INDEX idx_user_role_member ON join_user_company_role (company_member_id);

ALTER TABLE join_user_company_role
    ADD CONSTRAINT FK_JOIN_USER_COMPANY_ROLE_ON_ROLE FOREIGN KEY (role_id) REFERENCES biz_role (id);

CREATE INDEX idx_user_role_role ON join_user_company_role (role_id);