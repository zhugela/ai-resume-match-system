# ai-resume-match-system · Resume ↔ JD Matching

> Upload a resume, get a structured profile and a multi-dimensional match score against a job description — with concrete improvement suggestions.

![Java](https://img.shields.io/badge/Java-Spring%20Boot%203-orange?logo=springboot&logoColor=white)
![Vue](https://img.shields.io/badge/Vue-3-42b883?logo=vuedotjs&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1)
![Redis](https://img.shields.io/badge/Redis-DC382D?logo=redis&logoColor=white)

---

## What it does

1. **Parse** — extract structured fields from an uploaded resume (skills, experience, education)
2. **Match** — compare the parsed profile against a job description across several dimensions
3. **Score** — produce a match percentage per dimension plus an overall score
4. **Advise** — list the gaps that cost the most points, in priority order

## Stack

| Layer | Choice |
|---|---|
| Backend | Spring Boot 3.2, Spring Security, Spring AOP |
| Persistence | MyBatis-Plus, MySQL 8 |
| Cache / session | Redis |
| Auth | JWT |
| Frontend | Vue 3 |
| Build | Maven |

## Design notes

- **Structured extraction first, scoring second.** Scoring raw text is unstable; normalising the resume into a fixed schema makes the comparison deterministic and explainable.
- **Explainable scores.** Every dimension reports which evidence produced the number, so a low score can be acted on rather than merely observed.
- **Security from the start.** JWT + Spring Security guard the endpoints; uploads are validated by type and size before parsing.

## Layout

```
src/main/java/    controllers, services, mappers, security, AI client
sql/              schema
web/              Vue 3 frontend
*.md              project guide, quick start, startup notes
```

## Status

Private project, functional end to end for the core parse → match → score path; frontend polish and evaluation are ongoing.
