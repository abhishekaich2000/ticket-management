# Ticket Management

## What is in this project

- **JWT auth** — customers, agents, and admins log in and get a token. APIs are role-based. Admins can register agents.
- **Customer tickets** — create a ticket, list own tickets with filter and sort, add comments.
- **Agent tickets** — assign, change status / priority / category, set SLA due date, comment.
- **History** — ticket changes are stored so you can see what happened.
- **SLA** — if a ticket goes past its due date, it is marked as breached.
- **Auto category (Spring AI)** — on create, the ticket text is sent to a local model (Ollama) to pick a category.
- **Summary (Spring AI)** — a short ticket summary is generated with Spring AI for agents.
- **RAG (Spring AI)** — agents upload PDF, Word, or text help docs. Docs are chunked, embedded, and stored in Postgres (pgvector). Customer chat retrieves matching chunks and answers from them.

## Tech stack

- Java 17, Spring Boot, Maven
- Spring Security + JWT
- PostgreSQL, Flyway, JPA
- RabbitMQ (ticket events)
- Spring AI, Ollama, pgvector
- OpenAPI (springdoc)
