CREATE TABLE company (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    gstin VARCHAR(15),
    state VARCHAR(50),
    logo_url VARCHAR(512),
    invoice_number_format VARCHAR(50) DEFAULT 'INV-{YYYY}-{SEQ}',
    e_invoicing_enabled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE app_user (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES company(id),
    username VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, username)
);

CREATE TABLE product (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES company(id),
    name VARCHAR(255) NOT NULL,
    hsn_code VARCHAR(50),
    unit VARCHAR(20),
    default_sale_rate DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE manufacturer_rate (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES company(id),
    product_id UUID NOT NULL REFERENCES product(id),
    manufacturer_name VARCHAR(255) NOT NULL,
    rate DECIMAL(15, 2) NOT NULL,
    date_recorded TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE client (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES company(id),
    name VARCHAR(255) NOT NULL,
    billing_address TEXT,
    shipping_address TEXT,
    gstin VARCHAR(15),
    state VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE invoice (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES company(id),
    client_id UUID NOT NULL REFERENCES client(id),
    invoice_number VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT', -- DRAFT, ISSUED, PAID, PENDING_IRN
    total_amount DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    cgst_amount DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    sgst_amount DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    igst_amount DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    issued_at TIMESTAMP WITH TIME ZONE,
    irn VARCHAR(64),
    qr_code TEXT,
    UNIQUE(tenant_id, invoice_number)
);

CREATE TABLE invoice_line_item (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES company(id),
    invoice_id UUID NOT NULL REFERENCES invoice(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES product(id),
    quantity DECIMAL(15, 2) NOT NULL,
    rate DECIMAL(15, 2) NOT NULL,
    line_total DECIMAL(15, 2) NOT NULL
);

CREATE TABLE credit_note (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES company(id),
    original_invoice_id UUID NOT NULL REFERENCES invoice(id),
    credit_note_number VARCHAR(100) NOT NULL,
    reason TEXT,
    amount DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, credit_note_number)
);

CREATE TABLE invoice_sequence (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES company(id),
    financial_year VARCHAR(10) NOT NULL,
    current_value BIGINT NOT NULL DEFAULT 0,
    UNIQUE(tenant_id, financial_year)
);

-- Indexes for frequent queries and tenant isolation
CREATE INDEX idx_user_tenant ON app_user(tenant_id);
CREATE INDEX idx_product_tenant ON product(tenant_id);
CREATE INDEX idx_client_tenant ON client(tenant_id);
CREATE INDEX idx_invoice_tenant ON invoice(tenant_id);
CREATE INDEX idx_line_item_tenant ON invoice_line_item(tenant_id);
CREATE INDEX idx_credit_note_tenant ON credit_note(tenant_id);
