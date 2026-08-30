-- =============================================
-- Real Company Data: AIR KING EQUIPMENT (Indore) & AIR KING (Bhavnagar)
-- =============================================

-- Update dummy "Acme Corp" to be AIR KING EQUIPMENT (Indore)
UPDATE company SET
    name = 'AIR KING EQUIPMENT',
    address = '1293 Station Road Rau, Indore M.P. 453331, INDIA',
    gstin = '23ARFPB4246F1ZC',
    state = 'Madhya Pradesh',
    phone = '9826313663',
    pan = 'ARFPB4246F',
    bank_name = 'Canara Bank',
    bank_account = '6056257000016',
    bank_ifsc = 'CNRB0006056',
    bank_branch = 'Rau-453331, Indore MP',
    invoice_number_format = '{SEQ}/{YYYY}-{YY+1}',
    template_name = 'default'
WHERE id = '11111111-1111-1111-1111-111111111111';

-- Insert AIR KING (Bhavnagar) as second company
INSERT INTO company (id, name, address, gstin, state, phone, pan, bank_name, bank_account, bank_ifsc, bank_branch, invoice_number_format, template_name)
VALUES (
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    'AIR KING',
    '38, Panchnath Nagar, Nari Chowkdi, Bhavnagar Gujarat 364004, INDIA',
    '24BCPPB8943P1ZX',
    'Gujarat',
    '9826313663',
    'BCPPB8943P',
    NULL, NULL, NULL, NULL,
    '{SEQ}/{YYYY}-{YY+1}',
    'default'
);

-- =============================================
-- Admin user for Bhavnagar company (password: 'password')
-- =============================================
INSERT INTO app_user (id, tenant_id, username, password_hash, role)
VALUES (
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    'admin',
    '$2a$10$uSN0P4syL7zu8H3.J2XeR.L0uTKEKoCQ5iT.txdtL64S7Zdt/HF2K',
    'ROLE_USER'
);

-- =============================================
-- Real Clients
-- =============================================

-- Client for Bhavnagar company: BIOPAC VENTURES
INSERT INTO client (id, tenant_id, name, billing_address, shipping_address, gstin, state)
VALUES (
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    'BIOPAC VENTURES PVT LTD',
    'Plot No. 18, Meghpar Borichi, Survey No. 27, Anjar-370110',
    'BIOPAC VENTURES PRIVATE LIMITED, Meghpar Borichi, Near Sai Weighbridge, Near RTO Office, Anjar-370110',
    '24AAJCB8370J1ZE',
    'Gujarat'
);

-- Client for Indore company: AIR KING Bhavnagar (inter-company)
UPDATE client SET
    name = 'AIR KING (Bhavnagar)',
    billing_address = '38, Panchnath Nagar, Nari Chowkdi, Bhavnagar Gujarat 364004',
    gstin = '24BCPPB8943P1ZX',
    state = 'Gujarat'
WHERE id = '33333333-3333-3333-3333-333333333333';

-- =============================================
-- Real Products (Air Compressor & Oxygen Plant Spares)
-- =============================================

-- Update dummy product to a real one for Indore
UPDATE product SET
    name = 'Piston C.I DIA 101.1 x 166mm L',
    hsn_code = '8414',
    unit = 'NOS',
    default_sale_rate = 7000.00
WHERE id = '44444444-4444-4444-4444-444444444444';

-- More products for Indore (Air Compressor parts, HSN 8414)
INSERT INTO product (id, tenant_id, name, hsn_code, unit, default_sale_rate) VALUES
('44444444-4444-4444-4444-444444444401', '11111111-1111-1111-1111-111111111111', 'Piston Ring 2-3/8"', '8414', 'NOS', 150.00),
('44444444-4444-4444-4444-444444444402', '11111111-1111-1111-1111-111111111111', 'Angel Check Valve', '8414', 'NOS', 600.00),
('44444444-4444-4444-4444-444444444403', '11111111-1111-1111-1111-111111111111', 'Tune Up Kit 2475', '8414', 'NOS', 5000.00),
('44444444-4444-4444-4444-444444444404', '11111111-1111-1111-1111-111111111111', 'K.G Khosla Repair Kit', '8414', 'NOS', 2500.00),
('44444444-4444-4444-4444-444444444405', '11111111-1111-1111-1111-111111111111', 'Tune Up Kit Elgi', '8414', 'NOS', 4500.00),
('44444444-4444-4444-4444-444444444406', '11111111-1111-1111-1111-111111111111', 'Air Compressor Oil Filter', '8414', 'NOS', 400.00),
('44444444-4444-4444-4444-444444444407', '11111111-1111-1111-1111-111111111111', 'Air Comp Discharge Valve 1st Stage', '8414', 'NOS', 9000.00),
('44444444-4444-4444-4444-444444444408', '11111111-1111-1111-1111-111111111111', 'Air Comp Lubrication Coupling Complete', '8414', 'NOS', 680.00);

-- Products for Bhavnagar (Oxygen Plant Spares, HSN 84196000)
INSERT INTO product (id, tenant_id, name, hsn_code, unit, default_sale_rate) VALUES
('55555555-5555-5555-5555-555555555501', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Expansion Engine Push Pump', '84196000', 'NOS', 22500.00),
('55555555-5555-5555-5555-555555555502', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Expansion Engine Roller', '84196000', 'NOS', 1650.00),
('55555555-5555-5555-5555-555555555503', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Air Compressor Oil Filter', '84196000', 'NOS', 400.00),
('55555555-5555-5555-5555-555555555504', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Manifold Valve Oxygen Line', '84196000', 'NOS', 16500.00),
('55555555-5555-5555-5555-555555555505', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Expansion Engine Piston Ring 85mm Set', '84196000', 'SET', 5000.00),
('55555555-5555-5555-5555-555555555506', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Expansion Engine Piston Ring 100mm Set', '84196000', 'SET', 7000.00),
('55555555-5555-5555-5555-555555555507', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Expansion Engine Gland Nut', '84196000', 'NOS', 1250.00),
('55555555-5555-5555-5555-555555555508', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Pedestal Bearing Shaft', '84196000', 'NOS', 27000.00),
('55555555-5555-5555-5555-555555555509', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'N2 Pump Small NRV Brass Nut & Nipple', '84196000', 'NOS', 1650.00),
('55555555-5555-5555-5555-555555555510', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Sub Zero Digital Process Controller SZ756', '84196000', 'NOS', 900.00);
