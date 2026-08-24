# Implementacioni Plan — Building Ticket Management System

> Baziran na MVP-PRD.md i ARCHITECTURE.md template-u.
> Stack: Java 21 + Spring Boot 3.2 + MySQL 8 + React 18 + TypeScript + TailwindCSS

---

## Sadrzaj

1. [Baza podataka — ER model i entiteti](#1-baza-podataka--er-model-i-entiteti)
2. [Business pravila i status masina](#2-business-pravila-i-status-masina)
3. [Backend implementacija](#3-backend-implementacija)
   - [3.1 Projekt setup i zavisnosti](#31-projekt-setup-i-zavisnosti)
   - [3.2 Entiteti](#32-entiteti)
   - [3.3 Enumi](#33-enumi)
   - [3.4 DTO-ovi](#34-dto-ovi)
   - [3.5 Repositories](#35-repositories)
   - [3.6 Mappers](#36-mappers)
   - [3.7 Security (JWT)](#37-security-jwt)
   - [3.8 Services](#38-services)
   - [3.9 Controllers i API endpointi](#39-controllers-i-api-endpointi)
   - [3.10 Exception handling](#310-exception-handling)
4. [Frontend implementacija](#4-frontend-implementacija)
   - [4.1 Projekt setup](#41-projekt-setup)
   - [4.2 Folder struktura](#42-folder-struktura)
   - [4.3 TypeScript modeli](#43-typescript-modeli)
   - [4.4 Auth kontekst i JWT](#44-auth-kontekst-i-jwt)
   - [4.5 API servisi](#45-api-servisi)
   - [4.6 Routing i zastita ruta](#46-routing-i-zastita-ruta)
   - [4.7 Stranice i komponente](#47-stranice-i-komponente)
5. [Redosled implementacije](#5-redosled-implementacije)

---

## 1. Baza podataka — ER model i entiteti

### Relacije

```
Building (1) ──── (N) Apartment
Apartment (1) ──── (N) Ticket
User (1) ──── (N) Ticket [kao tenant]
User (1) ──── (N) Ticket [kao manager]
User (1) ──── (N) Ticket [kao technician]
Ticket (1) ──── (N) Comment
Ticket (1) ──── (N) TicketHistory
User (1) ──── (N) Comment
User (1) ──── (N) TicketHistory [changedBy]
```

---

### Tabela: `building`

| Kolona  | Tip          | Ogranicenja       |
|---------|--------------|-------------------|
| id      | BIGINT       | PK, AUTO_INCREMENT|
| name    | VARCHAR(100) | NOT NULL          |
| address | VARCHAR(255) | NOT NULL          |

---

### Tabela: `apartment`

| Kolona      | Tip         | Ogranicenja         |
|-------------|-------------|---------------------|
| id          | BIGINT      | PK, AUTO_INCREMENT  |
| number      | VARCHAR(10) | NOT NULL            |
| floor       | INT         | NOT NULL            |
| building_id | BIGINT      | FK → building(id), NOT NULL |

---

### Tabela: `user`

| Kolona     | Tip          | Ogranicenja              |
|------------|--------------|--------------------------|
| id         | BIGINT       | PK, AUTO_INCREMENT       |
| first_name | VARCHAR(50)  | NOT NULL                 |
| last_name  | VARCHAR(50)  | NOT NULL                 |
| email      | VARCHAR(100) | NOT NULL, UNIQUE         |
| password   | VARCHAR(255) | NOT NULL (BCrypt hash)   |
| role       | ENUM         | NOT NULL: TENANT / MANAGER / TECHNICIAN |

---

### Tabela: `ticket`

| Kolona         | Tip          | Ogranicenja                        |
|----------------|--------------|------------------------------------|
| id             | BIGINT       | PK, AUTO_INCREMENT                 |
| title          | VARCHAR(200) | NOT NULL                           |
| description    | TEXT         | NOT NULL                           |
| status         | ENUM         | NOT NULL (vidi sekciju 2)          |
| priority       | ENUM         | NOT NULL: LOW / MEDIUM / HIGH / URGENT |
| created_at     | DATETIME     | NOT NULL, auto-set pri kreiranju   |
| updated_at     | DATETIME     | NOT NULL, auto-update pri izmeni   |
| tenant_id      | BIGINT       | FK → user(id), NOT NULL            |
| manager_id     | BIGINT       | FK → user(id), NULLABLE            |
| technician_id  | BIGINT       | FK → user(id), NULLABLE            |
| apartment_id   | BIGINT       | FK → apartment(id), NOT NULL       |

---

### Tabela: `comment`

| Kolona     | Tip      | Ogranicenja                     |
|------------|----------|---------------------------------|
| id         | BIGINT   | PK, AUTO_INCREMENT              |
| message    | TEXT     | NOT NULL                        |
| created_at | DATETIME | NOT NULL, auto-set pri kreiranju|
| ticket_id  | BIGINT   | FK → ticket(id), NOT NULL       |
| user_id    | BIGINT   | FK → user(id), NOT NULL         |

---

### Tabela: `ticket_history`

| Kolona      | Tip      | Ogranicenja                            |
|-------------|----------|----------------------------------------|
| id          | BIGINT   | PK, AUTO_INCREMENT                     |
| old_status  | ENUM     | NULLABLE (null = tiket kreiran)        |
| new_status  | ENUM     | NOT NULL                               |
| changed_at  | DATETIME | NOT NULL, auto-set                     |
| ticket_id   | BIGINT   | FK → ticket(id), NOT NULL              |
| changed_by  | BIGINT   | FK → user(id), NOT NULL                |

---

## 2. Business pravila i status masina

### Status masina tiketa

```
            [TENANT kreira]
                  ↓
               OPEN
                  ↓  [MANAGER dodeli tehnicara]
            ASSIGNED
                  ↓  [TECHNICIAN/MANAGER pocne rad]
           IN_PROGRESS
                  ↓  [TECHNICIAN oznaci zavrsetkom]
           COMPLETED
                  ↓  [MANAGER zatvori]
              CLOSED
```

### Ko moze da menja koji status

| Prelaz                         | Ko moze        | Uslov                              |
|--------------------------------|----------------|------------------------------------|
| → OPEN                         | TENANT         | Pri kreiranju tiketa               |
| OPEN → ASSIGNED                | MANAGER        | Mora dodeliti technician_id        |
| ASSIGNED → IN_PROGRESS         | TECHNICIAN     | Samo svoji tiketi                  |
| ASSIGNED → IN_PROGRESS         | MANAGER        | Uvek moze                          |
| IN_PROGRESS → COMPLETED        | TECHNICIAN     | Samo svoji tiketi                  |
| IN_PROGRESS → COMPLETED        | MANAGER        | Uvek moze                          |
| COMPLETED → CLOSED             | MANAGER        | Finalno zatvaranje                 |
| Bilo koji → OPEN               | MANAGER        | Re-open tiket (edge case)          |

### Ostala business pravila

**Tiketi:**
- TENANT moze kreirati tiket samo za posto odabere apartment iz liste
- TENANT vidi samo **svoje** tikete (gde je tenant_id = trenutni korisnik)
- TECHNICIAN vidi samo tikete gde je technician_id = trenutni korisnik
- MANAGER vidi **sve** tikete
- manager_id se automatski setuje na ID trenutno ulogovanog MANAGER-a kada on prvi put dodirne tiket (dodeli tehnicara, promeni prioritet, itd.)
- Brisanje tiketa: nije deo MVP-a — nema DELETE za tikete
- Priority moze menjati samo MANAGER i TENANT (pri kreiranju)

**Korisnici:**
- Svi se mogu registrovati samostalno (TENANT, TECHNICIAN, MANAGER)
- Nema admin odobrenja registracije u MVP-u
- Email mora biti jedinstven
- Password se cuva kao BCrypt hash, nikad plain-text
- TENANT pri registraciji **ne mora** odmah da naveze apartment — to ce biti pri kreiranju tiketa

**Komentari:**
- Svi ulogovani korisnici mogu dodavati komentare na tiket
- TENANT moze komentarisati samo tikete koje je on kreirao
- Komentari se ne brisu u MVP-u
- Komentari se prikazuju hronoloski (created_at ASC)

**TicketHistory:**
- Automatski se kreira svaki put kada se promeni `status` tiketa
- Kreira se i pri inicjalnom kreiranju tiketa (old_status = null, new_status = OPEN)
- Ne moze se brisati ni menjati — read-only
- Biljezi ko je izvrsio promenu

**Apartment / Building:**
- Apartment i Building kreira/menja samo MANAGER
- Listing je dostupan svim ulogovanim korisnicima (tenant treba da odabere apartman)

---

## 3. Backend implementacija

### 3.1 Projekt setup i zavisnosti

**Maven `pom.xml` zavisnosti:**

| Zavisnost | Svrha |
|-----------|-------|
| `spring-boot-starter-web` | REST API |
| `spring-boot-starter-data-jpa` | JPA / Hibernate |
| `spring-boot-starter-security` | Spring Security |
| `mysql-connector-j` | MySQL driver |
| `jjwt-api` + `jjwt-impl` + `jjwt-jackson` | JWT token (io.jsonwebtoken, verzija 0.12.x) |
| `mapstruct` + `mapstruct-processor` | DTO mapping |
| `lombok` + `lombok-mapstruct-binding` | Boilerplate reduction |
| `spring-boot-starter-validation` | Bean Validation (@NotBlank, @Email, itd.) |

**`application.properties` kljucne vrednosti:**
- `spring.datasource.url`, `username`, `password`
- `spring.jpa.hibernate.ddl-auto=update`
- `application.security.jwt.secret-key` (min 256-bit base64 string)
- `application.security.jwt.expiration` (npr. 86400000 = 24h)
- `server.port=8080`

---

### 3.2 Entiteti

Svaki entitet implementuje `DomainEntity` marker interfejs.

#### `Building`
- Polja: `id (Long)`, `name`, `address`
- Relacija: `@OneToMany(mappedBy = "building") List<Apartment>`
- Anotacije: `@Entity`, `@Table(name = "building")`

#### `Apartment`
- Polja: `id (Long)`, `number (String)`, `floor (int)`
- Relacija: `@ManyToOne(fetch = LAZY) Building building`
- FK: `@JoinColumn(name = "building_id")`

#### `User`
- Polja: `id (Long)`, `firstName`, `lastName`, `email`, `password`, `role (Role enum)`
- Implementira `UserDetails` iz Spring Security (za autentifikaciju)
- Metode `UserDetails`: `getUsername()` vraca `email`, `getAuthorities()` vraca `role`

#### `Ticket`
- Polja: `id (Long)`, `title`, `description`, `status (TicketStatus)`, `priority (Priority)`, `createdAt`, `updatedAt`
- Relacije:
  - `@ManyToOne tenant` → User (NOT NULL)
  - `@ManyToOne manager` → User (NULLABLE)
  - `@ManyToOne technician` → User (NULLABLE)
  - `@ManyToOne apartment` → Apartment (NOT NULL)
  - `@OneToMany comments` → Comment
  - `@OneToMany history` → TicketHistory
- `@PrePersist` za `createdAt`, `@PreUpdate` za `updatedAt`

#### `Comment`
- Polja: `id (Long)`, `message (TEXT)`, `createdAt`
- Relacije:
  - `@ManyToOne ticket` → Ticket
  - `@ManyToOne user` → User
- `@PrePersist` za `createdAt`

#### `TicketHistory`
- Polja: `id (Long)`, `oldStatus (TicketStatus, NULLABLE)`, `newStatus (TicketStatus)`, `changedAt`
- Relacije:
  - `@ManyToOne ticket` → Ticket
  - `@ManyToOne changedBy` → User
- `@PrePersist` za `changedAt`

---

### 3.3 Enumi

```
Role:             TENANT, MANAGER, TECHNICIAN

TicketStatus:     OPEN, ASSIGNED, IN_PROGRESS, COMPLETED, CLOSED

Priority:         LOW, MEDIUM, HIGH, URGENT
```

Svi enumi se cuvaju kao `@Enumerated(EnumType.STRING)` u bazi.

---

### 3.4 DTO-ovi

Svi DTO-ovi implementuju `DomainDTO` marker interfejs.

#### Request DTO-ovi (prima backend)

| DTO klasa | Polja | Validacija |
|-----------|-------|------------|
| `RegisterRequest` | firstName, lastName, email, password, role | @NotBlank, @Email, @Size(min=6) |
| `LoginRequest` | email, password | @NotBlank, @Email |
| `CreateTicketRequest` | title, description, priority, apartmentId | @NotBlank, @NotNull |
| `AssignTicketRequest` | ticketId, technicianId | @NotNull |
| `UpdateStatusRequest` | ticketId, newStatus | @NotNull |
| `UpdatePriorityRequest` | ticketId, priority | @NotNull |
| `AddCommentRequest` | ticketId, message | @NotNull, @NotBlank |
| `CreateBuildingRequest` | name, address | @NotBlank |
| `UpdateBuildingRequest` | id, name, address | @NotNull, @NotBlank |
| `CreateApartmentRequest` | number, floor, buildingId | @NotBlank, @NotNull |
| `UpdateApartmentRequest` | id, number, floor, buildingId | @NotNull |

#### Response DTO-ovi (vraca backend)

| DTO klasa | Polja |
|-----------|-------|
| `AuthResponse` | token (JWT String), userId, email, role, firstName, lastName |
| `UserDTO` | id, firstName, lastName, email, role |
| `BuildingDTO` | id, name, address |
| `ApartmentDTO` | id, number, floor, building (BuildingDTO) |
| `TicketDTO` | id, title, description, status, priority, createdAt, updatedAt, tenant (UserDTO), manager (UserDTO nullable), technician (UserDTO nullable), apartment (ApartmentDTO) |
| `CommentDTO` | id, message, createdAt, user (UserDTO) |
| `TicketHistoryDTO` | id, oldStatus, newStatus, changedAt, changedBy (UserDTO) |
| `DashboardStatsDTO` | openCount, assignedCount, inProgressCount, completedCount, closedCount, totalCount |

---

### 3.5 Repositories

Svaki repository extends `JpaRepository<Entity, Long>`.

#### `BuildingRepository`
- `findByNameContainingIgnoreCase(String name)` — pretraga po imenu

#### `ApartmentRepository`
- `findByBuildingId(Long buildingId)` — svi apartmani u zgradi
- `findByBuildingIdAndFloor(Long buildingId, int floor)` — filter

#### `UserRepository`
- `findByEmail(String email)` — za login i UserDetails
- `existsByEmail(String email)` — provera duplikata pri registraciji
- `findByRole(Role role)` — lista svih tehnicara (za assignment)

#### `TicketRepository`
- `findByTenantId(Long tenantId)` — tiketi stanara
- `findByTechnicianId(Long technicianId)` — tiketi tehnicara
- `findByStatus(TicketStatus status)` — filter po statusu
- `findAll()` — za managera (sve)
- `countByStatus(TicketStatus status)` — za dashboard

#### `CommentRepository`
- `findByTicketIdOrderByCreatedAtAsc(Long ticketId)` — komentari za tiket

#### `TicketHistoryRepository`
- `findByTicketIdOrderByChangedAtAsc(Long ticketId)` — istorija tiketa

---

### 3.6 Mappers

Koristi se **MapStruct** sa `componentModel = "spring"`.

| Mapper klasa | Mapira |
|---|---|
| `BuildingMapper` | `Building ↔ BuildingDTO` |
| `ApartmentMapper` | `Apartment ↔ ApartmentDTO`, uses: `BuildingMapper` |
| `UserMapper` | `User ↔ UserDTO` |
| `TicketMapper` | `Ticket → TicketDTO`, uses: `UserMapper`, `ApartmentMapper` |
| `CommentMapper` | `Comment → CommentDTO`, uses: `UserMapper` |
| `TicketHistoryMapper` | `TicketHistory → TicketHistoryDTO`, uses: `UserMapper` |

**Napomene:**
- `TicketMapper` mapira samo `Entity → DTO` (toDomainDTO), jer kreiranje tiketa ide kroz `CreateTicketRequest`, ne `TicketDTO`
- `User` entity implementuje `UserDetails` — `UserMapper` mapira samo relevantna polja, ne password

---

### 3.7 Security (JWT)

#### Klase koje treba kreirati

**`JwtService`** (servisna klasa za JWT operacije):
- `generateToken(User user)` → String JWT
- `extractEmail(String token)` → String
- `isTokenValid(String token, UserDetails userDetails)` → boolean
- `extractExpiration(String token)` → Date
- Token sadrzi claims: `sub` (email), `role`, `userId`
- Potpis: HMAC-SHA256 sa secret key iz `application.properties`

**`JwtAuthenticationFilter`** extends `OncePerRequestFilter`:
- Izvlaci `Authorization: Bearer <token>` header
- Validira token
- Setuje `SecurityContextHolder` sa autentifikacijom korisnika
- Ako nema tokena ili je nevazeci → propusta zahtev, Spring Security odbija ako je ruta zasticena

**`UserDetailsServiceImpl`** implements `UserDetailsService`:
- `loadUserByUsername(String email)` → cita User iz baze po email-u
- Koriste ga Spring Security internali za autentifikaciju

**`SecurityConfig`** (Spring Security konfiguracija):
- `SecurityFilterChain` bean:
  - Dozvoljene rute bez tokena: `POST /auth/register`, `POST /auth/login`
  - Sve ostale rute zahtevaju autentifikaciju
  - Role-based pristup:
    - `GET /tickets/all` → samo MANAGER
    - `POST /tickets/assign` → samo MANAGER
    - `POST /tickets/updateStatus` → MANAGER i TECHNICIAN
    - `GET /tickets/assigned` → samo TECHNICIAN
    - `GET /dashboard/stats` → samo MANAGER
    - `POST /buildings/**` → samo MANAGER (add/update)
    - `POST /apartments/**` → samo MANAGER (add/update)
  - CORS konfiguracija: dozvoli `http://localhost:3000`
  - CSRF: disabled (API, ne web forme)
  - Session: STATELESS
- `PasswordEncoder` bean: `BCryptPasswordEncoder`
- `AuthenticationManager` bean

**`AuthService`** (nije Spring Security klasa, nasa klasa):
- `register(RegisterRequest)` → kreira User, heshuje password, vraca `AuthResponse` sa JWT
- `login(LoginRequest)` → verifikuje kredencijale, vraca `AuthResponse` sa JWT

---

### 3.8 Services

#### `AuthService`
- `register(RegisterRequest request)`:
  - Proveri `existsByEmail` → ako postoji: exception
  - Hash password sa BCrypt
  - Kreiraj i sacuvaj User
  - Generiši JWT token
  - Vrati `AuthResponse`
- `login(LoginRequest request)`:
  - Nadi User po email-u → ako ne postoji: exception
  - Verifikuj password → ako ne odgovara: exception
  - Generiši JWT token
  - Vrati `AuthResponse`

#### `BuildingService`
- `getAll()` → List<BuildingDTO>
- `getBuilding(Long id)` → BuildingDTO (ili exception ako ne postoji)
- `addBuilding(CreateBuildingRequest)` → BuildingDTO
- `updateBuilding(UpdateBuildingRequest)` → BuildingDTO
- `deleteBuilding(Long id)` → ne moze ako ima apartmane (exception sa porukom)

#### `ApartmentService`
- `getAll()` → List<ApartmentDTO>
- `getByBuilding(Long buildingId)` → List<ApartmentDTO>
- `getApartment(Long id)` → ApartmentDTO
- `addApartment(CreateApartmentRequest)` → ApartmentDTO
- `updateApartment(UpdateApartmentRequest)` → ApartmentDTO
- `deleteApartment(Long id)` → ne moze ako ima tikete (exception sa porukom)

#### `UserService`
- `getCurrentUser(String email)` → UserDTO (za /users/me endpoint)
- `getAllTechnicians()` → List<UserDTO> (za manager assignment dropdown)

#### `TicketService`
- `createTicket(CreateTicketRequest, Long tenantId)`:
  - Nadi Apartment → exception ako ne postoji
  - Nadi User (tenant) → kreiraj Ticket
  - Status: OPEN, createdAt: now()
  - Sacuvaj Ticket
  - Kreiraj TicketHistory (oldStatus = null, newStatus = OPEN)
  - Vrati TicketDTO

- `getAllTickets()` → List<TicketDTO> (za managera)

- `getMyTickets(Long tenantId)` → List<TicketDTO>

- `getAssignedTickets(Long technicianId)` → List<TicketDTO>

- `getTicket(Long ticketId, Long userId, Role role)` → TicketDTO:
  - MANAGER → uvek moze videti
  - TENANT → samo ako je on tenant tog tiketa
  - TECHNICIAN → samo ako je on technician tog tiketa

- `assignTechnician(AssignTicketRequest, Long managerId)`:
  - Nadi Ticket → exception ako ne postoji
  - Nadi User (technician) → exception ako nije TECHNICIAN role
  - Setuj `technician`, `manager` (tekuci manager), `status = ASSIGNED`
  - Sacuvaj
  - Kreiraj TicketHistory (OPEN → ASSIGNED)
  - Vrati TicketDTO

- `updateStatus(UpdateStatusRequest, Long userId, Role role)`:
  - Nadi Ticket
  - Validacija tranzicije po tabeli iz sekcije 2
  - Ako je nevalidna tranzicija → exception sa porukom
  - Azuriraj `status`, `updatedAt`
  - Kreiraj TicketHistory
  - Vrati TicketDTO

- `updatePriority(UpdatePriorityRequest, Long managerId)`:
  - Nadi Ticket → proveri postoji
  - Azuriraj `priority`, `updatedAt`
  - Vrati TicketDTO

#### `CommentService`
- `getCommentsByTicket(Long ticketId, Long userId, Role role)`:
  - Proveri da korisnik ima pravo videti tiket (ista logika kao getTicket)
  - Vrati List<CommentDTO>
- `addComment(AddCommentRequest, Long userId)`:
  - Nadi Ticket i User
  - Proveri da li korisnik ima pristup tiketu
  - Kreiraj i sacuvaj Comment
  - Vrati CommentDTO

#### `TicketHistoryService`
- `getHistoryByTicket(Long ticketId)` → List<TicketHistoryDTO>
- `createHistoryEntry(Ticket, User changedBy, TicketStatus oldStatus, TicketStatus newStatus)` → internal metoda, poziva se iz TicketService

#### `DashboardService`
- `getStats()` → DashboardStatsDTO:
  - `countByStatus` za svaki status
  - `totalCount` = suma svih

---

### 3.9 Controllers i API endpointi

Svi controlleri:
- `@RestController`
- `@RequestMapping("/{resource}")`
- `@CrossOrigin("http://localhost:3000")`
- Primaju trenutnog korisnika iz JWT-a preko `@AuthenticationPrincipal UserDetails userDetails`

---

#### `AuthController` — `/auth`

| Metoda | Endpoint | Body | Ko | Odgovor |
|--------|----------|------|----|---------|
| POST | `/auth/register` | RegisterRequest | Svi (javno) | AuthResponse |
| POST | `/auth/login` | LoginRequest | Svi (javno) | AuthResponse |

---

#### `UserController` — `/users`

| Metoda | Endpoint | Ko | Odgovor |
|--------|----------|----|---------|
| GET | `/users/me` | Svi ulogovani | UserDTO (tekuci korisnik) |
| GET | `/users/technicians` | MANAGER | List<UserDTO> svih tehnicara |

---

#### `BuildingController` — `/buildings`

| Metoda | Endpoint | Body | Ko | Odgovor |
|--------|----------|------|----|---------|
| GET | `/buildings/all` | — | Svi ulogovani | List<BuildingDTO> |
| GET | `/buildings/{id}` | — | Svi ulogovani | BuildingDTO |
| POST | `/buildings/add` | CreateBuildingRequest | MANAGER | BuildingDTO |
| POST | `/buildings/update` | UpdateBuildingRequest | MANAGER | BuildingDTO |
| POST | `/buildings/delete/{id}` | — | MANAGER | poruka |

---

#### `ApartmentController` — `/apartments`

| Metoda | Endpoint | Body | Ko | Odgovor |
|--------|----------|------|----|---------|
| GET | `/apartments/all` | — | Svi ulogovani | List<ApartmentDTO> |
| GET | `/apartments/byBuilding/{buildingId}` | — | Svi ulogovani | List<ApartmentDTO> |
| GET | `/apartments/{id}` | — | Svi ulogovani | ApartmentDTO |
| POST | `/apartments/add` | CreateApartmentRequest | MANAGER | ApartmentDTO |
| POST | `/apartments/update` | UpdateApartmentRequest | MANAGER | ApartmentDTO |
| POST | `/apartments/delete/{id}` | — | MANAGER | poruka |

---

#### `TicketController` — `/tickets`

| Metoda | Endpoint | Body | Ko | Odgovor |
|--------|----------|------|----|---------|
| POST | `/tickets/create` | CreateTicketRequest | TENANT | TicketDTO |
| GET | `/tickets/all` | — | MANAGER | List<TicketDTO> |
| GET | `/tickets/my` | — | TENANT | List<TicketDTO> |
| GET | `/tickets/assigned` | — | TECHNICIAN | List<TicketDTO> |
| GET | `/tickets/{id}` | — | Svi ulogovani (provera prava) | TicketDTO |
| POST | `/tickets/assign` | AssignTicketRequest | MANAGER | TicketDTO |
| POST | `/tickets/updateStatus` | UpdateStatusRequest | MANAGER, TECHNICIAN | TicketDTO |
| POST | `/tickets/updatePriority` | UpdatePriorityRequest | MANAGER | TicketDTO |

---

#### `CommentController` — `/comments`

| Metoda | Endpoint | Body | Ko | Odgovor |
|--------|----------|------|----|---------|
| GET | `/comments/byTicket/{ticketId}` | — | Svi ulogovani (provera prava) | List<CommentDTO> |
| POST | `/comments/add` | AddCommentRequest | Svi ulogovani (provera prava) | CommentDTO |

---

#### `TicketHistoryController` — `/ticket-history`

| Metoda | Endpoint | Ko | Odgovor |
|--------|----------|----|---------|
| GET | `/ticket-history/byTicket/{ticketId}` | Svi ulogovani (provera prava) | List<TicketHistoryDTO> |

---

#### `DashboardController` — `/dashboard`

| Metoda | Endpoint | Ko | Odgovor |
|--------|----------|----|---------|
| GET | `/dashboard/stats` | MANAGER | DashboardStatsDTO |

---

### 3.10 Exception handling

#### Globalni exception handler (`@ControllerAdvice`)

Klasa: `GlobalExceptionHandler`

| Exception klasa | HTTP status | Kada se baca |
|-----------------|-------------|--------------|
| `ResourceNotFoundException` | 404 Not Found | Entity ne postoji u bazi |
| `AccessDeniedException` | 403 Forbidden | Korisnik nema pravo pristupa |
| `InvalidStatusTransitionException` | 400 Bad Request | Nevalidna tranzicija statusa |
| `EmailAlreadyExistsException` | 409 Conflict | Email vec postoji pri registraciji |
| `InvalidCredentialsException` | 401 Unauthorized | Pogresni login kredencijali |
| `MethodArgumentNotValidException` | 400 Bad Request | Bean Validation greska (@NotBlank, itd.) |
| `RuntimeException` (fallback) | 500 Internal Server Error | Neocekivana greska |

Svaki response greske ima uniforman format:
```json
{
  "message": "Ticket not found with id: 42",
  "status": "NOT_FOUND"
}
```

---

## 4. Frontend implementacija

### 4.1 Projekt setup

- **Create project**: `npm create vite@latest -- --template react-ts`
- **Zavisnosti**: `axios`, `react-router-dom`, `recharts`, `tailwindcss`, `@headlessui/react` (opciono za modalni)
- **Dev zavisnosti**: `@types/react`, `@types/react-router-dom`, `tailwindcss`, `postcss`, `autoprefixer`

---

### 4.2 Folder struktura

```
src/
├── api/                    ← Axios servisi po domenskoj oblasti
│   ├── authService.ts
│   ├── buildingService.ts
│   ├── apartmentService.ts
│   ├── ticketService.ts
│   ├── commentService.ts
│   ├── historyService.ts
│   ├── dashboardService.ts
│   └── userService.ts
│
├── types/                  ← TypeScript interfejsi (odgovaraju backend DTO-ovima)
│   ├── auth.types.ts
│   ├── user.types.ts
│   ├── building.types.ts
│   ├── apartment.types.ts
│   ├── ticket.types.ts
│   ├── comment.types.ts
│   ├── history.types.ts
│   ├── dashboard.types.ts
│   └── api.types.ts        ← ApiResponse wrapper tip
│
├── context/
│   └── AuthContext.tsx      ← JWT storage, trenutni korisnik, login/logout
│
├── hooks/
│   ├── useAuth.ts           ← Pristup AuthContext-u
│   └── useTickets.ts        ← Opcionalni custom hook za ucitavanje tiketa
│
├── routes/
│   ├── PrivateRoute.tsx     ← Zastita ruta po autentifikaciji
│   └── RoleRoute.tsx        ← Zastita ruta po roli
│
├── pages/
│   ├── LoginPage.tsx
│   ├── RegisterPage.tsx
│   ├── DashboardPage.tsx       ← MANAGER
│   ├── TicketListPage.tsx      ← Role-based (svako vidi svoje)
│   ├── TicketDetailPage.tsx    ← Detalji tiketa, komentari, istorija
│   ├── CreateTicketPage.tsx    ← TENANT
│   ├── BuildingsPage.tsx       ← MANAGER
│   └── NotFoundPage.tsx
│
├── components/             ← Visestruko koriscene komponente
│   ├── Navbar.tsx
│   ├── TicketCard.tsx
│   ├── TicketStatusBadge.tsx
│   ├── PriorityBadge.tsx
│   ├── CommentList.tsx
│   ├── CommentForm.tsx
│   ├── TicketHistoryList.tsx
│   ├── AssignTechnicianModal.tsx
│   ├── DashboardChart.tsx       ← Recharts bar/pie chart
│   └── LoadingSpinner.tsx
│
├── config/
│   └── api.ts              ← API_BASE_URL = "http://localhost:8080"
│
└── App.tsx                 ← Router definicija
```

---

### 4.3 TypeScript modeli

#### `api.types.ts`
```
ApiResponse<T>:
  message: string
  data?: Record<string, T>
```

#### `auth.types.ts`
```
RegisterRequest: firstName, lastName, email, password, role
LoginRequest: email, password
AuthResponse: token, userId, email, role, firstName, lastName
```

#### `user.types.ts`
```
UserDTO: id, firstName, lastName, email, role
Role: "TENANT" | "MANAGER" | "TECHNICIAN"
```

#### `building.types.ts`
```
BuildingDTO: id, name, address
```

#### `apartment.types.ts`
```
ApartmentDTO: id, number, floor, building: BuildingDTO
```

#### `ticket.types.ts`
```
TicketStatus: "OPEN" | "ASSIGNED" | "IN_PROGRESS" | "COMPLETED" | "CLOSED"
Priority: "LOW" | "MEDIUM" | "HIGH" | "URGENT"

TicketDTO:
  id, title, description
  status: TicketStatus
  priority: Priority
  createdAt: string (ISO datetime)
  updatedAt: string
  tenant: UserDTO
  manager?: UserDTO
  technician?: UserDTO
  apartment: ApartmentDTO

CreateTicketRequest: title, description, priority, apartmentId
AssignTicketRequest: ticketId, technicianId
UpdateStatusRequest: ticketId, newStatus
UpdatePriorityRequest: ticketId, priority
```

#### `comment.types.ts`
```
CommentDTO: id, message, createdAt, user: UserDTO
AddCommentRequest: ticketId, message
```

#### `history.types.ts`
```
TicketHistoryDTO: id, oldStatus?, newStatus, changedAt, changedBy: UserDTO
```

#### `dashboard.types.ts`
```
DashboardStatsDTO: openCount, assignedCount, inProgressCount, completedCount, closedCount, totalCount
```

---

### 4.4 Auth kontekst i JWT

#### `AuthContext.tsx`

State koji cuva:
- `token: string | null` — JWT token
- `user: AuthResponse | null` — podaci o ulogovanom korisniku (id, email, role, ime)
- `isAuthenticated: boolean`

Metode:
- `login(AuthResponse)` → cuva token u `localStorage`, setuje user state
- `logout()` → brise localStorage, resetuje state
- `isRole(role: Role)` → provera role

#### Axios interceptor (u `api.ts` ili posebnom fajlu)

- Request interceptor: dodaj `Authorization: Bearer <token>` header automatski na sve zahteve
- Response interceptor: ako 401 → pozovi `logout()` i redirect na `/login`

---

### 4.5 API servisi

Svaki servis je objekat sa metodama koje zovu Axios.

#### `authService.ts`
- `register(data: RegisterRequest)` → `POST /auth/register` → `AuthResponse`
- `login(data: LoginRequest)` → `POST /auth/login` → `AuthResponse`

#### `ticketService.ts`
- `createTicket(data: CreateTicketRequest)` → `POST /tickets/create` → `TicketDTO`
- `getAllTickets()` → `GET /tickets/all` → `TicketDTO[]`
- `getMyTickets()` → `GET /tickets/my` → `TicketDTO[]`
- `getAssignedTickets()` → `GET /tickets/assigned` → `TicketDTO[]`
- `getTicket(id: number)` → `GET /tickets/{id}` → `TicketDTO`
- `assignTechnician(data: AssignTicketRequest)` → `POST /tickets/assign` → `TicketDTO`
- `updateStatus(data: UpdateStatusRequest)` → `POST /tickets/updateStatus` → `TicketDTO`
- `updatePriority(data: UpdatePriorityRequest)` → `POST /tickets/updatePriority` → `TicketDTO`

#### `commentService.ts`
- `getComments(ticketId: number)` → `GET /comments/byTicket/{id}` → `CommentDTO[]`
- `addComment(data: AddCommentRequest)` → `POST /comments/add` → `CommentDTO`

#### `historyService.ts`
- `getHistory(ticketId: number)` → `GET /ticket-history/byTicket/{id}` → `TicketHistoryDTO[]`

#### `dashboardService.ts`
- `getStats()` → `GET /dashboard/stats` → `DashboardStatsDTO`

#### `userService.ts`
- `getMe()` → `GET /users/me` → `UserDTO`
- `getTechnicians()` → `GET /users/technicians` → `UserDTO[]`

#### `buildingService.ts`
- `getAll()` → `GET /buildings/all` → `BuildingDTO[]`
- `addBuilding(data)` → `POST /buildings/add` → `BuildingDTO`
- `updateBuilding(data)` → `POST /buildings/update` → `BuildingDTO`
- `deleteBuilding(id)` → `POST /buildings/delete/{id}`

#### `apartmentService.ts`
- `getAll()` → `GET /apartments/all` → `ApartmentDTO[]`
- `getByBuilding(buildingId)` → `GET /apartments/byBuilding/{id}` → `ApartmentDTO[]`
- `addApartment(data)` → `POST /apartments/add` → `ApartmentDTO`
- `updateApartment(data)` → `POST /apartments/update` → `ApartmentDTO`
- `deleteApartment(id)` → `POST /apartments/delete/{id}`

---

### 4.6 Routing i zastita ruta

#### `PrivateRoute.tsx`
- Proveri `isAuthenticated` iz AuthContext
- Ako nije — redirect na `/login`
- Ako jeste — prikazi `<Outlet />`

#### `RoleRoute.tsx`
- Prima `allowedRoles: Role[]` prop
- Proveri da `user.role` postoji u `allowedRoles`
- Ako ne — redirect na `/unauthorized` ili `dashboard`

#### Rute u `App.tsx`

| Ruta | Komponenta | Zastita |
|------|-----------|---------|
| `/login` | LoginPage | Javno |
| `/register` | RegisterPage | Javno |
| `/dashboard` | DashboardPage | MANAGER |
| `/tickets` | TicketListPage | TENANT, MANAGER, TECHNICIAN |
| `/tickets/create` | CreateTicketPage | TENANT |
| `/tickets/:id` | TicketDetailPage | TENANT, MANAGER, TECHNICIAN |
| `/buildings` | BuildingsPage | MANAGER |
| `*` | NotFoundPage | — |

Default redirect:
- Neautentifikovani → `/login`
- MANAGER → `/dashboard`
- TENANT → `/tickets`
- TECHNICIAN → `/tickets`

---

### 4.7 Stranice i komponente

#### `LoginPage`
- Forma: email, password
- Submit → `authService.login` → sacuvaj u AuthContext → redirect po roli
- Link na RegisterPage

#### `RegisterPage`
- Forma: firstName, lastName, email, password, role (select: TENANT / MANAGER / TECHNICIAN)
- Submit → `authService.register` → sacuvaj u AuthContext → redirect po roli

#### `DashboardPage` (MANAGER)
- Ucitaj stats sa `dashboardService.getStats()`
- Prikazi 5 kartica: OPEN, ASSIGNED, IN_PROGRESS, COMPLETED, CLOSED (sa brojacima)
- Recharts: `BarChart` ili `PieChart` za vizualizaciju raspodele tiketa

#### `TicketListPage`
- Role-based ucitavanje:
  - MANAGER → `getAllTickets()`
  - TENANT → `getMyTickets()`
  - TECHNICIAN → `getAssignedTickets()`
- Prikaz: lista `TicketCard` komponenti
- Filter: po statusu, prioritetu (dropdown)
- Klik na karticu → `/tickets/:id`
- TENANT: dugme "Kreiraj tiket" → `/tickets/create`

#### `TicketCard` komponenta
- Prikazi: title, status badge, priority badge, datum, apartment info
- MANAGER: prikazi tenant ime i technician ime (ako dodeljen)

#### `TicketStatusBadge` komponenta
- Boja po statusu: OPEN (plava), ASSIGNED (zuta), IN_PROGRESS (narandzasta), COMPLETED (zelena), CLOSED (siva)

#### `PriorityBadge` komponenta
- Boja po prioritetu: LOW (zelena), MEDIUM (zuta), HIGH (narandzasta), URGENT (crvena)

#### `CreateTicketPage` (TENANT)
- Forma: title, description, priority (select), buildingId (select), apartmentId (select — zavisi od izabranog buildinga)
- Logika: pri promeni buildingId → ucitaj apartmane za taj building → popuni apartmentId select
- Submit → `ticketService.createTicket` → redirect na `/tickets`

#### `TicketDetailPage`
- Ucitaj tiket, komentare, istoriju na mount
- Prikazi osnovne info: title, description, status, priority, apartman, zgrada, datumi, ucesnici
- **Sekcija akcija (role-based)**:
  - MANAGER: "Dodeli tehnicara" dugme → otvori `AssignTechnicianModal`
  - MANAGER: "Promeni prioritet" dropdown
  - MANAGER: "Promeni status" dropdown (po dozvoljenoj tranziciji)
  - TECHNICIAN: "Promeni status" dropdown (samo dozvoljene tranzicije)
  - TENANT: nema akcija
- **Sekcija komentara**: `CommentList` + `CommentForm`
- **Sekcija istorije**: `TicketHistoryList`

#### `AssignTechnicianModal` komponenta
- Ucitaj listu tehnicara (`userService.getTechnicians()`)
- Dropdown: odaberi tehnicara
- Confirm → `ticketService.assignTechnician` → refresh tiketa

#### `CommentList` komponenta
- Prikazi komentare sa avatarima/inicijalima, imenom korisnika, datumom, porukom
- Hronoloski redosled (najstariji gore)

#### `CommentForm` komponenta
- Textarea za poruku
- Submit → `commentService.addComment` → refresh komentara

#### `TicketHistoryList` komponenta
- Timeline prikaz: svaka promena statusa (ko, kada, stari → novi status)

#### `BuildingsPage` (MANAGER)
- Lista zgrada sa apartmanima
- Forma za dodavanje zgrade i apartmana
- CRUD operacije

#### `Navbar`
- Logo / ime aplikacije
- Navigacioni linkovi (role-based)
- "Odjavi se" dugme → `logout()` + redirect na `/login`

---

## 5. Redosled implementacije

### Faza 1 — Backend osnova (bez Security)

1. Maven projekt setup sa svim zavisnostima
2. `application.properties` sa DB konfiguracionom
3. Svi enumi (`Role`, `TicketStatus`, `Priority`)
4. Svi entiteti sa JPA anotacijama
5. Svi repositories
6. Svi MapStruct mappers
7. Svi DTO-ovi (bez validacije za sad)
8. `BuildingService` + `BuildingController` → testirati sa Postman
9. `ApartmentService` + `ApartmentController` → testirati
10. `UserService` (samo `getAllTechnicians`) → testirati

### Faza 2 — Security i Auth

11. `UserDetailsServiceImpl`
12. `JwtService`
13. `JwtAuthenticationFilter`
14. `SecurityConfig`
15. `AuthService` (register + login)
16. `AuthController`
17. Testirati register/login → dobiti JWT token
18. Testirati zasticene endpointe sa tokenom

### Faza 3 — Tiketi i logika

19. `TicketService` (sve metode) + `TicketController`
20. `CommentService` + `CommentController`
21. `TicketHistoryService` (automatsko kreiranje pri promeni statusa)
22. `TicketHistoryController`
23. `DashboardService` + `DashboardController`
24. Bean Validation na svim Request DTO-ovima
25. `GlobalExceptionHandler`
26. Kompletno testiranje svih endpointa sa Postman

### Faza 4 — Frontend osnova

27. React + TypeScript + Vite + TailwindCSS setup
28. Svi TypeScript tipovi
29. Axios konfiguracija (`api.ts`)
30. `AuthContext` + JWT localStorage
31. `authService` (register, login)
32. `LoginPage` + `RegisterPage`
33. `PrivateRoute` + `RoleRoute`
34. `App.tsx` routing

### Faza 5 — Frontend funkcionalnosti

35. `Navbar`
36. `ticketService` (sve metode) + `TicketListPage`
37. `TicketCard`, `TicketStatusBadge`, `PriorityBadge`
38. `CreateTicketPage` (sa dinamickim apartment dropdown-om)
39. `TicketDetailPage` — osnova (prikaz tiketa)
40. `CommentList` + `CommentForm` → integracija u TicketDetail
41. `TicketHistoryList` → integracija u TicketDetail
42. `AssignTechnicianModal` → integracija u TicketDetail (MANAGER)
43. Status i priority update akcije u TicketDetail
44. `DashboardPage` sa Recharts grafom
45. `BuildingsPage` (MANAGER, opciono za MVP)

### Faza 6 — Polishing

46. Error handling u svim API pozivima (try/catch + poruke korisniku)
47. Loading stanja (spinner komponenta)
48. Forma validacija (required, min length, itd.)
49. Responsive design sa TailwindCSS
50. Kompletno end-to-end testiranje svih user story-ja
