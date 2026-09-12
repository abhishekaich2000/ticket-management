# SupportHub — Phase 1, Week 1

Persistence layer for the support desk monolith. No controllers this week: the goal
is a schema you trust and entities that match it.

Stack: Java 25, Spring Boot 4.1, Postgres 17 (pgvector image), Flyway, Testcontainers.

## Running it

```bash
docker compose up -d
./mvnw test
```

One-time speedup, so the Postgres container survives between test runs:

```bash
echo 'testcontainers.reuse.enable=true' >> ~/.testcontainers.properties
```

To confirm the app boots against the schema (nothing to see yet — no web layer):

```bash
./mvnw spring-boot:run
```

If it starts, `ddl-auto: validate` has already proved every entity matches what
Flyway created. That is the cheapest schema test you will ever get.

## Expect to fix the test imports

Spring Boot 4 split the framework into per-module packages and renamed most
starters, so a few imports here may not resolve against 4.1 exactly as written —
most likely `@DataJpaTest` and `TestEntityManager`, which may now live under a
`spring-boot-data-jpa-test` module rather than `spring-boot-test-autoconfigure`.
Let your IDE resolve them and add the matching test starter companion.

Rather than trust this `pom.xml`, generate a baseline from
[start.spring.io](https://start.spring.io) with Boot 4.1 + JPA + PostgreSQL +
Flyway + Testcontainers and diff it against this one. Initializr is the source of
truth for starter names; a hand-written pom is not.

Two other Boot 4 notes worth knowing before you hit them:

- If you see `ConnectionDetailsFactoryNotFoundException` from `ContainerConfig`,
  add an explicit name: `@ServiceConnection(name = "postgresql")`.
- Testcontainers 2.0 renamed every module to a `testcontainers-` prefix and moved
  container classes into module packages, hence
  `org.testcontainers.postgresql.PostgreSQLContainer`. Old tutorials will show
  `org.testcontainers.containers.PostgreSQLContainer`.

## What is here

```
src/main/java/com/supporthub/
├── common/          BaseEntity (identity + auditing), Priority
├── security/        AppUser, Role          — authentication identity
├── customer/        Customer, Plan         — business profile
├── agent/           Agent
├── sla/             SlaPolicy
├── ticket/          Ticket aggregate: TicketComment, TicketEvent, Tag, enums
└── knowledge/       KnowledgeArticle

src/main/resources/db/migration/
├── V1__initial_schema.sql     the whole schema, hand-written
└── V2__reference_data.sql     SLA policies and tags

src/test/java/com/supporthub/
├── ArchitectureTest.java      the rules that make phase 2 cheap
├── support/                   ContainerConfig, AbstractDataJpaTest, SchemaMigrationTest
└── ticket/TicketRepositoryTest.java
```

## Day by day

**Day 1 — schema.** Read `V1__initial_schema.sql` top to bottom before touching
Java. Then, in psql, run `\d ticket` and `\di` and make sure you can explain every
constraint and every index. Delete one index and predict what slows down.

**Day 2 — identity and profiles.** `BaseEntity`, `AppUser`, `Customer`, `Agent`.
Read the class comment on `BaseEntity` about equals/hashCode carefully — then prove
it by putting two unsaved entities in a `HashSet` with the id assignment removed.

**Day 3 — the ticket aggregate.** `Ticket`, `TicketComment`, `TicketEvent`, `Tag`.
This is the substantive day. The two mapping rules in the `Ticket` javadoc are the
most important thing in the whole week.

**Day 4 — repositories and the happy-path test.** Get
`savesTicketWithCommentsAndTags` green. Watch the SQL in the log while it runs.

**Day 5 — the tests that teach.** The lazy-loading, versioning, cascade and
reference-generation tests. Each one exists because the behaviour it checks
surprises people.

**Day 6 — architecture rules.** Get `ArchitectureTest` green and understand why
each rule is worded the way it is.

## Exercises — do these, they are where the learning is

1. **Break validate.** Add a field to `Ticket` without a migration. Read the
   startup failure. This is the guardrail that replaces `ddl-auto: update`.
2. **Write an N+1.** Load all tickets and loop calling `getComments()`. Count the
   queries in the log. Then fix it with `@EntityGraph` and count again.
3. **Make EAGER hurt.** Change `TicketComment.ticket` to `FetchType.EAGER`, run the
   suite, and look at what happens to the query count.
4. **Ordinal enums.** Switch `TicketStatus` to `EnumType.ORDINAL`, save a ticket,
   reorder the enum constants, and read the row back. Then put it back to STRING.
5. **Break the back-reference.** Construct a `TicketComment` directly and add it to
   the list without `addComment`. Flush, clear, reload. Explain what you see.
6. **Detached collection.** Call `getComments()` outside a transaction on an entity
   loaded in one. With `open-in-view: false` you get `LazyInitializationException`.
   Turn `open-in-view` on and watch the bug disappear — then understand why hiding
   it is worse than fixing it.
7. **Timezone.** Store a ticket, then query it from a psql session with
   `SET timezone = 'Asia/Kolkata'`. Confirm `timestamptz` plus `Instant` gives you
   the same instant regardless.
8. **Cartesian product.** Add `"tags"` to the `@EntityGraph` on
   `findWithDetailsById` and run `savesTicketWithCommentsAndTags`. Three comments
   and two tags come back as six comments. Read the generated SQL and work out why,
   then change `comments` from `List` to `Set` and see what changes. This is the
   single most common JPA performance "fix" that quietly corrupts results.
9. **Paging with a fetch.** Put `@EntityGraph(attributePaths = {"tags"})` back on
   `findByStatusIn`, save 50 tickets, and request page 2 with size 10. Find
   Hibernate's warning in the log about applying the limit in memory, then implement
   the two-query fix: page the ids, then fetch the collections for those ids.

## Done when

- [ ] `docker compose up -d && ./mvnw test` passes from a clean clone
- [ ] `./mvnw spring-boot:run` starts with `ddl-auto: validate` and no drift
- [ ] Flyway runs from an empty database to current with no manual steps
- [ ] All six tests in `TicketRepositoryTest` pass
- [ ] `ArchitectureTest` passes with no suppressions
- [ ] You can explain, without looking: why `Ticket` holds `customerId` instead of
      a `Customer`; why `BaseEntity` assigns its own id; why `open-in-view` is off;
      why `Priority` lives in `common`
- [ ] `DECISIONS.md` has an entry for every exercise above that changed your mind

## Deliberately absent

No controllers, no DTOs, no security, no service layer. Adding any of them this
week means writing them twice, because week 2 changes what the API needs to look
like. `BACKLOG.md` is where ideas go to wait.
