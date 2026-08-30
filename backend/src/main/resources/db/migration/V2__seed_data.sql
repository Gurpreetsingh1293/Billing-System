INSERT INTO company (id, name, state, invoice_number_format) 
VALUES ('11111111-1111-1111-1111-111111111111', 'Acme Corp', 'Maharashtra', 'INV-{YYYY}-{SEQ}');

-- password is 'password' encoded in BCrypt
INSERT INTO app_user (id, tenant_id, username, password_hash, role)
VALUES ('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 'admin', '$2a$10$uSN0P4syL7zu8H3.J2XeR.L0uTKEKoCQ5iT.txdtL64S7Zdt/HF2K', 'ROLE_USER');

INSERT INTO client (id, tenant_id, name, billing_address, gstin, state)
VALUES ('33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111', 'Test Client', '123 Main St', '27ABCDE1234F1Z5', 'Maharashtra');

INSERT INTO product (id, tenant_id, name, hsn_code, unit, default_sale_rate)
VALUES ('44444444-4444-4444-4444-444444444444', '11111111-1111-1111-1111-111111111111', 'Software License', '9987', 'NOS', 1500.00);
