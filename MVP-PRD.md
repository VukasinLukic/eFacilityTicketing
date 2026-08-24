# Product Requirements Document (PRD)

## Product Name

Building Ticket Management System

---

## 1. Product Overview

Building Ticket Management System je web aplikacija za prijavu, praćenje i rešavanje kvarova u stambenim zgradama.

Sistem omogućava stanarima da prijavljuju kvarove, menadžerima zgrada da upravljaju prijavama, a tehničarima da evidentiraju rad i rešavaju probleme.

Cilj sistema je centralizacija procesa održavanja zgrade i transparentna komunikacija između svih učesnika.

---

## 2. Problem Statement

U većini stambenih objekata prijava kvarova se vrši telefonom, e-mailom ili preko Viber grupa.

Takav proces dovodi do:

* gubitka informacija
* nejasne odgovornosti
* sporog rešavanja problema
* nemogućnosti praćenja statusa kvara

Potrebno je rešenje koje omogućava jednostavno evidentiranje i praćenje kvarova od prijave do zatvaranja.

---

## 3. Target Users

### Tenant (Stanar)

Prijavljuje kvarove i prati status prijava.

### Manager (Menadžer zgrade)

Upravlja svim tiketima i dodeljuje zadatke tehničarima.

### Technician (Tehničar)

Rešava dodeljene kvarove i ažurira status rada.

---

## 4. MVP Scope

### Authentication

Korisnik može:

* registracija
* prijava
* odjava

Role:

* TENANT
* MANAGER
* TECHNICIAN

JWT autentifikacija.

---

### Ticket Management

Tenant može:

* kreirati tiket
* pregledati svoje tikete

Polja:

* title
* description
* priority

---

### Ticket Assignment

Manager može:

* pregledati sve tikete
* dodeliti tehničara
* promeniti prioritet

---

### Ticket Workflow

Statusi:

* OPEN
* ASSIGNED
* IN_PROGRESS
* COMPLETED
* CLOSED

Tehničar može menjati status tiketa.

---

### Comments

Svi učesnici mogu ostavljati komentare na tiketu.

---

### Dashboard

Manager Dashboard:

* broj otvorenih tiketa
* broj tiketa u radu
* broj zatvorenih tiketa

---

## 5. Out of Scope (Post-MVP)

Sledeće funkcionalnosti nisu deo prve verzije:

* WebSocket notifikacije
* SLA sistem
* AI klasifikacija kvarova
* Push notifikacije
* Mobile aplikacija
* Drag & Drop Kanban
* Upload više slika
* Evidencija radnih sati

---

## 6. User Stories

### Tenant

Kao stanar želim da prijavim kvar kako bi menadžer bio obavešten.

### Tenant

Kao stanar želim da vidim status svog tiketa kako bih znao da li je problem rešen.

### Manager

Kao menadžer želim da dodelim tehničara tiketu kako bi kvar bio otklonjen.

### Technician

Kao tehničar želim da menjam status tiketa tokom rada.

### Manager

Kao menadžer želim da zatvorim tiket nakon uspešne popravke.

---

## 7. Functional Requirements

### FR-01

Sistem mora omogućiti registraciju korisnika.

### FR-02

Sistem mora omogućiti JWT prijavu.

### FR-03

Tenant može kreirati tiket.

### FR-04

Manager vidi sve tikete.

### FR-05

Manager može dodeliti tehničara.

### FR-06

Technician vidi samo svoje tikete.

### FR-07

Technician može promeniti status.

### FR-08

Sistem čuva istoriju promena statusa.

### FR-09

Korisnici mogu ostavljati komentare.

---

## 8. Database Entities

Building

* id
* name
* address

Apartment

* id
* number
* floor
* building_id

User

* id
* first_name
* last_name
* email
* password
* role

Ticket

* id
* title
* description
* status
* priority
* created_at
* updated_at
* tenant_id
* manager_id
* technician_id
* apartment_id

Comment

* id
* ticket_id
* user_id
* message
* created_at

TicketHistory

* id
* ticket_id
* old_status
* new_status
* changed_by
* changed_at

---

## 9. Technology Stack

Backend

* Spring Boot
* Spring Security
* JWT
* Spring Data JPA
* MySQL

Frontend

* React
* TypeScript
* Material UI

Deployment

* Docker
* Nginx

---

## 10. Success Metrics

* Korisnik može kreirati tiket za manje od 1 minuta.
* Menadžer može dodeliti tehničara u manje od 30 sekundi.
* Status tiketa se ažurira bez grešaka.
* 100% promena statusa se evidentira u istoriji.
