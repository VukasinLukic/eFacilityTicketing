# Model podataka

Konceptualni model je prikazan u fajlu
[`NoviDijagramNJT.drawio.svg`](../NoviDijagramNJT.drawio.svg).

Veza stanara i stana je obična asocijacija jedan-prema-više:

- stan može privremeno biti slobodan ili dodeljen jednom stanaru (`0..1`);
- korisnik sa ulogom `TENANT` može imati jedan ili više stanova (`1..*` u aktivnom domenskom stanju);
- korisnik bez dodeljenog stana ne može prijaviti tiket;
- promena stanara stana ne menja podatak o prijaviocu postojećeg tiketa.

## Relacioni model

1. `users (id, first_name, last_name, email, password, role)`
2. `building (id, name, address)`
3. `apartment (id, number, floor, building_id, tenant_id)`
4. `ticket (id, title, description, status, priority, created_at, updated_at, tenant_id, manager_id, technician_id, apartment_id)`
5. `comment (id, message, created_at, ticket_id, user_id)`
6. `ticket_history (id, old_status, new_status, changed_at, ticket_id, changed_by)`

## Strani ključevi i obaveznost

- `apartment.building_id -> building.id` (`NOT NULL`)
- `apartment.tenant_id -> users.id` (`NULL` je dozvoljen)
- `ticket.tenant_id -> users.id` (`NOT NULL`)
- `ticket.manager_id -> users.id` (`NULL` je dozvoljen)
- `ticket.technician_id -> users.id` (`NULL` je dozvoljen)
- `ticket.apartment_id -> apartment.id` (`NOT NULL`)
- `comment.ticket_id -> ticket.id` (`NOT NULL`)
- `comment.user_id -> users.id` (`NOT NULL`)
- `ticket_history.ticket_id -> ticket.id` (`NOT NULL`)
- `ticket_history.changed_by -> users.id` (`NOT NULL`)

`apartment.tenant_id` nema `UNIQUE` ograničenje, pa isti stanar može biti
povezan sa više stanova. Servisni sloj proverava da dodeljeni korisnik ima
ulogu `TENANT`.
