# Tech Stack Document

**Invoice Management SaaS for Spare Parts Trading Businesses**

| | |
|---|---|
| **Document Version** | 1.0 |
| **Date** | 29 August 2026 |
| **Status** | Draft — for review |
| **Context** | Solo developer, self-managed infra, Java/Spring Boot background |

---

## 1. Guiding Constraints

The stack is chosen to fit the actual requirements from the PRD/SRS, not general preference:

- **Relational, auditable financial data** (invoices, credit notes, sequential numbering, tax splits) → needs a real relational database with strong consistency, not a document store.
- **Server-authoritative business logic** (tax calculation, invoice numbering must never trust the client) → needs a proper backend owning that logic, not a thin BaaS layer.
- **PWA with caching + submit-queue for weak signal** → needs a frontend stack with mature PWA tooling.
- **Two distinct, per-tenant-branded PDF outputs** (invoice + shipping label) → needs solid server-side PDF/templating support.
- **Solo developer, 3–4 tenants, not hyperscale** → argues against microservices, Kubernetes, or multi-region complexity; favors a single well-structured monolith that one person can build, run, and debug.
- **Developer is comfortable managing own infrastructure and already knows Java/Spring Boot** → the stack should build on that strength rather than introduce a second unfamiliar ecosystem.

---

## 2. Stack Summary

| Layer | Choice |
|---|---|
| Backend | Java 25 LTS, Spring Boot 3.x (Web, Data JPA, Security) |
| Database | PostgreSQL + Flyway migrations |
| Multi-tenancy | Shared schema, `tenant_id` discriminator + Hibernate filter |
| PDF Generation | Thymeleaf templates → OpenHTMLtoPDF |
| Frontend | React + Vite, PWA plugin, TanStack Query |
| Auth | Spring Security + JWT |
| E-Invoicing | REST client (Spring `RestClient`/`WebClient`) to chosen GSP |
| Infra | Docker Compose on a single VPS, Nginx, scheduled PostgreSQL backups |

---

## 3. Backend

### 3.1 Core Framework — Java 25 LTS+ Spring Boot 3.x
- **Spring Web** — REST API layer for all client-facing operations.
- **Spring Data JPA + Hibernate** — ORM for core entities: Company, User, Product, Client, Invoice, Invoice Line Item, Credit Note, Manufacturer Rate Record.
- **Spring Security** — authentication and role-based access control (Owner/Admin vs. Office Staff, per FR-2.x).
- **Flyway** — versioned, repeatable database migrations; important given the product evolves across three planned phases.

### 3.2 Multi-Tenancy Approach
- **Shared schema with a `tenant_id` column** on every tenant-scoped table, enforced via a Hibernate filter or a request-scoped interceptor that automatically scopes all queries.
- Chosen over schema-per-tenant or database-per-tenant: those add migration and operational overhead disproportionate to a 3–4 tenant scale, and are harder for a solo developer to operate safely.
- Optional hardening: PostgreSQL row-level security as a second enforcement layer beneath the application-level filter, so a bug in application code cannot leak cross-tenant data.

### 3.3 Business Logic Placement
- Tax calculation (CGST/SGST vs IGST), invoice numbering, and duplicate-detection logic live entirely server-side, never trusting client-submitted totals — this is a hard constraint from the SRS (FR-6.2, FR-6.3, FR-6.4).
- E-invoicing/GSP integration isolated behind a `GSPClient` interface, so the provider can be swapped or mocked in tests without touching core invoice logic.

---

## 4. Database — PostgreSQL

- Strong relational integrity for financial records; native transactional guarantees needed for invoice numbering correctness under concurrent use.
- Free, mature, and straightforward to self-host via Docker.
- Works natively with Spring Data JPA.
- Supports row-level security if stronger tenant isolation is desired later.
- **Flyway-managed migrations** ensure schema changes across phases (MVP → Fast Follow → AI-assisted) are tracked and reversible.

---

## 5. PDF Generation

**Approach:** HTML/CSS templates rendered to PDF, not programmatic PDF drawing.

- **Thymeleaf** — Spring-native templating engine, used to build the invoice and shipping label as HTML templates with variables for per-tenant branding (logo, footer terms, color accents).
- **OpenHTMLtoPDF** — converts the rendered HTML/CSS to a final PDF.
- Two distinct templates maintained:
  - **Invoice (A4)** — full letterhead, GSTIN, HSN codes, tax breakdown, optional IRN/QR block.
  - **Shipping Label** — small print format, client name and address only.
- This approach was chosen over direct PDF-drawing libraries (e.g. raw iText/PDFBox) because HTML/CSS templates are significantly easier to restyle per tenant and to iterate on visually without rewriting drawing code.

---

## 6. Frontend — React + Vite (PWA)

- **React (Vite)** as a separate single-page application, consuming the Spring Boot REST API. Kept independent from the backend rather than using Thymeleaf for the app UI — Thymeleaf is reserved for PDF template rendering only.
- **`vite-plugin-pwa`** — provides service worker registration, offline asset caching, and installability with minimal configuration, directly satisfying the weak-signal requirements (FR-9.1–FR-9.4).
- **TanStack Query** — handles caching of read-heavy data (product catalog, client directory, rate history) and provides a near-built-in pattern for retrying queued writes (invoice submission) when connectivity returns.
- **Local draft persistence** — in-progress invoice drafts persisted via IndexedDB (using a lightweight wrapper such as `idb`) or `localStorage` for simpler cases, preventing data loss if connectivity drops mid-entry.

---

## 7. Authentication

- **Spring Security + JWT.**
- Appropriate because the frontend is a separate SPA calling a stateless REST API; JWT avoids server-side session management complexity.
- Simple username/password login is sufficient per current requirements — no SSO or third-party identity provider need identified.

---

## 8. E-Invoicing / GSP Integration

- Implemented as a plain REST client (Spring's `RestClient` or `WebClient`) calling the selected GST Suvidha Provider (e.g. ClearTax, Cygnet, MasterGST — all expose REST APIs).
- No dedicated framework needed; isolated behind an interface so the provider can be changed later or mocked during testing.
- Must support graceful failure handling and retry, per FR-6.9 (Pending-IRN state).

---

## 9. Infrastructure & Deployment

Given the developer manages their own infrastructure and the scale is 3–4 tenants:

- **Docker Compose** for both local development and production — runs the Spring Boot application and PostgreSQL together, keeping environment parity.
- **Single VPS** (e.g. Hetzner, DigitalOcean, AWS Lightsail) is sufficient for this workload — no need for Kubernetes or managed container orchestration at this scale.
- **Nginx** in front of the stack for TLS termination and serving the built React static assets.
- **Scheduled PostgreSQL backups** (`pg_dump` on a cron schedule) — important given the data is financial/invoice records with legal/audit relevance.

---

## 10. Explicitly Rejected Alternatives

| Alternative | Why rejected |
|---|---|
| Full offline-first architecture (local DB + sync engine) | Requirement is weak/patchy signal, not true offline operation; TanStack Query's cache/retry pattern is sufficient and far less to build and maintain solo. |
| Microservices architecture | No operational or scaling justification at 3–4 tenants; a single Spring Boot monolith is easier for one developer to build, deploy, and debug. Can be split later if genuinely needed. |
| NoSQL / document database for core data | Invoices, credit notes, and tax records are inherently relational and require strong consistency guarantees that document stores do not prioritize. |
| Schema-per-tenant or database-per-tenant multi-tenancy | Adds migration and operational complexity disproportionate to a 3–4 tenant scale; shared schema with `tenant_id` is simpler to operate solo. |
| Kubernetes / managed container platforms | Unnecessary operational overhead for this scale; a single VPS with Docker Compose is sufficient. |

---

## 11. Open Items to Confirm Before Build

- Choice of GST Suvidha Provider (GSP) for e-invoicing integration.
- Target VPS provider and region.
- Final decision on IndexedDB vs. `localStorage` for draft persistence (can be decided during frontend implementation).

---

*This document should be read alongside the PRD and SRS. It defines how the product will be built, not what it must do — see those documents for functional scope and requirements.*
