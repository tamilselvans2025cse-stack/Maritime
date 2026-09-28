# Autonomous Maritime Freight Vessel Navigation & Port Berth Allocation System

A simplified, single-module **Spring Boot 3 / Java 17 / MySQL** academic project that
simulates a maritime terminal ERP: autonomous vessel telemetry, berth allocation,
automated docking & dynamic billing, purchasing, double-entry accounting, budgeting,
and financial reporting.

---

## 1. Problem Statement

Traditional port terminals rely on manual coordination between vessel captains,
dispatchers and accountants to allocate berths, calculate docking charges and keep
the books balanced. This project simulates an **autonomous** version of that
workflow: vessels report simulated AIS/LiDAR telemetry, the system allocates berths,
automatically calculates dynamic docking + shore-power fees on departure, raises a
customer invoice, and posts the matching double-entry accounting journal — all
without manual intervention.

## 2. Objective

Build a runnable, easy-to-explain academic system that demonstrates:
- Master data management (vessels, berths, contacts, products, chart of accounts)
- A simulated autonomous docking workflow with dynamic fee calculation
- Purchase-to-pay and order-to-cash cycles
- Balanced double-entry bookkeeping
- Budgeting with variance tracking
- Balance Sheet / Profit & Loss / Budget Variance reporting

## 3. Features

- Vessel & berth master data with CRUD
- Simulated AIS/LiDAR telemetry ingestion (`POST /api/maritime/telemetry`)
- Berth reservation + automated docking + departure processing
- Dynamic docking fee (occupancy-tier multiplier) + shore-power billing
- Automatic customer invoice generation on departure
- Purchase Orders → Vendor Bills → Vendor Payments
- Sales Orders, Customer Invoices, Customer Payments
- Double-entry journal engine with a hard "debit must equal credit" guard
- Budgets with planned vs. actual variance and utilization %
- Balance Sheet, Profit & Loss, Budget Variance reports
- Clean Thymeleaf UI (navy/white maritime theme), no frontend frameworks
- Sample data auto-seeded via `CommandLineRunner`

## 4. Actors

| Actor | Responsibilities |
|---|---|
| **Admin** (Maritime Fleet Operations Director) | Configure berths, chart of accounts, analytic accounts, budgets; view reports |
| **Invoicing User** (Port Accountant) | Purchase orders, vendor bills/payments, customer invoices/payments |
| **Vessel Captain / Dispatcher** | View vessels, reserve berths, monitor docking, view invoices |
| **System** | Ingests telemetry, calculates fees, posts journal entries, updates budgets |

*(This project does not implement authentication/roles — it is a single-user
academic demo. All screens are accessible to keep the viva simple.)*

## 5. Technology Stack

Java 17 · Spring Boot 3.2 (Web, Data JPA, Validation, Thymeleaf) · MySQL ·
Lombok · Maven · HTML5 / CSS3 / vanilla JavaScript.

## 6. Architecture

Single Spring Boot application, layered:

```
controller/  -> Thymeleaf page controllers (@Controller) + REST controllers (@RestController)
service/     -> business logic (docking, billing, accounting, purchasing, sales, budgets, reports)
repository/  -> Spring Data JPA repositories (one per entity)
model/       -> JPA entities + package-private enums (Enums.java)
dto/         -> request payloads (TelemetryRequest, DockingRequest, InvoiceRequest, PaymentRequest)
exception/   -> ResourceNotFoundException + GlobalExceptionHandler
```

Each business area has **two controllers**: a `@Controller` that renders the
Thymeleaf page and handles HTML form posts, and a `@RestController` under
`/api/maritime/...` that exposes the JSON REST API described in section 30 of the
spec. Both call the same service layer, so business rules live in one place.

## 7. Database Schema (key tables)

`contacts, products, vessels, berths, analytic_accounts, accounts, journals,
journal_entries, journal_entry_lines, docking_events, sales_orders,
sales_order_lines, customer_invoices, purchase_orders, purchase_order_lines,
vendor_bills, payments, budgets, budget_lines`

Relationships use `@ManyToOne` / `@OneToMany` with `mappedBy` on the owning side
and `cascade = ALL, orphanRemoval = true` on entities that own line items
(JournalEntry→Lines, SalesOrder→Lines, PurchaseOrder→Lines, Budget→BudgetLines),
matching section 39 of the spec.

## 8. Master Data

- **Contacts**: `CUSTOMER` / `VENDOR` (e.g. *Oceanic Autonomous Shipping Ltd.*, *Maritime Sensor Technologies Pvt Ltd.*)
- **Products**: `SERVICE` / `GOODS` (5 seeded, matching spec section 7)
- **Vessels**: dimensions, cargo, telemetry fields, status lifecycle
- **Berths**: capacity limits, base hourly rate, status lifecycle
- **Chart of Accounts**: ASSET / LIABILITY / INCOME / EXPENSE, 10 seeded accounts
- **Analytic Account**: `TERM-DW4` used for budget tracking
- **Journals**: SALES, PURCHASE, BANK, CASH

## 9. Vessel Navigation Simulation

`POST /api/maritime/telemetry` accepts simulated AIS/LiDAR readings
(`latitude, longitude, speed, heading, aisSignalStrength, lidarObstacleDistance`)
and stores the latest values on the `Vessel` entity. The `/navigation` page
renders a simple `START — — — VESSEL — — — PORT` visual using plain HTML/CSS.
**This is a simulation only** — it does not control real vessels or provide real
collision avoidance.

## 10. Berth Allocation Algorithm

On `POST /api/maritime/dockings` (`DockingService.createDocking`):
1. Look up vessel and berth (404 if missing).
2. Reject if berth status is not `AVAILABLE` or `RESERVED`.
3. Reject if vessel length/draft exceed the berth's limits.
4. Mark berth `OCCUPIED`, vessel `DOCKED`, create a `DockingEvent` (`DOCKED`).

On `POST /api/maritime/dockings/{id}/departure` (`DockingService.processDeparture`):
1. Compute occupancy hours = departure − dockingStart.
2. Calculate docking fee + shore-power fee (see below).
3. Mark the event `COMPLETED`, release the berth (`AVAILABLE`), mark vessel `DEPARTED`.
4. Auto-generate a customer invoice (`SalesService.generateInvoiceFromDocking`).

## 11. Docking Fee Calculation

```
dockingFee = occupancyHours × baseDockingRatePerHour × multiplier
  0–4h   → multiplier 1.00
  4–8h   → multiplier 1.25
  >8h    → multiplier 1.50

shorePowerFee = shorePowerKwh × electricityRatePerKwh   (default ₹12/kWh, configurable)

totalFee = dockingFee + shorePowerFee
```
Example: base rate ₹5000/hr, 6 hours occupancy → `6 × 5000 × 1.25 = ₹37,500`.
Implemented in `BillingService`.

## 12. Accounting Flow (Double-Entry)

`AccountingService.postSimpleEntry(...)` creates a `JournalEntry` with two
`JournalEntryLine`s (one debit, one credit) and calls
`validateBalancedEntry()`, which **throws `IllegalStateException` if total debit
≠ total credit** — no unbalanced entry can ever be persisted.

| Event | Debit | Credit |
|---|---|---|
| Docking fee revenue | Customer Receivable (1020) | Berth Docking Revenues (4000) |
| Shore-power revenue | Customer Receivable (1020) | Shore-Power Sales (4010) |
| Vendor bill raised | Mooring Winch Maintenance (5000) | Sensor Vendor Creditors (2000) |
| Vendor payment | Sensor Vendor Creditors (2000) | Cash/Bank (1020) |

*(Simplification note for the viva: the base chart of accounts in the spec has a
single Cash/Bank account and no separate "Customer Receivable" ledger code, so
this teaching implementation reuses account 1020 as a stand-in receivable when
revenue is booked, and treats the later customer-payment step as a settlement
that does not double-book cash. A production system would add a dedicated
Accounts Receivable account.)*

## 13. Purchase Flow

`PurchaseOrder (DRAFT → APPROVED)` → `PurchaseService.createBillFromOrder` creates
a `VendorBill (OPEN)` and posts the expense/creditor journal entry → 
`PaymentService.recordVendorPayment` marks the bill `PAID` and posts the
creditor/cash journal entry.

## 14. Sales Flow

`SalesOrder` represents a berth booking. A `CustomerInvoice` is normally
**auto-generated** from a completed `DockingEvent` (docking fee + shore-power fee
as line items) — see section 10 above. `PaymentService.recordCustomerPayment`
marks the invoice `PAID`.

## 15. Budget Flow

A `Budget` belongs to an `AnalyticAccount` and has `BudgetLine`s per chart-of-account
code with `plannedAmount` / `actualAmount`. `variance = actual − planned`.
`BudgetService.recalculateVariance()` recomputes all lines for a budget.
`ReportService.budgetVarianceForAnalyticAccount()` powers
`GET /api/maritime/reports/budget/{analyticAccountId}`.

## 16. Reports

- **Balance Sheet** (`/balance-sheet`) — Assets vs Liabilities with totals.
- **Profit & Loss** (`/profit-loss`) — Income vs Expenses, Net Profit/Loss.
- **Budget Report** (`/budget-report`) — Planned, Actual, Variance, Utilization % per line.

## 17. REST API Summary

All endpoints are under `/api/maritime` (see section 30 of the original spec for
the full list): `vessels`, `berths`, `telemetry`, `dockings` (+`/departure`),
`contacts`, `products`, `accounts`, `purchase-orders` (+`/approve`),
`vendor-bills` (+`/pay`), `sales-orders`, `invoices` (+`/pay`), `budgets`,
`reports/balance-sheet`, `reports/profit-loss`, `reports/budget`.

## 18. MySQL Setup

1. Install MySQL locally and make sure it is running on `localhost:3306`.
2. Open `src/main/resources/application.properties` and **replace `YOUR_PASSWORD`**
   with your MySQL root password.
3. The database `maritime_db` is created automatically
   (`createDatabaseIfNotExist=true`); tables are created/updated automatically
   via `spring.jpa.hibernate.ddl-auto=update`.

## 19. How to Run

```bash
cd maritime-system
mvn spring-boot:run
```
or open the folder as a Maven project in IntelliJ IDEA / VS Code and run
`MaritimeApplication.java` directly. Then visit **http://localhost:8080**.

Sample data (5 vessels, 5 berths, 2 contacts, 5 products, 10 accounts, 4 journals,
1 analytic account, 1 budget, one completed+paid docking, one occupied berth, one
open vendor bill) is inserted automatically on first run.

## 20. Example Workflow (to demo in a viva)

1. Visit `/dashboard` — see seeded KPIs.
2. `/vessels` — add a new vessel.
3. `/dockings` — start a docking (pick a `WAITING` vessel + `AVAILABLE` berth).
4. Click **Process Departure**, enter shore-power kWh → fee is calculated, berth
   is released, and an invoice appears under `/invoices`.
5. `/payments` — record the customer payment against that invoice.
6. `/purchase-orders` → create + approve a PO → `/vendor-bills` → convert to bill
   → `/payments` → pay the vendor bill.
7. `/balance-sheet`, `/profit-loss`, `/budget-report` — see the updated figures.

## 21. Future Enhancements

- Role-based authentication (Admin / Accountant / Dispatcher)
- A dedicated Accounts Receivable ledger account
- Real map-based navigation and live AIS feed integration
- Multi-currency support and tax computation
- Partial payments / payment allocation across multiple invoices

## 22. Code Quality Notes

- Every class has its package declaration and explicit imports.
- Lombok (`@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor`) is
  used only on entities/DTOs to remove boilerplate.
- `GlobalExceptionHandler` converts `ResourceNotFoundException`,
  `IllegalStateException` (including unbalanced-entry errors) and validation
  failures into clean JSON error responses.
- Enums live together in one package-private `Enums.java` file to keep the
  `model/` package from ballooning to 30+ files while staying simple to read.

---

## 23. Viva Questions & Answers

1. **What is Spring Boot?** A framework that auto-configures a Spring
   application so you can run a production-ready web app with minimal setup
   (embedded Tomcat, no XML config).
2. **Why Java 17?** It's the current LTS (Long-Term Support) release required by
   Spring Boot 3, and provides modern language features like records and pattern
   matching.
3. **What is JPA?** Java Persistence API — a specification for mapping Java
   objects to relational database tables (ORM).
4. **What is Hibernate?** The most popular implementation of JPA; Spring Data
   JPA uses it under the hood to generate SQL from your entity classes.
5. **What is Thymeleaf?** A server-side Java templating engine that lets you
   write natural HTML with `th:*` attributes that Spring fills in with data.
6. **What is a REST API?** An architectural style where resources (vessels,
   berths, invoices…) are exposed over HTTP using standard verbs
   (GET/POST/PUT/DELETE) and JSON payloads.
7. **What is AIS?** Automatic Identification System — the real-world maritime
   tracking system vessels use to broadcast position/speed. Here it's simulated.
8. **What is LiDAR?** Light Detection and Ranging — a sensor that measures
   distance to obstacles using laser pulses; simulated here as a single distance value.
9. **How is vessel telemetry simulated?** A client posts JSON to
   `/api/maritime/telemetry`; the system just stores the latest values on the
   vessel — no real sensor hardware or vessel control is involved.
10. **How does berth allocation work?** The system checks the berth is
    available and the vessel fits its length/draft limits, then marks the berth
    `OCCUPIED` and the vessel `DOCKED`.
11. **How is docking fee calculated?** `occupancyHours × baseRate × tier
    multiplier` (1.00 / 1.25 / 1.50 depending on hours docked).
12. **What is double-entry accounting?** Every transaction is recorded as a
    debit in one account and an equal credit in another, keeping the books
    balanced at all times.
13. **Why must debit equal credit?** Because the accounting equation
    (Assets = Liabilities + Equity) must always hold; an imbalance means the
    books are wrong.
14. **What is a Chart of Accounts?** The master list of all ledger accounts
    (Assets, Liabilities, Income, Expenses) a business uses to record
    transactions.
15. **What is an Analytic Account?** A "tag" used for management reporting
    (e.g. tracking one berth sector's budget) that sits alongside the chart of
    accounts.
16. **What is a Purchase Order?** A formal request to a vendor to supply goods
    or services at an agreed price, before any money changes hands.
17. **What is a Vendor Bill?** The vendor's invoice to us, created once we
    receive goods/services against a Purchase Order; it's what we owe.
18. **What is a Sales Order?** A confirmed booking with a customer (here, a
    berth reservation) before the invoice is raised.
19. **What is a Customer Invoice?** A bill we send to a customer for services
    rendered (docking + shore power), showing what they owe us.
20. **Difference between revenue and expense?** Revenue is money earned from
    providing a service (docking fees); expense is money spent to run the
    business (maintenance, radar upkeep).
21. **What is a Balance Sheet?** A snapshot of what the company owns (Assets)
    versus what it owes (Liabilities) at a point in time.
22. **What is a Profit & Loss statement?** A summary of income minus expenses
    over a period, showing whether the business made a profit or a loss.
23. **What is budget variance?** The difference between actual spend/revenue
    and the planned (budgeted) amount — positive variance on expenses means
    overspending; on revenue it means outperforming target.
24. **How does the system update the budget?** Budget lines store
    `actualAmount`; as docking/purchase transactions occur, this project seeds
    representative actuals and `BudgetService.recalculateVariance()`
    recomputes `variance = actual − planned` on demand.
25. **How does the system handle an occupied berth?** `DockingService` rejects
    any new docking request for a berth unless its status is `AVAILABLE` or
    `RESERVED`, throwing a clear error otherwise.
