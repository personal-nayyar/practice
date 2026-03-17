# SIP System Design Diagrams

## High-Level Architecture Diagram

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client/UI     │────│  REST API       │────│  Business Logic │────│   Database      │
│                 │    │                 │    │                 │    │                 │
│ • Mobile App    │    │ • Controllers   │    │ • Services      │    │ • H2 Database   │
│ • Web Portal    │    │ • DTOs          │    │ • Domain Models │    │ • Tables        │
│ • APIs          │    │ • Validation    │    │ • Business Rules│    │ • Relations     │
└─────────────────┘    └─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Data Flow Diagram

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   User      │────│  Controller │────│   Service   │────│ Repository  │
│             │    │             │    │             │    │             │
│1. Browse    │    │2. Validate  │    │3. Process   │    │4. Persist   │
│2. Select    │    │3. Convert   │    │4. Calculate │    │5. Query     │
│3. Create    │    │4. Response  │    │5. Validate  │    │6. Return    │
│4. Manage    │    │             │    │6. Notify    │    │             │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

## Entity Relationship (ER) Diagram

```
┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│      User       │         │     SipPlan      │         │   MutualFund    │
│                 │         │                 │         │                 │
│ • id (PK)       │◄────────│ • id (PK)        │◄────────│ • id (PK)        │
│ • userId        │    1    │ • user_id (FK)   │    *    │ • fundCode (UK) │
│ • name          │         │ • mutualFund_id  │         │ • fundName      │
│ • email         │         │ • sipCode (UK)   │         │ • fundCategory  │
│ • phoneNumber   │         │ • installmentAmt│         │ • currentNav    │
│ • createdAt     │         │ • sipMode        │         │ • isActive      │
│ • isActive      │         │ • status         │         │ • navLastUpdated│
└─────────────────┘         │ • startDate      │         └─────────────────┘
                             │ • endDate        │
                             │ • totalInstall  │
                             │ • completedInst │
                             │ • stepUpPct     │
                             │ • createdAt     │
                             └─────────────────┘
                                      │
                                      │ 1
                                      ▼
                             ┌─────────────────┐
                             │  SipTransaction │
                             │                 │
                             │ • id (PK)        │
                             │ • sipPlan_id (FK)│
                             │ • installmentNo │
                             │ • amount         │
                             │ • navAtExecution │
                             │ • unitsAllocated │
                             │ • paymentStatus  │
                             │ • scheduledDate  │
                             │ • executionDate  │
                             │ • paymentTxnId   │
                             │ • failureReason  │
                             └─────────────────┘
```

## SIP Execution Flow Diagram

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Scheduler │────│  SipService │────│ PaymentSvc  │────│  Notification│
│             │    │             │    │             │    │             │
│• Daily Run  │    │• Find Due  │    │• Process    │    │• Email/SMS  │
│• Check Date │    │• Calculate  │    │• Callback   │    │• Audit Log  │
│• Trigger    │    │• Create Txn │    │• Update Txn │    │• Status     │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

## Technology Engagement Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           SIP System Technology Stack                           │
├─────────────────┬─────────────────┬─────────────────┬─────────────────────────┤
│   Frontend      │   Backend       │   Database      │   DevOps/Infra         │
│                 │                 │                 │                         │
│ • React/Vue     │ • Spring Boot   │ • H2 (Dev)      │ • Docker               │
│ • Angular       │ • Java 17       │ • PostgreSQL    │ • Kubernetes           │
│ • Mobile Apps   │ • Maven         │ • MySQL         │ • CI/CD Pipeline       │
│                 │ • JPA/Hibernate │ • Redis Cache   │ • Monitoring           │
│                 │ • REST APIs     │ • Connection    │ • Logging              │
│                 │ • Validation    │   Pool          │ • Alerting             │
└─────────────────┴─────────────────┴─────────────────┴─────────────────────────┘
```

## Class Diagram

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  UserController │    │ SipPlanController│   │MutualFundController│
│                 │    │                 │    │                 │
│ +getAllFunds()  │    │ +createSip()    │    │ +getFunds()     │
│ +getFund()      │    │ +getSip()       │    │ +getFund()      │
│ +getNav()       │    │ +pauseSip()     │    │ +getNav()       │
└─────────────────┘    │ +unpauseSip()   │    └─────────────────┘
                        │ +stopSip()      │
                        └─────────────────┘
                                 │
                                 ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   UserService   │    │  SipPlanService │    │MutualFundService │
│                 │    │                 │    │                 │
│ +getUserById()  │    │ +createSip()    │    │ +getAllFunds()  │
│ +createUser()   │    │ +getSip()       │    │ +getFund()      │
│ +validateEmail()│    │ +pauseSip()     │    │ +getNav()       │
└─────────────────┘    │ +unpauseSip()   │    │ +updateNav()    │
                        │ +stopSip()      │    └─────────────────┘
                        │ +validateReq()  │
                        │ +calcInstall()  │
                        └─────────────────┘
                                 │
                                 ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   UserRepo      │    │  SipPlanRepo    │    │MutualFundRepo   │
│                 │    │                 │    │                 │
│ +findByUserId() │    │ +findBySipCode()│    │ +findByFundCode()│
│ +findByEmail()  │    │ +findByUserId() │    │ +findActive()   │
│ +existsByEmail()│    │ +save()         │    │ +save()         │
│ +save()         │    │ +delete()       │    │ +delete()       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## State Machine Diagram

```
    ┌─────────────┐
    │   START    │
    └─────────────┘
           │
           ▼
    ┌─────────────┐    pause()    ┌─────────────┐
    │   ACTIVE    │──────────────►│   PAUSED    │
    │             │◄──────────────│             │
    └─────────────┘   unpause()   └─────────────┘
           │                           │
           │ stop()                    │ stop()
           ▼                           ▼
    ┌─────────────┐            ┌─────────────┐
    │   STOPPED   │            │   STOPPED   │
    └─────────────┘            └─────────────┘
           │                           │
           ▼                           ▼
    ┌─────────────┐            ┌─────────────┐
    │  COMPLETED  │            │  COMPLETED  │
    └─────────────┘            └─────────────┘
```

## Data Tables and Relations

### Users Table
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary Key |
| user_id | VARCHAR | UNIQUE, NOT NULL | User Identifier |
| name | VARCHAR | NOT NULL | User Name |
| email | VARCHAR | UNIQUE, NOT NULL | Email Address |
| phone_number | VARCHAR | NOT NULL | Phone Number |
| created_at | TIMESTAMP | NOT NULL | Creation Time |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | Active Status |

### Mutual Funds Table
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary Key |
| fund_code | VARCHAR | UNIQUE, NOT NULL | Fund Code |
| fund_name | VARCHAR | NOT NULL | Fund Name |
| fund_category | VARCHAR | NOT NULL | Fund Category |
| current_nav | DECIMAL(19,4) | NOT NULL | Current NAV |
| nav_last_updated | TIMESTAMP | NOT NULL | Last NAV Update |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | Active Status |

### SIP Plans Table
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary Key |
| user_id | BIGINT | FK → users.id | User Reference |
| mutual_fund_id | BIGINT | FK → mutual_funds.id | Fund Reference |
| sip_code | VARCHAR | UNIQUE, NOT NULL | SIP Code |
| installment_amount | DECIMAL(19,4) | NOT NULL | Installment Amount |
| sip_mode | ENUM | NOT NULL | SIP Mode |
| status | ENUM | NOT NULL | SIP Status |
| start_date | DATE | NOT NULL | Start Date |
| end_date | DATE | NOT NULL | End Date |
| total_installments | INT | NOT NULL | Total Installments |
| completed_installments | INT | NOT NULL, DEFAULT 0 | Completed Installments |
| step_up_percentage | DECIMAL(5,2) | NULL | Step-up Percentage |
| created_at | TIMESTAMP | NOT NULL | Creation Time |
| last_modified | TIMESTAMP | NULL | Last Modified |

### SIP Transactions Table
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary Key |
| sip_plan_id | BIGINT | FK → sip_plans.id | SIP Plan Reference |
| installment_number | INT | NOT NULL | Installment Number |
| installment_amount | DECIMAL(19,4) | NOT NULL | Installment Amount |
| nav_at_execution | DECIMAL(19,4) | NULL | NAV at Execution |
| units_allocated | DECIMAL(19,4) | NULL | Units Allocated |
| payment_status | ENUM | NOT NULL | Payment Status |
| scheduled_date | TIMESTAMP | NOT NULL | Scheduled Date |
| execution_date | TIMESTAMP | NULL | Execution Date |
| payment_transaction_id | VARCHAR | NULL | Payment Transaction ID |
| failure_reason | VARCHAR | NULL | Failure Reason |
| created_at | TIMESTAMP | NOT NULL | Creation Time |

## Deployment Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           Production Environment                               │
├─────────────────┬─────────────────┬─────────────────┬─────────────────────────┤
│   Load Balancer │   Application   │   Database      │   Monitoring/Logging   │
│                 │                 │                 │                         │
│ • AWS ALB       │ • Docker Pods   │ • PostgreSQL    │ • ELK Stack            │
│ • SSL/TLS       │ • Spring Boot   │ • Redis Cache   │ • Prometheus           │
│ • Health Checks │ • 3 Instances   │ • Backup        │ • Grafana              │
│ • Auto Scaling  │ • Horizontal    │ • Replication  │ • Alert Manager        │
└─────────────────┴─────────────────┴─────────────────┴─────────────────────────┘
```

## API Request/Response Flow

```
┌─────────────┐    HTTP Request    ┌─────────────┐    Business Logic    ┌─────────────┐
│   Client    │──────────────────►│ Controller  │──────────────────►│   Service   │
│             │                   │             │                   │             │
│• REST Call  │◄──────────────────│• Validation │◄──────────────────│• Processing │
│• JSON Data  │   HTTP Response   │• DTO Mapping│   Response Data   │• Database  │
│• Error Handle│                   │• Error Handle│                   │• Business  │
└─────────────┘                   └─────────────┘                   └─────────────┘
```

These diagrams provide a comprehensive view of the SIP system architecture, data relationships, and deployment patterns, making it easy to understand the system design and implementation approach.
