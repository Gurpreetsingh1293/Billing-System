# Product Requirements Document (PRD)

**Invoice Management SaaS for Spare Parts Trading Businesses**

| | |
|---|---|
| **Document Version** | 1.0 |
| **Date** | 27 August 2026 |
| **Status** | Draft — for stakeholder review |
| **Owner** | [Product Owner Name] |

---

## 1. Overview

### 1.1 Summary

A multi-tenant SaaS product that replaces a manual, Word-document-based invoicing process with a fast, structured, mobile-accessible system. Built initially for one spare parts trading business (air compressor and oxygen plant components) and designed to onboard 3–4 independent companies from day one.

### 1.2 Background

The client currently builds every invoice from a blank Word template: retyping company info, manually entering every product line, and totaling on a calculator. There is no reusable record of products, no history of what was paid to manufacturers, and no way to work from a phone in the field. This is slow, error-prone, and throws away information (past rates, past products, past clients) that the business already generates every day but never captures.

### 1.3 Problem Statement

> Office staff spend significant time re-entering information that barely changes between invoices, with no memory of past products, rates, or clients — and no way to work outside the office.

---

## 2. Goals & Success Metrics

### 2.1 Goals

- Eliminate manual retyping of products and manual total calculation
- Give the business a reusable, de-duplicated record of what it sells and what it pays manufacturers
- Make invoicing and invoice lookup possible from a phone, including in the field with weak signal
- Support 3–4 separate companies from one product, each with its own branding and invoice numbering
- Stay fully GST-compliant, including e-invoicing for companies above the statutory threshold

### 2.2 Non-Goals

- Inventory / stock quantity management (not needed — simple resale business model)
- Payment collection / payment gateway integration
- Full accounting or bookkeeping ledger

### 2.3 Success Metrics

| Metric | Target |
|---|---|
| Time to create and issue a typical 5–10 line invoice | Under 2 minutes, down from an estimated 10–15 minutes manually |
| Manual calculation errors on issued invoices | Zero (fully system-calculated) |
| Duplicate product/client entries in catalog | Near zero, caught at entry time |
| Invoices generated on a mobile device | Majority of history lookups and a meaningful share of creation |
| Companies successfully onboarded with isolated templates | 3–4 at launch |
| E-invoicing (IRN) success rate for applicable tenants | >99% of issued invoices receive a valid IRN on first attempt or automatic retry |

---

## 3. Users & Personas

### 3.1 Owner / Admin
Runs the company, cares about margin (sale rate vs. manufacturer rate), needs to trust that issued invoices are correct and compliant, and wants visibility across the business without doing data entry personally.

### 3.2 Office Staff
Creates invoices day-to-day, deals directly with clients and manufacturers, wants the fastest possible path from "client wants these parts" to "PDF invoice sent," often working from a phone or a shared desktop.

### 3.3 Platform Operator (you)
Onboards new companies, configures per-tenant templates and e-invoicing, and needs the product to scale to more companies over time without custom code per client.

---

## 4. User Stories

Organized by theme, each phrased as a need rather than a system behavior — the SRS maps each of these to formal requirements.

### Product Catalog
- As office staff, I want to search for a spare part by name or HSN code so I don't retype product details every invoice.
- As office staff, I want to be warned if I'm about to add a product that already exists, so the catalog doesn't fill up with duplicates.
- As an owner, I want product rates to update going forward without changing invoices I've already issued.

### Manufacturer Rates
- As an owner, I want a history of what we paid each manufacturer for each part, so I can track cost trends over time.
- As an owner, I want to see my margin on an invoice at a glance, since I'm already tracking both sides of the price.
- As an owner, I want to be alerted if a newly entered purchase rate looks unusually high or low compared to recent history, in case it's a data entry mistake.

### Clients
- As office staff, I want to search for an existing client instead of retyping their address every time.
- As office staff, I want to see what a client ordered recently, so repeat orders are fast to build.

### Invoice Creation
- As office staff, I want to build an invoice by picking products and quantities, with totals and tax calculated automatically.
- As office staff, I want the correct GST split (CGST/SGST vs IGST) applied automatically based on where the client is.
- As office staff, I want to save a draft and come back to it before finalizing.
- As an owner, I want issued invoices to be locked from editing, with corrections handled through a proper credit note, so our records stay trustworthy.

### E-Invoicing & Compliance
- As an owner of a company above the e-invoicing threshold, I want the IRN and QR code generated and included automatically, so I don't have to do it manually elsewhere.
- As office staff, I want to know clearly if e-invoice submission failed and needs retrying, rather than silently missing it.

### Shipping Labels
- As office staff, I want to print a parcel label with the client's name and address without retyping it, ideally from the same screen as the invoice.

### Mobile & Connectivity
- As office staff working at a remote site, I want to keep working on an invoice even with weak signal, and have it submit automatically once I'm back online.
- As an owner, I want to look up any past invoice from my phone, without needing to be at the office computer.

### Multi-Company
- As the platform operator, I want each company's invoices to look distinct (their own logo, layout, numbering) even though they run on the same product.
- As the platform operator, I want to onboard a new company without writing custom code for them.

---

## 5. Scope by Phase

### Phase 1 — MVP (launch blocking)
The full replacement of the Word-based workflow: multi-tenant setup, product catalog with dedup, manufacturer rate history, invoice builder with auto GST calculation, sequential invoice numbering, invoice + shipping label PDFs, per-tenant e-invoicing, mobile-accessible invoice history, and weak-connectivity support.

### Phase 2 — Fast Follow
Client autocomplete and reorder suggestions, margin visibility, credit note workflow, role-based access restricting rate/margin visibility for staff.

### Phase 3 — AI-Assisted Differentiators
Smarter duplicate detection, OCR-based bulk import of historical invoices, rate anomaly detection, natural-language search across invoice history.

*(Full requirement-level detail for each phase lives in the companion SRS document.)*

---

## 6. Key Product Decisions & Rationale

| Decision | Rationale |
|---|---|
| No inventory/stock tracking | Business model is simple resale; stock tracking would add complexity with no user asking for it |
| Invoices immutable once issued; corrections via credit note | Matches GST compliance expectations and protects trust in historical records |
| Per-tenant e-invoicing toggle rather than global | Only some companies cross the turnover threshold; forcing it on all tenants adds unnecessary cost/complexity |
| PWA with caching, not full offline-first sync | Real need is "works on weak signal," not "works with zero signal for hours" — this keeps the build simpler without under-serving the actual use case |
| Shipping label as a separate document from the invoice | Dispatch and billing don't always happen at the same moment; label only needs name + address, not GST detail |
| Product/client dedup at entry time (fuzzy + exact match) | Directly addresses a named pain point — catalog quality degrades fast without this |

---

## 7. Risks & Open Questions

| Risk / Question | Why it matters |
|---|---|
| E-invoicing threshold is government-set and can change | Per-tenant e-invoicing flag must be easy to toggle without a code change |
| Which companies are above the threshold today, and which GSP to integrate with | Blocks Phase 1 scoping for the e-invoicing feature |
| Should all 3–4 companies share one invoice number format, or does each need its own series/prefix? | Affects tenant configuration design |
| Should office staff ever see manufacturer rates/margin? | Affects role-based access design (Phase 2) |
| Is there historical invoice data worth importing at launch? | Affects whether OCR bulk-import (Phase 3) should be pulled earlier |
| Exact shipping label size/format currently used | Needed to finalize the label PDF template |
| Who bears the GSP (e-invoicing) API cost — client or built into SaaS pricing? | Business/pricing decision, not just technical |

---

## 8. Out of Scope (Explicitly)

- Inventory or stock-level tracking
- Payment processing or collection
- Full double-entry accounting or ledger reconciliation
- Support for non-GST (international) invoicing formats, unless a future company requires it

---

*This PRD defines the product's purpose, users, and priorities. See the companion Software Requirements Specification (SRS) for detailed, testable functional and non-functional requirements derived from this document.*
