# Software Requirements Specification

**Multi-Tenant Invoice Management System for Spare Parts Trading Businesses**

| | |
|---|---|
| **Document Version** | 1.0 |
| **Date** | 27 August 2026 |
| **Status** | Draft — for client review |
| **Standard Reference** | Structured per IEEE 830 conventions |

---

## 1. Introduction

### 1.1 Purpose

This document specifies the functional and non-functional requirements for a multi-tenant, cloud-based Invoice Management SaaS. The system replaces a manual Microsoft Word based invoicing process used by a spare parts trading business (air compressor and oxygen plant components) and is designed to be deployed for 3–4 independent client companies from a single codebase.

### 1.2 Scope

The system shall allow authorized users to maintain a master catalog of products (with HSN codes), maintain a history of manufacturer purchase rates, maintain a client (buyer) directory, and generate GST-compliant invoices and shipping labels as downloadable/printable PDF documents. The system shall support multiple independently branded company tenants, each with its own invoice numbering sequence and template. The system shall be accessible via a mobile-friendly web interface (PWA) usable under weak network conditions.

Out of scope for this specification: inventory/stock management (explicitly not required by the business model), payment gateway integration, and accounting/ledger reconciliation beyond invoice and rate records.

### 1.3 Intended Audience

- Development team — as the basis for design and implementation
- Client stakeholders — to validate that requirements match business needs before build
- QA — as the basis for test case derivation

### 1.4 Definitions, Acronyms, and Abbreviations

| Term | Definition |
|---|---|
| HSN Code | Harmonized System of Nomenclature code; classifies goods for GST purposes |
| GST | Goods and Services Tax (India) |
| CGST / SGST | Central/State GST, applied on intra-state (same-state) sales |
| IGST | Integrated GST, applied on inter-state sales |
| GSTIN | GST Identification Number, unique per registered business |
| IRN | Invoice Reference Number, issued by the government for e-invoicing |
| GSP | GST Suvidha Provider — authorized third-party API for e-invoice/IRN generation |
| Tenant | One of the independent companies using the platform |
| PWA | Progressive Web App — installable web app with offline-caching capability |
| SRS | Software Requirements Specification (this document) |

### 1.5 References

- Scope & Feasibility Document — Invoice Management SaaS (companion document, v1)
- GST e-invoicing rules — as published by the Goods and Services Tax Network (GSTN); applicable turnover threshold to be reconfirmed at build time as it is subject to government revision

---

## 2. Overall Description

### 2.1 Product Perspective

This is a new, standalone, cloud-hosted product. It is not an extension of an existing system. It replaces an entirely manual, offline (Word + calculator) process. The system is multi-tenant: a single deployment serves 3–4 companies, each with logically isolated data.

### 2.2 User Classes and Characteristics

| User Class | Characteristics |
|---|---|
| Owner / Admin | Full access per tenant: manages products, rates, users, templates, and views margin/cost data. Typically one or two per company. |
| Office Staff | Creates and issues invoices, manages clients. May be restricted from viewing manufacturer rates/margin depending on configuration. |
| Platform Super-Admin | You / the SaaS operator. Manages tenant onboarding, template configuration, and cross-tenant system settings. Not part of any single company's data view. |

### 2.3 Operating Environment

- Client-side: modern mobile and desktop browsers (Chrome, Safari, Edge — last 2 major versions); installable as a PWA on Android/iOS/desktop
- Server-side: cloud-hosted backend and database (specific stack to be finalized at design stage)
- Network: designed for intermittent/weak signal; not designed for extended zero-connectivity operation

### 2.4 Design and Implementation Constraints

- Invoice numbering must be strictly sequential per company per financial year, with no gaps — a legal/audit constraint, not just a UX preference
- Once an invoice is marked Issued, its line items and totals become immutable; corrections must occur via a Credit Note
- E-invoicing (IRN generation) must be configurable per tenant, since only companies above the statutory turnover threshold require it
- All monetary and tax calculations must be performed server-side and treated as the source of truth, even when initiated from a cached/offline client state

### 2.5 Assumptions and Dependencies

- All tenant companies are GST-registered in India
- At least one tenant company is above the e-invoicing turnover threshold at time of writing; this must be reconfirmed per company before go-live, and the threshold itself is subject to government revision
- A GST Suvidha Provider (GSP) account will be available for IRN generation for applicable tenants
- The business does not require inventory/stock quantity tracking

---

## 3. Functional Requirements

Each requirement is uniquely identified. Priority: **Must** (MVP/launch-blocking), **Should** (fast-follow), **Could** (future enhancement). This mirrors the phased scope agreed with the client.

### 3.1 Tenant & Company Management

| ID | Requirement | Priority |
|---|---|---|
| FR-1.1 | The system shall allow a Super-Admin to create a new tenant (company) with name, address, GSTIN, logo, and default invoice template. | Must |
| FR-1.2 | The system shall isolate all data (products, clients, invoices, rates, users) per tenant such that no tenant can view another tenant's data. | Must |
| FR-1.3 | The system shall allow each tenant to configure its own invoice numbering prefix/format and reset rule per financial year. | Must |
| FR-1.4 | The system shall allow each tenant to enable or disable e-invoicing (IRN generation) independently of other tenants. | Must |
| FR-1.5 | The system shall allow each tenant to upload/select its own invoice PDF template (logo placement, footer terms, color accents). | Must |

### 3.2 User Management & Access Control

| ID | Requirement | Priority |
|---|---|---|
| FR-2.1 | The system shall support at least two roles per tenant: Owner/Admin and Office Staff. | Must |
| FR-2.2 | The system shall allow an Admin to restrict Office Staff visibility of manufacturer purchase rates and computed margin. | Should |
| FR-2.3 | The system shall require authentication (username/password or equivalent) for all users before accessing any tenant data. | Must |
| FR-2.4 | The system shall log which user created or issued each invoice, for audit purposes. | Should |

### 3.3 Product (Spare Parts) Catalog

| ID | Requirement | Priority |
|---|---|---|
| FR-3.1 | The system shall allow authorized users to add a product with name, HSN code, unit of measure, and default sale rate. | Must |
| FR-3.2 | The system shall check for an exact HSN code match when a new product is added and warn the user if one exists. | Must |
| FR-3.3 | The system shall check for a fuzzy name match (e.g. minor spelling/formatting differences) when a new product is added and warn the user of likely duplicates before saving. | Must |
| FR-3.4 | The system shall allow searching/filtering the product catalog by name or HSN code with autocomplete suggestions during invoice entry. | Must |
| FR-3.5 | The system shall allow editing a product's default sale rate without altering historical invoices already issued using the previous rate. | Must |
| FR-3.6 | The system shall support bulk import of products (e.g. via spreadsheet or OCR-assisted extraction from historical invoices). | Could |

### 3.4 Manufacturer Purchase Rate History

| ID | Requirement | Priority |
|---|---|---|
| FR-4.1 | The system shall allow recording a manufacturer purchase rate for a product, tagged with manufacturer name and date. | Must |
| FR-4.2 | The system shall retain a full timestamped history of purchase rates per product per manufacturer (append-only, not overwritten). | Must |
| FR-4.3 | The system shall display the most recent purchase rate alongside the current sale rate when building an invoice, for margin awareness. | Should |
| FR-4.4 | The system shall flag a newly entered purchase rate that deviates significantly (configurable threshold) from the average of the last N purchases of that product. | Could |

### 3.5 Client (Buyer) Directory

| ID | Requirement | Priority |
|---|---|---|
| FR-5.1 | The system shall allow authorized users to add a client with name, billing address, shipping address, GSTIN (if applicable), and state. | Must |
| FR-5.2 | The system shall check for duplicate clients (by name and/or GSTIN) when a new client is added and warn the user. | Should |
| FR-5.3 | The system shall allow searching/selecting an existing client via autocomplete during invoice creation. | Must |
| FR-5.4 | The system shall suggest recently invoiced products for a selected client, based on that client's invoice history. | Should |

### 3.6 Invoice Creation & Management

| ID | Requirement | Priority |
|---|---|---|
| FR-6.1 | The system shall allow a user to create a new invoice by selecting a client and adding one or more line items from the product catalog. | Must |
| FR-6.2 | The system shall auto-calculate line-item totals (quantity × rate) and the invoice grand total without manual arithmetic. | Must |
| FR-6.3 | The system shall automatically determine and apply CGST+SGST (intra-state) or IGST (inter-state) based on the tenant's registered state versus the client's state. | Must |
| FR-6.4 | The system shall assign the next sequential invoice number for the tenant only at the point of issuance, not at draft creation. | Must |
| FR-6.5 | The system shall support a Draft state in which an invoice can be freely edited or discarded prior to issuance. | Must |
| FR-6.6 | The system shall prevent any edits to line items, quantities, rates, or totals once an invoice is marked Issued. | Must |
| FR-6.7 | The system shall generate an A4 PDF of the issued invoice including tenant letterhead, GSTIN, client details, HSN codes, tax breakdown, and totals. | Must |
| FR-6.8 | For tenants with e-invoicing enabled, the system shall submit the invoice to the configured GSP upon issuance, retrieve the IRN and QR code, and embed them in the invoice PDF before the invoice is considered finalized. | Must |
| FR-6.9 | The system shall handle GSP API failure gracefully, placing the invoice in a Pending-IRN state with retry capability rather than silently failing. | Must |
| FR-6.10 | The system shall allow marking an issued invoice as Paid, independent of PDF generation. | Should |

### 3.7 Credit Notes

| ID | Requirement | Priority |
|---|---|---|
| CR-1 | The system shall allow an authorized user to raise a Credit Note against an issued invoice, referencing the original invoice number. | Should |
| CR-2 | The system shall generate a separate sequentially numbered Credit Note PDF, distinct from the invoice numbering sequence. | Should |

### 3.8 Shipping Label / Sticker

| ID | Requirement | Priority |
|---|---|---|
| FR-7.1 | The system shall generate a shipping label PDF containing the client's name and shipping address, in a small print format distinct from the A4 invoice layout. | Must |
| FR-7.2 | The system shall allow generating the shipping label independently of invoice issuance (e.g. before billing is finalized). | Should |
| FR-7.3 | The system shall source shipping label data from the same Client record used for invoicing, to avoid re-entry. | Must |

### 3.9 Invoice History & Retrieval

| ID | Requirement | Priority |
|---|---|---|
| FR-8.1 | The system shall provide a searchable, filterable list of all issued invoices (by client, date range, invoice number, status). | Must |
| FR-8.2 | The system shall allow re-downloading the PDF of any previously issued invoice from a mobile device. | Must |
| FR-8.3 | The system shall support natural-language or keyword search across invoice history (e.g. by client name across a date range). | Could |

### 3.10 Connectivity & Mobile Access

| ID | Requirement | Priority |
|---|---|---|
| FR-9.1 | The system shall be usable as an installable Progressive Web App on mobile devices. | Must |
| FR-9.2 | The system shall cache the product catalog, client directory, and recent rate history locally for use under weak/intermittent connectivity. | Must |
| FR-9.3 | The system shall persist an in-progress invoice draft locally if connectivity is lost mid-entry, without data loss. | Must |
| FR-9.4 | The system shall queue an invoice submission made while offline and automatically retry when connectivity is restored, notifying the user of pending status. | Must |

---

## 4. Non-Functional Requirements

### 4.1 Performance

- Invoice PDF generation shall complete within 3 seconds under normal network conditions.
- Product/client autocomplete search shall return results within 500ms for catalogs of up to 5,000 items.

### 4.2 Reliability & Data Integrity

- Invoice numbering logic shall guarantee no duplicate or skipped numbers under concurrent submissions from multiple users of the same tenant.
- All tax and total calculations shall be re-validated server-side regardless of client-side computation, to prevent tampering or client bugs from producing an invalid invoice.

### 4.3 Security

- Tenant data isolation shall be enforced at the data-access layer, not only in the UI.
- Manufacturer rate and margin data shall be restricted per the role-based access rules defined in FR-2.2.
- All data in transit shall be encrypted (HTTPS/TLS).

### 4.4 Usability

- Invoice creation for a typical 5–10 line item invoice shall require no more manual typing than selecting products, entering quantity, and confirming — no manual total calculation by the user.
- The interface shall be usable on a mobile screen (minimum ~360px width) without horizontal scrolling for core workflows.

### 4.5 Compliance

- Generated invoices shall comply with applicable GST invoicing rules at time of build, including mandatory fields (GSTIN, HSN, place of supply, tax split).
- E-invoicing behavior shall comply with current GSTN e-invoicing regulations for tenants above the applicable turnover threshold; threshold and rules to be reconfirmed at implementation time as they are subject to change.

### 4.6 Maintainability & Extensibility

- The invoice template system shall be data-driven (configurable per tenant) rather than hard-coded, to support onboarding additional companies beyond the initial 3–4 without code changes.
- Phase 3 (AI-assisted) features shall be designed as additive services that do not require restructuring the core data model.

---

## 5. Data Requirements (Summary)

Full entity-relationship design is a design-phase deliverable; the table below summarizes the core entities this SRS assumes.

| Entity | Key Attributes |
|---|---|
| Company (Tenant) | name, address, GSTIN, state, logo, invoice number format, e-invoicing flag |
| User | name, credentials, role, tenant reference |
| Product | name, HSN code, unit, default sale rate, tenant reference |
| Manufacturer Rate Record | product reference, manufacturer name, rate, date recorded |
| Client | name, billing address, shipping address, GSTIN, state, tenant reference |
| Invoice | number, tenant reference, client reference, line items, tax split, status, IRN (if applicable), timestamps |
| Invoice Line Item | invoice reference, product reference, quantity, rate, line total |
| Credit Note | reference to original invoice, reason, amount, number |

---

## 6. Acceptance Criteria (MVP)

The system shall be considered ready for MVP client sign-off when the following are demonstrably true:

- A user can generate a fully GST-compliant, correctly tax-split invoice PDF without any manual calculation.
- A user can generate a shipping label PDF for the same client without re-entering address details.
- Product and client entry actively warns on likely duplicates.
- An issued invoice cannot be silently edited; corrections require a credit note.
- At least one tenant above the e-invoicing threshold successfully generates a live IRN via the configured GSP.
- The system remains usable — without data loss — when tested under simulated weak/intermittent connectivity.
- Each of the 3–4 companies can operate with fully isolated data and its own invoice template/numbering.

---

*This SRS should be reviewed and formally signed off by the client stakeholder before implementation begins. Any requirement change after sign-off should be tracked as a versioned amendment to this document.*
