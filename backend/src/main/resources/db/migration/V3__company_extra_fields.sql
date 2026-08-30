-- Add business detail columns to company table
ALTER TABLE company ADD COLUMN phone VARCHAR(20);
ALTER TABLE company ADD COLUMN pan VARCHAR(15);
ALTER TABLE company ADD COLUMN bank_name VARCHAR(100);
ALTER TABLE company ADD COLUMN bank_account VARCHAR(50);
ALTER TABLE company ADD COLUMN bank_ifsc VARCHAR(20);
ALTER TABLE company ADD COLUMN bank_branch VARCHAR(100);
ALTER TABLE company ADD COLUMN template_name VARCHAR(50) DEFAULT 'default';
