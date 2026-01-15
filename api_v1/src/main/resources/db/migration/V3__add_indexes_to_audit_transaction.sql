CREATE INDEX idx_audit_transaction_tx
    ON audit_transaction (transaction_id);

CREATE INDEX idx_audit_transaction_user
    ON audit_transaction (performed_by);
