# CLAUDE.md

Guidance for Claude Code when working in this repository.

## What this repo is

A **personal interview-preparation and practice codebase** — not a deployable
product. It is a large, loosely-coupled collection of:

- **DSA solutions** (`src/main/java/code/`) — grouped by technique: `graph/`,
  `dp/`, `greedy/`, `stack/`, `heap/`, `linkedList/`, `backtracking_dp/`, `mix/`, etc.
- **Low-Level Design (LLD)** implementations (`src/main/java/LLD/`) — one folder
  per system (`parkinglot/`, `splitwise/`, `Game/chessGame/`, `BookingApp/BookMyShow/`,
  `machine_hd/ElevatorSystem/`, …). Shared building blocks live in `LLD/util/`
  (`payment/`, `Notification/`, `user/`, `address/`, `repository/`).
- **Design patterns** (`src/main/java/design_pattern/`) — `creational/`,
  `structural/`, `behavioral/`.
- **Custom data-structure implementations** (`src/main/java/DSimpl/`) —
  hand-rolled `HashMapCustom`, `LRUCacheDemo`, `ConcurrentHashMapCustom`, etc.
- **Concurrency examples** (`syncronization/`, `mutlithreading/`).
- **Study notes & interview material** (Markdown/PDF, not code):
  `src/main/topic/` (Java, Spring Boot, Kafka, Spark, AWS, SQL, …),
  `src/main/interviews/`, and `src/main/java/A_interview_experiences/`.
- **SQL practice** (`src/main/sql/`).

Because it is a practice repo, most `.java` files are **self-contained and meant
to be run individually** — they typically declare non-public classes with their
own `main(...)` method (e.g. `Main.java` contains `Test1`, `Test2`, `Task`, …).
There is no single cohesive application flow; do not assume files in a package
depend on or integrate with one another unless the code shows it.

## Build & run

Maven project (`pom.xml`) using the **Spring Boot 3.2.0 parent**, **Java 17**,
with Lombok, Spring Web/Data-JPA, Eureka client, H2, and Apache Spark 3.5.1.

```bash
mvn compile          # compile main sources
mvn test             # run tests
mvn spring-boot:run  # runs the configured main class (Main)
```

**Important build quirks** (see `pom.xml` `maven-compiler-plugin` config):

- The main compile **excludes** test-shaped files and one broken sample:
  `**/*Test.java`, `**/*Tests.java`, `**/Test*.java`, `**/*TestCase.java`, and
  `**/LLD/machine_hd/ATM/**`. Adding a class named `Test*` to main sources will
  be silently skipped by the main compile.
- The Spring Boot plugin's `mainClass` is `Main` (`src/main/java/Main.java`), not
  `PracticeApplication`. Both classes exist; `PracticeApplication` is the
  `@SpringBootApplication` entry point.
- The full main tree **compiles cleanly** under Java 17 with Lombok (verified:
  297 source files → 0 errors, only a few non-fatal `@Builder` warnings). The
  6 excluded files above are the only ones left out of the build.
- Many files are still standalone snippets with their own `main(...)`, so for
  quick iteration it's often easiest to compile/run the **specific file** you're
  working on (IDE run button, or `java <File>.java` for single-file snippets)
  rather than driving the whole Maven build.

## Tooling & environment

- Requires **Lombok** (annotation processing is configured in `pom.xml`); code
  uses `@Data`, `@AllArgsConstructor`, `@SneakyThrows`, `@ToString`, etc.
- Tests use **JUnit 5 (Jupiter)**. Existing tests (e.g.
  `src/test/java/LLD/ATM/ATMServiceImplTest.java`) are largely empty stubs.
- IntelliJ IDEA project (`.idea/`, `practice.iml`) — this is the primary way the
  author runs individual files.

## Conventions

- Package names and folders are informal and sometimes misspelled
  (`syncronization`, `mutlithreading`, `Archetecture`, `RestaurentMgmt`).
  **Match the existing spelling** when referencing or extending them; do not
  "fix" directory/package names as a side effect of another change.
- When adding a new practice solution, follow the existing pattern: place it in
  the topic-appropriate folder, keep it self-contained, and add a `main(...)` if
  it's meant to be demonstrated.
- Reusable LLD primitives belong in `LLD/util/`; check there before writing a new
  payment/notification/user abstraction.

## Git & credentials

This is a **personal** repository, hosted at
`https://github.com/personal-nayyar/practice.git`. Push/pull must use the
personal GitHub account **`personal-nayyar`**, not any work account (e.g.
`mnayyar_paypal`) that may also be logged into `gh` for `github.com`.

The account is pinned per-repo (already configured; recorded here so it can be
restored if the local config is lost):

```bash
# git uses gh's credential helper for github.com...
gh auth setup-git --hostname github.com
# ...and this repo always authenticates as personal-nayyar
git config --local credential.https://github.com.username personal-nayyar
```

With this in place, commits and pushes here use `personal-nayyar` regardless of
which `gh` account is globally active — no `gh auth switch` needed.

## Reference docs

- `DESIGN_DIAGRAMS.md` — architecture/diagram notes.
- `src/main/topic/**` and `src/main/interviews/**` — extensive Markdown study
  guides (Java, Spring Boot, Hibernate, Kafka, Spark, SQL, system design, etc.).
