# AI-Powered Customer Support Platform — Enhanced Product & Engineering Plan

## 1. Project Overview

### Working title
**SupportAI — AI-Powered Customer Support Platform**

### One-line description
An enterprise-style customer support platform built with Spring Boot, enhanced with Spring AI, RAG, and MCP-based agentic tool calling to automate ticket understanding, knowledge retrieval, and support workflows.

### Primary goal
Use one project to demonstrate strong **Spring Framework / Spring Boot backend engineering** first, then progressively add **Spring AI, RAG, AI agents, and MCP**.

### Target duration
**8 weeks** (40 working days, ~5 days/week)

### Development approach
- Backend-first
- API-first
- No complex frontend required
- Swagger/OpenAPI + Postman for primary interaction
- One Spring Boot monolith
- PostgreSQL as the primary database
- Add AI only after the conventional backend is stable
- Add MCP only after the RAG/AI layer works

---

# 2. Pre-Project Setup (Before Week 1)

## Environment & Dependencies
- [ ] Java 21+ installed and verified
- [ ] Maven 3.9+ installed
- [ ] PostgreSQL 15+ running locally or Docker
- [ ] Docker & Docker Compose installed
- [ ] Git repo created (GitHub/GitLab)
- [ ] IDE configured (IntelliJ IDEA recommended)

## Technology Decisions (Finalize Now)

### LLM Provider
**Chosen: OpenAI (Claude as alternative)**
- Why: Spring AI has excellent support, cost-predictable
- Model: `gpt-4-turbo` (or `claude-opus-sonnet` for Claude)
- Fallback: Use Spring AI's `OllamaApi` for offline testing locally

### Embedding Model
**Chosen: OpenAI `text-embedding-3-small`**
- Vector dimensions: 1536
- Cost-effective, fast, good quality
- Fallback: Ollama's `nomic-embed-text` (384-dim) for local dev

### Vector Store
**Chosen: PostgreSQL + pgvector**
- No separate vector DB to manage
- HNSW indexing for semantic search
- Native SQL integration

### RAG Framework
**Chosen: Spring AI + LangChain4j patterns**
- Vector retrieval via pgvector
- Prompt template management
- Document chunking (fixed 512 tokens with 100-token overlap)

### MCP Tool Framework
**Chosen: Spring AI's native tool calling**
- Avoids external MCP server complexity for MVP
- Wrap existing services as callable tools
- Can integrate real MCP later if needed

---

# 3. Product Vision

The platform simulates a customer support system where:

1. Customers create support tickets.
2. Agents receive and manage tickets.
3. Tickets move through a defined lifecycle.
4. Agents can comment, assign, resolve, and close tickets.
5. SLA rules determine whether tickets are overdue.
6. The system maintains ticket history and audit information.
7. AI classifies incoming tickets.
8. AI summarizes long conversations.
9. AI suggests responses to agents.
10. RAG retrieves relevant support documentation.
11. AI identifies similar historical tickets.
12. An AI agent can use tool calling to search and modify support data.

The final product should demonstrate both:
- **Traditional enterprise backend engineering**
- **Modern AI/agentic backend engineering**

---

# 4. Project Scope

## Phase 1 — Spring Backend (Weeks 1–3)

### Core modules
- Authentication & JWT
- User management (ADMIN, AGENT, CUSTOMER roles)
- Customer management
- Ticket management (full CRUD + lifecycle)
- Ticket comments (with internal flag)
- Ticket assignment
- Ticket status lifecycle (with validation)
- Ticket priority
- Categories (predefined)
- SLA management (calculation + monitoring)
- Ticket history/audit (event-driven)
- Search/filtering/pagination
- Knowledge-base document management (schema only)
- Basic dashboard/statistics
- Unit & integration tests
- Docker & Docker Compose
- Swagger/OpenAPI documentation

## Phase 2 — Spring AI + RAG + MCP (Weeks 4–8)

### AI features
- Ticket classification (multi-label)
- Priority prediction
- Sentiment detection
- Ticket summarization (conversation)
- Suggested agent response (grounded in KB)
- Knowledge-base Q&A (RAG)
- Similar-ticket retrieval (embedding search)
- RAG-based grounded responses
- AI agent with tool calling
- Multi-step support workflows

---

# 5. Explicit Non-Goals

Do NOT add these during the core 8-week project:

- Microservices
- Kubernetes
- Kafka
- Complex React/Angular frontend
- GraphQL
- WebSockets
- Elasticsearch
- Multi-region deployment
- Complex event sourcing
- Full production cloud architecture
- OAuth2 (JWT is sufficient)
- Rate limiting middleware
- Advanced analytics/BI tools

These can be documented as future improvements.

**The goal is depth, not technology count.**

---

# 6. Technology Stack

## Core backend
- Java 21+
- Spring Boot 3.3+
- Spring MVC / Spring Web
- Spring Data JPA
- Hibernate 6+
- Spring Security 6+ (JWT)
- Jakarta Bean Validation
- Spring Transactions
- Spring Events (`ApplicationEventPublisher`)
- Spring Scheduling (`@Scheduled`)
- Spring Cache (`@Cacheable`)
- Spring Actuator (health, metrics)
- Lombok (optional, but recommended for boilerplate)

## Database
- PostgreSQL 15+
- pgvector extension (for embeddings)
- Flyway (migration)

## AI & Embeddings
- Spring AI 1.0+ (OpenAI client)
- LangChain4j patterns (chunking, retrieval)
- pgvector for similarity search

## Testing
- JUnit 5 (Jupiter)
- Mockito 5+
- Spring Boot Test
- Spring Test MockMvc
- Testcontainers (PostgreSQL + pgvector)

## DevOps / API
- Maven 3.9+
- Git
- GitHub/GitLab
- Docker 24+
- Docker Compose
- Swagger/OpenAPI 3.1 (springdoc-openapi)

---

# 7. High-Level Architecture

```
                         Client
                    Swagger / Postman
                           |
                           v
                 +-------------------+
                 |    Spring Boot    |
                 |                   |
                 | REST Controllers  |
                 | Security (JWT)    |
                 | Services          |
                 | JPA / Hibernate   |
                 | Events            |
                 | Scheduling        |
                 | Cache             |
                 | Spring AI         |
                 | Tool Calling      |
                 +---------+---------+
                           |
              +------------+-------------+
              |            |             |
              v            v             v
        PostgreSQL      pgvector        LLM API
              |            ^
              |            |
              +------------+
                           |
                    RAG & Embeddings
                           |
                           v
                  AI Agent / Tools
                           |
            +──────────────┼──────────────+
            |              |              |
            v              v              v
        search_tickets  assign_ticket   add_comment
```

---

# 8. Core Domain Model

## User
Represents an authenticated platform user.

```java
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue
    private Long id;
    
    private String name;
    private String email;
    private String passwordHash;
    
    @Enumerated(EnumType.STRING)
    private UserRole role; // ADMIN, AGENT, CUSTOMER
    
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

Roles:
- `ADMIN` — Full system access
- `AGENT` — Ticket management
- `CUSTOMER` — Create/view own tickets

---

## Customer
A non-authenticated entity representing customers.

```java
@Entity
@Table(name = "customers")
public class Customer {
    @Id @GeneratedValue
    private Long id;
    
    private String name;
    private String email;
    private String phone;
    
    @OneToMany(mappedBy = "customer")
    private List<Ticket> tickets;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

---

## Ticket
Core business entity.

```java
@Entity
@Table(name = "tickets")
public class Ticket {
    @Id @GeneratedValue
    private Long id;
    
    @Column(unique = true)
    private String ticketNumber; // e.g., SUPP-2024-001
    
    private String title;
    private String description;
    
    @Enumerated(EnumType.STRING)
    private TicketStatus status; // OPEN, ASSIGNED, IN_PROGRESS, etc.
    
    @Enumerated(EnumType.STRING)
    private TicketPriority priority; // LOW, MEDIUM, HIGH, URGENT
    
    @Enumerated(EnumType.STRING)
    private TicketCategory category; // BILLING, TECHNICAL, ACCOUNT, etc.
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_agent_id")
    private User assignedAgent;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
    private LocalDateTime slaDueAt;
    private boolean slaBreached;
    
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<TicketComment> comments;
    
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<TicketHistory> history;
}
```

Statuses:
```
OPEN → ASSIGNED → IN_PROGRESS → (WAITING_FOR_CUSTOMER ↔ IN_PROGRESS) → RESOLVED → CLOSED
```

Priorities:
- `LOW` (SLA: 7 days)
- `MEDIUM` (SLA: 3 days)
- `HIGH` (SLA: 1 day)
- `URGENT` (SLA: 4 hours)

---

## TicketComment
```java
@Entity
@Table(name = "ticket_comments")
public class TicketComment {
    @Id @GeneratedValue
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Ticket ticket;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    
    private String content;
    private boolean internal; // true = only agents/admins see
    private LocalDateTime createdAt;
}
```

---

## TicketHistory
Audit trail for state changes.

```java
@Entity
@Table(name = "ticket_history")
public class TicketHistory {
    @Id @GeneratedValue
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Ticket ticket;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private User actor;
    
    @Enumerated(EnumType.STRING)
    private TicketEventType eventType; // CREATED, ASSIGNED, STATUS_CHANGED, etc.
    
    private String oldValue;
    private String newValue;
    private LocalDateTime createdAt;
}
```

---

## KnowledgeDocument
Represents support documentation.

```java
@Entity
@Table(name = "knowledge_documents")
public class KnowledgeDocument {
    @Id @GeneratedValue
    private Long id;
    
    private String title;
    private String description;
    private String fileName;
    private String fileType; // "pdf", "txt", "docx"
    private String storagePath;
    
    @Enumerated(EnumType.STRING)
    private DocumentStatus status; // UPLOADED, PROCESSING, READY, FAILED
    
    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private List<KnowledgeChunk> chunks; // Week 6
}
```

---

## KnowledgeChunk (Week 6)
```java
@Entity
@Table(name = "knowledge_chunks")
public class KnowledgeChunk {
    @Id @GeneratedValue
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private KnowledgeDocument document;
    
    @Column(columnDefinition = "TEXT")
    private String content; // The text chunk (512 tokens max)
    
    @Column(columnDefinition = "vector(1536)")
    private PGvectorType embedding; // OpenAI text-embedding-3-small
    
    private int chunkIndex;
    private LocalDateTime createdAt;
}
```

---

# 9. Important Business Rules

These rules should drive the service layer and tests.

## Ticket Creation
- A customer can create a ticket (anyone can, or authenticated only? → Authenticated only)
- Ticket starts in `OPEN` state
- Ticket receives a unique ticket number (e.g., `SUPP-2024-001`)
- SLA due time is calculated based on priority
- A `TicketCreatedEvent` is published
- Ticket creation is recorded in history

## Assignment
- Only agents/admins can assign tickets
- An inactive agent cannot receive new tickets
- Assigning changes status from `OPEN` → `ASSIGNED`
- Assignment creates a history record
- A `TicketAssignedEvent` is published

## Status Changes
Valid transitions (enforce in service, not DB):
```
OPEN → ASSIGNED
ASSIGNED → IN_PROGRESS
IN_PROGRESS → WAITING_FOR_CUSTOMER
WAITING_FOR_CUSTOMER → IN_PROGRESS
IN_PROGRESS → RESOLVED
RESOLVED → CLOSED
OPEN/ASSIGNED/IN_PROGRESS → CLOSED (admin override)
```

Invalid transitions should throw `InvalidTicketTransitionException`.

## Resolution
- Only the assigned agent or an admin can resolve a ticket
- Resolution changes status to `RESOLVED`
- `resolvedAt` timestamp is recorded
- A `TicketResolvedEvent` is published
- History is recorded

## Closure
- Only an admin can close a resolved ticket
- Status changes to `CLOSED`
- `closedAt` timestamp is recorded
- A `TicketClosedEvent` is published

## SLA Monitoring
- Every 5 minutes, a scheduled task runs
- Finds all open tickets where `slaDueAt < now()`
- Marks `slaBreached = true`
- Publishes a `TicketSLABreachedEvent`
- Agents/admins are notified (optional email/logging)

## Authorization Rules
- **Customers**: Can only view/create their own tickets
- **Agents**: Can view assigned tickets (configurable: all vs. assigned only)
- **Admins**: Full access to all data

---

# 10. REST API Plan

## Authentication
```http
POST   /api/auth/register          # { name, email, password, role }
POST   /api/auth/login             # { email, password } → { token, user }
GET    /api/auth/me                # Get current user (JWT required)
POST   /api/auth/refresh           # Refresh JWT token
POST   /api/auth/logout            # Blacklist token (optional)
```

## Users
```http
GET    /api/users                  # List users (ADMIN only)
GET    /api/users/{id}             # Get user (ADMIN or self)
PUT    /api/users/{id}             # Update user
DELETE /api/users/{id}             # Deactivate user (ADMIN only)
```

## Customers
```http
POST   /api/customers              # Create customer
GET    /api/customers              # List customers (paginated)
GET    /api/customers/{id}         # Get customer
PUT    /api/customers/{id}         # Update customer
DELETE /api/customers/{id}         # Delete customer
GET    /api/customers/{id}/tickets # Get customer's tickets
```

## Tickets
```http
POST   /api/tickets                # Create ticket (customer)
GET    /api/tickets                # List tickets (paginated, filtered)
GET    /api/tickets/{id}           # Get ticket detail
PUT    /api/tickets/{id}           # Update ticket (partial)
DELETE /api/tickets/{id}           # Soft-delete ticket (admin)

# Ticket actions
POST   /api/tickets/{id}/assign            # Assign to agent
POST   /api/tickets/{id}/status            # Change status
POST   /api/tickets/{id}/resolve           # Mark resolved
POST   /api/tickets/{id}/close             # Mark closed
POST   /api/tickets/{id}/reopen            # Reopen ticket

# Ticket search/filter
GET    /api/tickets?status=OPEN
GET    /api/tickets?priority=HIGH
GET    /api/tickets?category=BILLING
GET    /api/tickets?assignedAgentId=10
GET    /api/tickets?customerId=5
GET    /api/tickets?page=0&size=20&sort=createdAt,desc
GET    /api/tickets/search?q=password%20reset
```

## Ticket Comments
```http
POST   /api/tickets/{id}/comments              # Add comment
GET    /api/tickets/{id}/comments              # List comments
PUT    /api/tickets/{id}/comments/{commentId}  # Edit own comment
DELETE /api/tickets/{id}/comments/{commentId}  # Delete own comment
```

## Ticket History
```http
GET    /api/tickets/{id}/history  # Get full audit trail
```

## Knowledge Base
```http
POST   /api/knowledge/documents          # Upload KB document
GET    /api/knowledge/documents          # List documents
GET    /api/knowledge/documents/{id}     # Get document
DELETE /api/knowledge/documents/{id}     # Delete document
GET    /api/knowledge/documents/{id}/chunks # Get chunks (Week 6)

# Search KB (Week 6)
GET    /api/knowledge/search?q=refund%20policy
```

## AI Endpoints (Phase 2)
```http
# Week 5
POST   /api/ai/tickets/{id}/classify           # Classify ticket
POST   /api/ai/tickets/{id}/summarize          # Summarize conversation
POST   /api/ai/tickets/{id}/suggest-response   # Suggest response

# Week 6
POST   /api/ai/ask                             # Q&A against KB (RAG)
POST   /api/ai/tickets/{id}/similar            # Find similar tickets

# Week 7-8
POST   /api/ai/agent/chat                      # Agent with tool calling
```

## Dashboard/Stats (Optional, P2)
```http
GET    /api/dashboard/stats                    # Overall stats
GET    /api/dashboard/agent/{id}/stats         # Agent stats
GET    /api/dashboard/sla-report               # SLA breaches
```

---

# 11. Spring Features to Showcase

The project intentionally demonstrates:

## Spring Boot
- `application.properties` / `application-{profile}.properties`
- Active profiles (dev, test, prod)
- `@ConfigurationProperties` for typed config
- Dependency injection & bean lifecycle
- Auto-configuration customization
- Spring Actuator health & metrics

## Spring MVC
- `@RestController`, `@GetMapping`, `@PostMapping`
- Request/response DTOs with validation
- `@Valid` bean validation
- Custom exception handling (`@ControllerAdvice`)
- HTTP status codes (200, 201, 400, 403, 404, 409, 500)
- Pagination (`Pageable`, `Page<T>`)
- Sorting

## Spring Data JPA
- `CrudRepository`, `JpaRepository`
- Derived queries: `findByStatusAndPriority`, `findByCreatedAtAfter`
- `@Query` with JPQL
- `Specifications` for complex filtering (optional)
- Pagination: `repository.findAll(pageable)`
- Sorting: `Sort.by("createdAt").descending()`

## Hibernate
- Entity relationships (`@OneToMany`, `@ManyToOne`, fetch strategy)
- Lazy loading gotchas (N+1 problem detection)
- `@Transactional` boundaries
- Persistence context
- Dirty checking
- Cascading (CascadeType.ALL on audit, none on FK relations)
- orphanRemoval for one-to-many cleanup

## Spring Security
- `SecurityConfig` with filter chains
- JWT token generation & validation
- `@PreAuthorize("hasRole('ADMIN')")` for method-level security
- Custom `UserDetailsService`
- BCrypt for password hashing

## Spring Transactions
Example: Resolving a ticket atomically
```java
@Service
public class TicketService {
    
    @Transactional
    public void resolveTicket(Long ticketId, String resolutionNote) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();
        
        // All or nothing
        ticket.setStatus(RESOLVED);
        ticket.setResolvedAt(now());
        ticketRepository.save(ticket);
        
        TicketHistory history = new TicketHistory(...);
        historyRepository.save(history);
        
        applicationEventPublisher.publishEvent(new TicketResolvedEvent(ticket));
    }
}
```

## Spring Events
```java
@Component
public class TicketEventListener {
    
    @EventListener
    public void onTicketCreated(TicketCreatedEvent event) {
        // Send notification, log to analytics, etc.
    }
    
    @EventListener
    public void onTicketResolved(TicketResolvedEvent event) {
        // Mark as resolved in audit log
    }
}
```

## Spring Scheduling
```java
@Component
public class SLAMonitor {
    
    @Scheduled(fixedDelay = 300000) // Every 5 min
    public void checkSLABreaches() {
        List<Ticket> overdue = ticketRepository.findOverdueTickets();
        overdue.forEach(t -> {
            t.setSlaBreached(true);
            applicationEventPublisher.publishEvent(new TicketSLABreachedEvent(t));
        });
    }
}
```

## Spring Cache
```java
@Component
public class CategoryService {
    
    @Cacheable("categories")
    public List<TicketCategory> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    @CacheEvict("categories")
    public void refreshCategories() { }
}
```

## Spring Actuator
Enable in `application.properties`:
```properties
management.endpoints.web.exposure.include=health,metrics,info
management.endpoint.health.show-details=when-authorized
```

---

# 12. Week-by-Week Breakdown

## Week 1: Spring Boot Foundation + Authentication

### Goals
- Spring Boot project scaffolding
- Database schema & migrations (Flyway)
- User & authentication layer
- JWT token generation/validation
- Role-based access control (RBAC)

### Deliverables (Daily)
**Day 1:**
- [ ] Maven/Spring Boot project created
- [ ] PostgreSQL + Flyway setup
- [ ] User entity & repository
- [ ] Initial database migration

**Day 2:**
- [ ] JWT token provider implementation
- [ ] `SecurityConfig` with filter chain
- [ ] Login & register controllers
- [ ] Password hashing (BCrypt)

**Day 3:**
- [ ] `@PreAuthorize` annotations on endpoints
- [ ] Custom `UserDetailsService`
- [ ] Auth tests (unit + integration)
- [ ] Swagger setup with auth support

**Day 4:**
- [ ] Refresh token logic
- [ ] Logout/token blacklist (optional)
- [ ] Role-based endpoint guards
- [ ] Integration tests for auth flows

**Day 5:**
- [ ] Docker setup (Dockerfile + docker-compose.yml)
- [ ] Database migration verification
- [ ] Actuator health check
- [ ] README documentation

### Key Metrics
- ✅ Login/register endpoints fully tested
- ✅ JWT tokens valid & refreshable
- ✅ Swagger shows auth requirements
- ✅ Docker Compose runs cleanly

### If Behind: Cut scope
- Skip refresh token (use long-lived JWT)
- Skip logout (tokens expire naturally)
- Skip role-based detail (just ADMIN vs USER)

---

## Week 2: Ticket Core + Lifecycle

### Goals
- Ticket entity & full CRUD
- Ticket status lifecycle with validation
- Ticket-Customer relationship
- Ticket-Agent assignment
- SLA calculation
- Ticket history/audit

### Deliverables (Daily)
**Day 1:**
- [ ] Ticket, TicketComment, TicketHistory entities
- [ ] Database migrations (Flyway)
- [ ] Repositories + derived queries

**Day 2:**
- [ ] TicketService with business logic
- [ ] Status transition validation (state machine)
- [ ] SLA calculation based on priority
- [ ] Unit tests for service layer

**Day 3:**
- [ ] REST controllers (POST, GET, PUT)
- [ ] DTOs (TicketCreateRequest, TicketResponse, TicketFilter)
- [ ] Pagination & sorting
- [ ] Search/filter endpoints

**Day 4:**
- [ ] Ticket assignment logic
- [ ] Authorization checks (customer can only see own, agent sees assigned)
- [ ] Comment add/edit endpoints
- [ ] Internal comment flag

**Day 5:**
- [ ] Swagger documentation
- [ ] Integration tests (Testcontainers)
- [ ] Edge cases (invalid transitions, permission denials)
- [ ] Postman collection

### Key Metrics
- ✅ All ticket CRUD operations working
- ✅ Status transitions validated & enforced
- ✅ SLA calculated correctly (per priority)
- ✅ Authorization checks in place
- ✅ 80%+ test coverage on TicketService

### If Behind: Cut scope
- Skip comment editing
- Skip internal comment filtering (show all)
- Skip advanced search (keep only status/priority)

---

## Week 3: Spring Features + Testing

### Goals
- Spring Events for ticket lifecycle
- Spring Scheduling for SLA monitoring
- Comprehensive unit & integration tests
- Flyway migrations verified
- N+1 problem identification & fix
- Swagger/OpenAPI complete

### Deliverables (Daily)
**Day 1:**
- [ ] TicketCreatedEvent, TicketAssignedEvent, TicketResolvedEvent
- [ ] EventListener components
- [ ] Publish events from service
- [ ] Unit tests for event flow

**Day 2:**
- [ ] `@Scheduled` SLA breach checker (every 5 min)
- [ ] TicketSLABreachedEvent listener
- [ ] Database index on slaDueAt
- [ ] Tests for scheduler

**Day 3:**
- [ ] Identify & fix N+1 issues (fetch joins, `@EntityGraph`)
- [ ] Lazy loading strategy review
- [ ] Performance testing (10k tickets)
- [ ] Slow query logging enabled

**Day 4:**
- [ ] Testcontainers PostgreSQL setup
- [ ] Integration test suite (full ticket lifecycle)
- [ ] Mock Spring Security in tests
- [ ] Test data builders

**Day 5:**
- [ ] Swagger generation + customization
- [ ] API documentation in README
- [ ] Docker image build verification
- [ ] CI/CD pipeline (GitHub Actions) basic setup

### Key Metrics
- ✅ Events published & handled
- ✅ SLA checker running (no false positives)
- ✅ N+1 eliminated (query count stable with scale)
- ✅ 90%+ test coverage
- ✅ Docker image builds & runs
- ✅ All endpoints documented in Swagger

### If Behind: Cut scope
- Skip SLA scheduler (manual checks only)
- Skip event listeners (just publish)
- Skip N+1 optimization (document as future work)
- Skip CI/CD (local testing only)

---

## Week 4: Caching + Observability + Buffer

### Goals
- Spring Cache for stable data
- Spring Actuator metrics/health
- Performance benchmarking
- Buffer for Week 1-3 catch-up

### Deliverables (Daily)
**Day 1-2:**
- [ ] `@Cacheable` on categories, support configs
- [ ] Cache invalidation logic
- [ ] Cache hit/miss metrics

**Day 3:**
- [ ] Actuator health endpoint customization
- [ ] Custom metrics (ticket counts, SLA breaches)
- [ ] Prometheus integration (optional)

**Day 4:**
- [ ] Load testing (Apache JMeter or Gatling)
- [ ] Database performance review
- [ ] Slowness identification & fixes

**Day 5:**
- [ ] Week 1-3 tech debt cleanup
- [ ] Code review & refactoring
- [ ] Missing integration tests
- [ ] Swagger completeness

### Key Metrics
- ✅ Cache hit rate > 80% for categories
- ✅ Actuator endpoints accessible
- ✅ Sub-100ms response for cached queries
- ✅ Zero technical debt blocking AI features

### If Behind: Extend
- Spend full week on buffer
- Finish Week 3 incomplete items
- Add missing integration tests
- Polish Phase 1

---

## Week 5: Spring AI + Prompt Engineering

### Goals
- Ticket classification (multi-label)
- Priority prediction
- Sentiment analysis
- Suggested response to agent
- Structured AI output (DTOs, not string parsing)

### Deliverables (Daily)
**Day 1:**
- [ ] Spring AI dependency + OpenAI config
- [ ] AI service scaffold with prompt templates
- [ ] PromptTemplate for ticket classification
- [ ] Test with manual API calls

**Day 2:**
- [ ] Ticket classification endpoint (POST /api/ai/tickets/{id}/classify)
- [ ] Parse structured output (JSON) from LLM
- [ ] DTO: `ClassificationResult { category, confidence, labels }`
- [ ] Validation & error handling

**Day 3:**
- [ ] Priority prediction (LOW/MEDIUM/HIGH/URGENT)
- [ ] Sentiment detection (POSITIVE/NEUTRAL/NEGATIVE)
- [ ] Prompt engineering for consistency
- [ ] Unit tests (mock LLM responses)

**Day 4:**
- [ ] Ticket summarization endpoint
- [ ] DTO: `SummaryResult { summary, keyIssues, suggestedResolution }`
- [ ] Handle long conversations (truncate/summarize recursively)
- [ ] Tests

**Day 5:**
- [ ] Suggested response endpoint
- [ ] DTO: `SuggestedResponse { response, tone, confidence }`
- [ ] Integration tests with Testcontainers
- [ ] Swagger documentation
- [ ] Cost estimation (token usage logging)

### Key Metrics
- ✅ Classification endpoint working
- ✅ Structured output 95%+ parseable
- ✅ LLM calls logged with token counts
- ✅ Fallback behavior for API errors
- ✅ All endpoints integration-tested

### Code Example: Structured Classification
```java
@Service
public class TicketClassificationService {
    private final ChatClient chatClient;
    
    public ClassificationResult classify(String ticketDescription) {
        String prompt = """
            Classify this support ticket into ONE category: BILLING, TECHNICAL, ACCOUNT, SUBSCRIPTION, SECURITY, GENERAL.
            Also provide confidence (0-100) and up to 3 relevant tags.
            
            Ticket: {description}
            
            Respond ONLY as JSON:
            { "category": "...", "confidence": 95, "tags": ["tag1", "tag2"] }
            """;
        
        String response = chatClient.call(prompt);
        return parseJson(response, ClassificationResult.class);
    }
}

record ClassificationResult(String category, int confidence, List<String> tags) {}
```

### If Behind: Cut scope
- Skip sentiment detection
- Skip priority prediction (manual only)
- Skip suggested response (just classification + summary)

---

## Week 6: RAG + Vector Search

### Goals
- Knowledge base document ingestion
- Embedding generation
- Vector storage in pgvector
- Similarity search (find similar tickets)
- RAG-based Q&A

### Deliverables (Daily)
**Day 1:**
- [ ] KnowledgeDocument & KnowledgeChunk entities
- [ ] Document upload endpoint (POST /api/knowledge/documents)
- [ ] File storage (local or S3, keep simple)
- [ ] Status pipeline (UPLOADED → PROCESSING → READY)

**Day 2:**
- [ ] Text extraction (Apache Tika or Spring Framework)
- [ ] Chunking strategy: 512 tokens max, 100-token overlap
- [ ] Tokenization & chunk validation
- [ ] Unit tests for chunking

**Day 3:**
- [ ] Embedding generation via Spring AI + OpenAI
- [ ] Store embeddings in pgvector
- [ ] Status updates (PROCESSING → READY / FAILED)
- [ ] Retry logic for embedding failures

**Day 4:**
- [ ] Vector similarity search (pgvector `<->` operator)
- [ ] Top-K retrieval (default K=5)
- [ ] Similar ticket finder (embed current ticket, search historical)
- [ ] Endpoint: GET /api/ai/tickets/{id}/similar

**Day 5:**
- [ ] RAG Q&A endpoint (POST /api/ai/ask)
- [ ] Retrieve relevant chunks
- [ ] Build context + prompt for LLM
- [ ] Grounded response (cite sources)
- [ ] Integration tests

### Key Metrics
- ✅ Documents ingest & chunk correctly
- ✅ Embeddings generated & stored
- ✅ Vector search < 200ms
- ✅ Similar tickets retrieved (relevant results)
- ✅ RAG responses cite source documents

### Code Example: RAG Retrieval
```java
@Service
public class KnowledgeBaseService {
    
    public String answerQuestion(String question) {
        // 1. Embed question
        List<Double> questionEmbedding = embeddingService.embed(question);
        
        // 2. Search pgvector
        List<KnowledgeChunk> relevant = chunkRepository.findSimilar(questionEmbedding, 5);
        
        // 3. Build context
        String context = relevant.stream()
            .map(c -> c.getContent())
            .collect(joining("\n\n"));
        
        // 4. LLM with context
        String prompt = """
            Use this knowledge base to answer the question.
            If not found, say so.
            
            Knowledge:
            {context}
            
            Question: {question}
            """;
        
        return chatClient.call(prompt);
    }
}
```

### If Behind: Cut scope
- Skip similar ticket search (keep KB Q&A)
- Use pre-chunked documents (no extraction)
- Skip retry logic (simple async background job)

---

## Week 7: AI Agent + Tool Calling

### Goals
- Implement tool calling with Spring AI
- Define MCP-style tools
- Build first agent workflows
- Authorization for tool execution

### Deliverables (Daily)
**Day 1:**
- [ ] Tool schema definition (tool calling specs)
- [ ] `@Tool` annotations on service methods
- [ ] Tool registration with Spring AI
- [ ] Basic tool: `search_tickets(status, priority)`

**Day 2:**
- [ ] Tool: `get_ticket(id)` — fetch ticket detail
- [ ] Tool: `get_customer_tickets(customerId)` — list customer tickets
- [ ] Tool: `search_knowledge_base(query)` — KB search
- [ ] Tool tests

**Day 3:**
- [ ] Tool: `assign_ticket(ticketId, agentId)` — with authorization
- [ ] Tool: `update_ticket_status(ticketId, status)` — with validation
- [ ] Tool: `add_ticket_comment(ticketId, content)` — with author
- [ ] Authorization checks inside tools

**Day 4:**
- [ ] Agent loop with tool calling
- [ ] Endpoint: POST /api/ai/agent/chat
- [ ] Request: `{ "userId": 10, "query": "Find my open high-priority tickets" }`
- [ ] Response: Agent calls tools, returns result
- [ ] Streaming response (if supported)

**Day 5:**
- [ ] Integration tests (agent workflow end-to-end)
- [ ] Error handling (tool failures, auth denial)
- [ ] Tool result parsing
- [ ] Documentation: tools, schemas, examples

### Key Metrics
- ✅ All tools callable & return correct data
- ✅ Authorization enforced (no privilege escalation)
- ✅ Agent completes multi-step workflows
- ✅ Error gracefully handled
- ✅ Full audit trail (tool calls logged)

### Code Example: Tool Definition
```java
@Service
public class TicketToolService {
    
    @Tool("Finds tickets by status and priority. Returns matching ticket IDs and summaries.")
    public List<TicketSummary> searchTickets(
        @ToolParam(value = "status", description = "OPEN, ASSIGNED, IN_PROGRESS, etc.") String status,
        @ToolParam(value = "priority", description = "LOW, MEDIUM, HIGH, URGENT") String priority
    ) {
        return ticketRepository.findByStatusAndPriority(status, priority).stream()
            .map(t -> new TicketSummary(t.getId(), t.getTitle(), t.getStatus(), t.getPriority()))
            .collect(toList());
    }
}

record TicketSummary(Long id, String title, String status, String priority) {}
```

### If Behind: Cut scope
- Implement only `search_tickets` & `get_ticket` (read-only)
- Skip write tools (`assign_ticket`, `update_status`)
- Skip authorization details (log access only)

---

## Week 8: Multi-Step Workflows + Polish + Buffer

### Goals
- Complex agent workflows (3+ steps)
- Production polish
- Documentation
- Testing completion
- Buffer for Week 6-7 catch-up

### Deliverables (Daily)
**Day 1-2:**
- [ ] Multi-step workflow example:
  - User: "Find John's unresolved billing issues, identify highest priority, assign to me"
  - Agent: get_customer → get_customer_tickets → filter → assign_ticket → response
- [ ] Test multi-step workflows
- [ ] Verify tool calls in order

**Day 3:**
- [ ] Error handling & recovery
- [ ] Agent timeout handling
- [ ] Tool result validation
- [ ] Audit logging for all tool calls

**Day 4:**
- [ ] Architecture diagrams (draw.io, Mermaid)
- [ ] README: project structure, how to run, examples
- [ ] API examples (Postman collection)
- [ ] Demo workflow documentation

**Day 5:**
- [ ] Week 6-7 catch-up
- [ ] Missing tests (aim for 85%+ coverage)
- [ ] Final Swagger verification
- [ ] Docker image test & publish

### Deliverables (From Definition of Done)
- [x] Customer can register/login
- [x] JWT authentication works
- [x] Roles/authorization work
- [x] Customer can create tickets
- [x] Agent can manage tickets
- [x] Ticket lifecycle is enforced
- [x] Comments work
- [x] Ticket history is recorded
- [x] SLA is calculated
- [x] Scheduled SLA monitoring works
- [x] Spring events are used meaningfully
- [x] Pagination/filtering/sorting work
- [x] JPA/Hibernate relationships properly implemented
- [x] N+1 issue investigated & addressed
- [x] Flyway migrations work
- [x] Unit tests exist
- [x] Integration tests exist
- [x] Testcontainers used
- [x] Docker Compose works
- [x] AI classification works
- [x] AI summarization works
- [x] AI suggested response works
- [x] Knowledge documents can be ingested
- [x] Embeddings are generated
- [x] pgvector search works
- [x] RAG Q&A works
- [x] Similar-ticket retrieval works
- [x] Tool calling works
- [x] Agent can call tools
- [x] Tool authorization is enforced
- [x] Multi-step agent workflow works
- [x] Swagger is complete
- [x] README is complete
- [x] Architecture diagrams included
- [x] Demo workflow documented

### If Behind: Extend
- Spend full week on catch-up
- Prioritize P0 (Spring, JPA, Security, AI basics)
- Cut P2 (dashboard, advanced analytics)
- Document incomplete items as future work

---

# 13. Risk Management

## Critical Risks

### Risk 1: N+1 Query Problem (Week 2-3)
**Impact**: Severe (query explosion at scale)
**Probability**: High (easy to miss)

**Mitigation**:
- Enable SQL logging early
- Use Hibernate statistics plugin
- Add `@EntityGraph` preemptively
- Load test with 10k tickets before Week 3 end

### Risk 2: Spring AI API Rate Limiting (Week 5)
**Impact**: High (blocking development)
**Probability**: Medium

**Mitigation**:
- Set up local Ollama fallback early
- Batch API calls during development
- Monitor token usage closely
- Budget ~$100-200 for 8-week dev

### Risk 3: pgvector Query Performance (Week 6)
**Impact**: High (RAG unusable)
**Probability**: Medium

**Mitigation**:
- Create HNSW index on embeddings
- Test with 100+ documents before Week 6 end
- Use reasonable top-K (5-10)
- Profile similarity search latency

### Risk 4: Week 1-2 Overrun (Timeline)
**Impact**: High (cascading delays)
**Probability**: Medium

**Mitigation**:
- Spend Day 1 on project scaffold (use Spring Initializr)
- Pre-write SQL migrations (have them ready)
- Use Lombok to reduce boilerplate
- Daily 15-min standup check-in

### Risk 5: LLM Structured Output Failures (Week 5)
**Impact**: Medium (unreliable parsing)
**Probability**: High

**Mitigation**:
- Always parse JSON with schema validation
- Add fallback/retry on parse errors
- Use JSON mode (if LLM supports it)
- Test with 20+ real examples before prod

---

## Dependency Risks

| Dependency | Risk | Mitigation |
|------------|------|-----------|
| OpenAI API availability | Downtime halts Week 5+ | Use Ollama as local fallback |
| PostgreSQL + pgvector | Extension install issues | Test Docker Compose Day 1 Week 1 |
| Spring AI stability | API changes | Lock version, use LTS |
| Testcontainers | Resource limits | Set docker memory limits |

---

# 14. Priority System (If Behind)

## P0 — Must Finish ✅
```
✓ Spring Boot + Maven project
✓ PostgreSQL + Flyway
✓ User/Auth + JWT
✓ Ticket CRUD + lifecycle
✓ Comments + history
✓ SLA calculation
✓ JPA relationships (no N+1)
✓ Spring Security authorization
✓ Unit + integration tests
✓ Docker setup
✓ Spring AI classification
✓ Spring AI summarization
✓ RAG + vector search
✓ Tool calling
✓ Multi-step agent workflow
```

## P1 — Important 🔸
```
◇ Spring Events + listeners
◇ Scheduled SLA monitoring
◇ Spring Caching
◇ Sentiment detection
◇ Similar ticket search
◇ Source citations in RAG
◇ Tool authorization detail
◇ Swagger completeness
◇ Postman collection
◇ Architecture diagram
```

## P2 — Nice to Have 💎
```
◆ Dashboard/stats endpoints
◆ Advanced analytics
◆ Email notifications
◆ Cloud deployment (Heroku, Railway)
◆ Refresh token endpoint
◆ Logout blacklist
◆ Prometheus metrics
◆ Rate limiting middleware
◆ GraphQL support
```

**Rule: Cut P2, not P0.**

---

# 15. Technology Decisions Reference

| Decision | Chosen | Alternative | Why |
|----------|--------|-------------|-----|
| LLM | OpenAI (GPT-4-turbo) | Claude Opus, Llama | Cost-predictable, Spring AI support |
| Embedding | OpenAI text-embedding-3-small | Nomic, Ollama | Good quality, 1536-dim standard |
| Vector DB | pgvector | Pinecone, Weaviate | No separate service to manage |
| RAG Framework | Spring AI + LangChain4j | LlamaIndex, Haystack | Lightweight, Spring-native |
| Tool Calling | Spring AI native | External MCP server | Simpler MVP, reuse existing services |
| Cache | Spring Cache | Redis | Simpler local dev, not needed for single instance |
| Monitoring | Spring Actuator | Prometheus+Grafana | Sufficient for MVP |
| CI/CD | GitHub Actions | Jenkins, GitLab CI | Simple, free, GitHub-native |

---

# 16. Code Structure (Recommended)

```
support-ai/
├── src/main/java/com/supportai/
│   ├── SupportAiApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── JwtTokenProvider.java
│   │   └── WebConfig.java
│   ├── domain/
│   │   ├── entities/
│   │   │   ├── User.java
│   │   │   ├── Customer.java
│   │   │   ├── Ticket.java
│   │   │   ├── TicketComment.java
│   │   │   ├── TicketHistory.java
│   │   │   ├── KnowledgeDocument.java
│   │   │   └── KnowledgeChunk.java
│   │   ├── enums/
│   │   │   ├── UserRole.java
│   │   │   ├── TicketStatus.java
│   │   │   ├── TicketPriority.java
│   │   │   ├── TicketCategory.java
│   │   │   ├── DocumentStatus.java
│   │   │   └── TicketEventType.java
│   │   ├── repositories/
│   │   │   ├── UserRepository.java
│   │   │   ├── TicketRepository.java
│   │   │   ├── CommentRepository.java
│   │   │   ├── HistoryRepository.java
│   │   │   ├── KnowledgeDocumentRepository.java
│   │   │   └── KnowledgeChunkRepository.java
│   │   └── events/
│   │       ├── TicketCreatedEvent.java
│   │       ├── TicketAssignedEvent.java
│   │       ├── TicketResolvedEvent.java
│   │       └── TicketSLABreachedEvent.java
│   ├── service/
│   │   ├── TicketService.java
│   │   ├── UserService.java
│   │   ├── AuthService.java
│   │   ├── SLAService.java
│   │   ├── KnowledgeBaseService.java (Week 6)
│   │   ├── TicketClassificationService.java (Week 5)
│   │   ├── TicketSummarizationService.java (Week 5)
│   │   ├── RAGService.java (Week 6)
│   │   ├── TicketToolService.java (Week 7)
│   │   ├── AIAgentService.java (Week 7)
│   │   └── EmbeddingService.java (Week 6)
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── TicketController.java
│   │   ├── CustomerController.java
│   │   ├── KnowledgeController.java (Week 6)
│   │   ├── AIController.java (Week 5+)
│   │   └── DashboardController.java (P2)
│   ├── dto/
│   │   ├── request/
│   │   │   ├── TicketCreateRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   └── ClassifyTicketRequest.java
│   │   ├── response/
│   │   │   ├── TicketResponse.java
│   │   │   ├── AuthResponse.java
│   │   │   ├── ClassificationResult.java
│   │   │   ├── SummaryResult.java
│   │   │   └── RAGResponse.java
│   │   └── filter/
│   │       └── TicketFilter.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── TicketNotFoundException.java
│   │   ├── InvalidTicketTransitionException.java
│   │   └── UnauthorizedException.java
│   ├── security/
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── UserDetailsServiceImpl.java
│   │   └── SecurityUtil.java
│   └── listener/
│       └── TicketEventListener.java
├── src/main/resources/
│   ├── application.properties
│   ├── application-dev.properties
│   ├── application-test.properties
│   ├── db/
│   │   └── migration/
│   │       ├── V1__init.sql
│   │       ├── V2__add_ticket.sql
│   │       ├── V3__add_comments.sql
│   │       ├── V4__add_pgvector.sql
│   │       └── V5__add_kb_chunks.sql
│   └── logback-spring.xml
├── src/test/java/com/supportai/
│   ├── TicketServiceTests.java
│   ├── TicketControllerTests.java
│   ├── AuthServiceTests.java
│   ├── AuthControllerTests.java
│   ├── IntegrationTests.java
│   └── AIServiceTests.java
├── docker/
│   └── docker-compose.yml
├── docs/
│   ├── architecture.md
│   ├── database.md
│   ├── rag.md
│   ├── mcp.md
│   ├── api-examples.md
│   └── setup.md
├── .github/workflows/
│   └── ci.yml
├── Dockerfile
├── pom.xml
├── README.md
└── .gitignore
```

---

# 17. Spring AI Configuration (Week 5)

```properties
# application.properties
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-4-turbo
spring.ai.openai.chat.options.temperature=0.7
spring.ai.openai.embedding.options.model=text-embedding-3-small

# Or for Ollama local:
# spring.ai.ollama.base-url=http://localhost:11434
# spring.ai.ollama.chat.options.model=mistral
```

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

---

# 18. Deployment Checklist

### Local Development
- [ ] Docker Compose runs cleanly
- [ ] PostgreSQL initializes with migrations
- [ ] App starts on http://localhost:8080
- [ ] Swagger UI accessible at /swagger-ui.html
- [ ] All tests pass

### Docker Container
- [ ] Dockerfile builds cleanly
- [ ] Image size < 500MB
- [ ] Health check endpoint works
- [ ] No hardcoded secrets
- [ ] Logs visible via `docker logs`

### Production Readiness (Future)
- [ ] Environment variables for all configs
- [ ] Database connection pooling tuned
- [ ] Monitoring enabled (Actuator)
- [ ] Error tracking (Sentry optional)
- [ ] API key rotation strategy
- [ ] Backup/recovery procedure

---

# 19. Final Deliverables

At the end of eight weeks, GitHub should contain:

```
support-ai/
├── src/
│   ├── main/java/com/supportai/
│   │   ├── config/
│   │   ├── domain/
│   │   ├── service/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── exception/
│   │   ├── security/
│   │   └── listener/
│   ├── main/resources/
│   │   ├── application*.properties
│   │   └── db/migration/
│   └── test/java/com/supportai/
│
├── db/
│   └── migration/
│       ├── V1__init.sql
│       ├── V2__ticket.sql
│       ├── V3__comments.sql
│       ├── V4__pgvector.sql
│       └── V5__kb.sql
│
├── docs/
│   ├── architecture.md
│   ├── database.md
│   ├── rag.md
│   ├── mcp.md
│   ├── api-examples.md
│   └── setup.md
│
├── docker/
│   ├── Dockerfile
│   └── docker-compose.yml
│
├── .github/workflows/
│   └── ci.yml
│
├── pom.xml
├── README.md
├── .gitignore
└── NOTES.md (development journal, lessons learned)
```

---

# 20. Documentation Sections

## architecture.md (Write Week 8)
```markdown
# Architecture

## Overview
- Request flow: Controller → Service → Repository → DB
- Event-driven audit trail
- Spring Security authorization
- Spring AI + RAG layer

## Components
- REST Controllers (7 endpoints × phases)
- Service Layer (business logic)
- Repository Layer (data access)
- Event Listeners (side effects)
- AI Integration (Spring AI)
- RAG Pipeline (embeddings → retrieval)
- Tool Calling (agent + MCP-style tools)

## Data Flow (Ticket Creation)
```

## database.md (Write Week 2-3)
- ER diagram (Mermaid or draw.io)
- Entity relationships
- Key indexes (slaDueAt, status, customer_id)
- Transaction boundaries

## rag.md (Write Week 6)
- Document ingestion pipeline
- Chunking strategy (512 tokens, 100-token overlap)
- Embedding model choice
- Similarity search (pgvector)
- Retrieval-augmented generation flow
- Source attribution

## mcp.md (Write Week 7)
- Tool schema definitions
- Authorization model
- Tool call example (JSON)
- Error handling for tool failures
- Future: real MCP server integration

## api-examples.md (Write Week 8)
- cURL examples for all endpoints
- Response samples
- Error codes
- Pagination examples

---

# 21. Interview Topics Covered

After completion, you can discuss:

### Spring Core
- Dependency injection & bean lifecycle
- Spring Security filter chain
- JWT token handling
- Method-level security (`@PreAuthorize`)
- Transactions (`@Transactional`)
- Application events

### JPA/Hibernate
- Entity relationships (OneToMany, ManyToOne)
- Lazy vs. eager loading trade-offs
- N+1 problem: identification & resolution
- `@EntityGraph` for fetch strategies
- Dirty checking & persistence context
- Cascade & orphanRemoval policies

### AI/Embeddings
- LLM prompting & structured output
- Embedding models & vector similarity
- RAG pipeline (document → chunking → embedding → retrieval)
- Grounding AI responses in source documents
- Token counting & cost estimation

### Tool Calling / Agents
- Tool schema definition
- Agent loop (think → act → observe)
- Authorization for tool execution
- Multi-step workflows
- MCP concepts

### Spring-specific Deep Dives
- Spring Cache eviction strategies
- Scheduled task best practices
- Event listener ordering
- Spring Actuator custom metrics
- Testcontainers integration

---

# 22. CV Positioning

After completion, present the project as:

**AI-Powered Customer Support Platform**  
`Java | Spring Boot | Spring Data JPA | Hibernate | PostgreSQL | Spring Security | Spring AI | RAG | pgvector | Docker`

### CV Bullets

- **Built an enterprise-grade customer support platform** using Spring Boot 3.3+ with Spring Data JPA, Hibernate, and PostgreSQL, implementing ticket lifecycle management, role-based access control, SLA tracking, and transactional workflows (1,200+ LOC service layer).

- **Designed and optimized Hibernate relationships** to eliminate N+1 query problems using `@EntityGraph`, achieving sub-100ms response times for ticket queries at scale (10k+ tickets).

- **Implemented Spring application events and scheduled SLA monitoring** to detect overdue tickets every 5 minutes, publishing domain events for audit logging and notifications without coupling components.

- **Integrated Spring AI for intelligent ticket classification, conversation summarization, and agent response generation** using structured output DTOs, achieving 90%+ parsing accuracy with fallback error handling.

- **Built RAG pipeline using document chunking (512 tokens), OpenAI embeddings, and pgvector similarity search** to ground AI responses in company documentation, reducing hallucinations by 70% (measured via source attribution).

- **Implemented AI agent with tool calling** (search_tickets, assign_ticket, add_comment) with authorization checks, enabling multi-step support workflows while reusing existing service-layer business logic.

- **Developed comprehensive test suite** using JUnit 5, Mockito, Testcontainers (PostgreSQL), and Spring Boot Test, achieving 85%+ code coverage with integration tests for ticket lifecycle and RAG retrieval.

- **Containerized application** with Docker & Docker Compose, enabling one-command setup (`docker-compose up`) with automatic database migrations via Flyway.

### Real Metrics (Use Only If Measured)
- Response time: 95th percentile < 150ms (under load)
- Test coverage: 85% on service layer
- AI classification accuracy: 92% (on 100-ticket validation set)
- RAG retrieval precision: Top-3 contains relevant doc 90% of time

---

# 23. Definition of Done

The project is complete when all items are checked:

### Phase 1: Spring Backend (Week 1-3)
- [x] Customer can register/login with JWT
- [x] JWT token validation & refresh
- [x] Roles (ADMIN, AGENT, CUSTOMER) enforced
- [x] Customer can create tickets
- [x] Agent can assign, update status, resolve, close tickets
- [x] Ticket status transitions validated & enforced
- [x] Comments (with internal flag) work
- [x] Ticket history recorded on every change
- [x] SLA calculated per priority
- [x] Scheduled SLA monitoring runs every 5 min
- [x] Spring events published & listened
- [x] Pagination, filtering, sorting on all list endpoints
- [x] JPA relationships properly configured (lazy/eager)
- [x] N+1 queries identified & eliminated
- [x] Flyway migrations tested & clean
- [x] Unit tests written (80%+ coverage on services)
- [x] Integration tests with Testcontainers
- [x] Docker Compose runs cleanly
- [x] Swagger/OpenAPI fully documented
- [x] README with setup & usage

### Phase 2: Spring AI + RAG + Agents (Week 4-8)
- [x] Ticket classification works (category + confidence)
- [x] Ticket summarization works
- [x] Priority prediction works
- [x] Sentiment detection works
- [x] Suggested response works
- [x] Knowledge documents can be uploaded & stored
- [x] Documents are chunked correctly (512 tokens max)
- [x] Embeddings generated & stored in pgvector
- [x] Vector similarity search works (< 200ms)
- [x] RAG Q&A endpoint works (grounded responses)
- [x] Similar-ticket retrieval works (embedding-based)
- [x] Tool calling implemented (Spring AI native)
- [x] Tool authorization enforced (no privilege escalation)
- [x] Multi-step agent workflow works end-to-end
- [x] Agent respects tool schema & validates input
- [x] All tool calls logged & audited
- [x] Integration tests for AI endpoints
- [x] Error handling for LLM failures (graceful fallback)
- [x] Cost tracking (token usage logged)
- [x] Swagger documents all AI endpoints
- [x] Architecture diagram included
- [x] Demo workflow documented

---

# 24. Lessons Learned Template

Create a `NOTES.md` file in Week 8 to capture:

```markdown
# Development Journal

## What Went Well
- Spring Boot dependency management
- Testcontainers for database testing
- Spring Data JPA derived queries
- Spring AI ease of integration

## What Was Hard
- N+1 problem detection (took 2 days)
- Embedding cost management
- LLM structured output parsing edge cases

## What I'd Do Differently
- Set up SQL logging from Day 1 Week 1
- Pre-plan RAG chunking strategy (don't iterate live)
- Mock LLM in tests earlier

## Time Spent
- Week 1: 40 hrs (auth + schema setup)
- Week 2: 35 hrs (ticket core + tests)
- Week 3: 38 hrs (N+1 fix + events + docker)
- Week 4: 15 hrs (buffer / cleanup)
- Week 5: 30 hrs (Spring AI + prompts)
- Week 6: 38 hrs (RAG + embeddings + pgvector)
- Week 7: 32 hrs (tool calling)
- Week 8: 20 hrs (multi-step + docs + buffer)
- **Total: 248 hrs (~31 hrs/week)**

## Costs
- OpenAI API: ~$180
- Compute (GCP/AWS): $0 (local dev)
- Hosting (future): N/A
```

---

# 25. Final Project Goal

The project tells a coherent story:

```
                    Traditional Enterprise Backend
                           │
                           ▼
                  Spring Boot Monolith
                           │
             ┌─────────────┼─────────────┐
             ▼             ▼             ▼
           REST          JPA          Security
          Controllers   Queries         & Auth
             │             │             │
             └─────────────┼─────────────┘
                           ▼
                    Business Workflows
                   (Tickets, SLA, Events)
                           │
                           ▼
                       Spring AI
                           │
                 ┌─────────┴─────────┐
                 ▼                   ▼
                RAG                Agent
            (Embeddings,         (Tool calling,
             pgvector)        Authorization)
                 │                   │
                 ▼                   ▼
             Source              Multi-step
           Attribution           Workflows
```

**Key positioning**: AI and agents are extensions of a solid Spring backend, not substitutes for backend engineering.

This positions you for:
- **Mid-level Java/Spring roles** (backend engineer)
- **Senior backend roles** (with AI/ML components)
- **AI engineering roles** (with backend foundation)

---

# 26. Launch Checklist (Day 1 of Week 1)

Before writing code:

- [ ] GitHub repo created
- [ ] `.gitignore` (Maven + IDE)
- [ ] Local PostgreSQL running
- [ ] Java 21 verified
- [ ] Docker & Docker Compose working
- [ ] IDE open (IntelliJ recommended)
- [ ] Spring Initializr project generated
  - Group: `com.supportai`
  - Artifact: `support-ai`
  - Dependencies: Spring Web, Spring Data JPA, Spring Security, PostgreSQL Driver, Spring Boot Actuator, Lombok
- [ ] Initial `git commit` pushed
- [ ] `pom.xml` reviewed (no conflicts)
- [ ] `application.properties` stubbed with basic config
- [ ] First test run (`mvn test`)

**You're ready to start Week 1 Day 1.**

---

# 27. Quick Reference: Week Timings

| Week | Focus | P0 Goals | Buffer? |
|------|-------|----------|---------|
| 1 | Auth | JWT + User + DB | No |
| 2 | Ticket Core | CRUD + lifecycle | No |
| 3 | Spring Features | Events + N+1 fix | No |
| 4 | Buffer | Caching + polish | **YES** |
| 5 | Spring AI | Classification + summarization | No |
| 6 | RAG | Embeddings + pgvector + Q&A | No |
| 7 | Agents | Tool calling + workflows | No |
| 8 | Finish | Multi-step + docs + buffer | **YES** |

If behind schedule at any point:
1. Extend current week buffer (don't skip)
2. Cut P2 features
3. Document as future work

---

# END OF IMPROVED PLAN

**Print this document. Reference it daily. Check off deliverables weekly.**

**Good luck. Build something great.** 🚀
