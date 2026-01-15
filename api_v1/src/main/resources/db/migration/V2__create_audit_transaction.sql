CREATE TABLE audit_transaction (
                                   id RAW(16) NOT NULL,
                                   transaction_id RAW(16) NOT NULL,
                                   performed_by RAW(16) NOT NULL,
                                   type VARCHAR2(50) NOT NULL,
                                   audit_date TIMESTAMP NOT NULL,
                                   previous_value VARCHAR2(255) NOT NULL,
                                   next_value VARCHAR2(255) NOT NULL,
                                   CONSTRAINT pk_audit_transaction PRIMARY KEY (id),
                                   CONSTRAINT fk_audit_transaction_tx
                                       FOREIGN KEY (transaction_id)
                                           REFERENCES transaction (id),
                                   CONSTRAINT fk_audit_transaction_user
                                       FOREIGN KEY (performed_by)
                                           REFERENCES app_user (id)
);
