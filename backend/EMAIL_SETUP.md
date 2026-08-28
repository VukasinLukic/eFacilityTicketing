# Podesavanje e-mail obavestenja

Sistem stanaru salje automatsko obavestenje kada se promeni status njegovog tiketa.
Slanje ide preko Gmail SMTP servera i zahteva **App Password** — obicna lozinka Google naloga
ne radi za SMTP.

## 1. Ukljuci dvofaktorsku autentifikaciju (2FA)

App Password se ne moze generisati bez 2FA.

1. Otvori <https://myaccount.google.com/security>
2. Pronadji **Potvrda u 2 koraka** (*2-Step Verification*) i ukljuci je
3. Zavrsi verifikaciju telefonom

## 2. Generisi App Password

1. Otvori <https://myaccount.google.com/apppasswords>
2. Unesi naziv aplikacije, npr. `eFacility Ticketing`
3. Klikni **Napravi** (*Create*)
4. Google prikazuje **16 karaktera** u formatu `abcd efgh ijkl mnop`

Kopiraj tu lozinku odmah — Google je vise nikad nece prikazati.
Razmake mozes ostaviti ili ukloniti, oba oblika rade.

## 3. Postavi environment varijable

Aplikacija cita dve varijable:

| Varijabla | Vrednost |
|---|---|
| `MAIL_USERNAME` | tvoja Gmail adresa, npr. `ime.prezime@gmail.com` |
| `MAIL_APP_PASSWORD` | 16-karakterni App Password iz koraka 2 |

App Password se **nikada ne upisuje u `application.properties`** — taj fajl ide u git.
U njemu stoje samo placeholder-i:

```properties
spring.mail.username=${MAIL_USERNAME:}
spring.mail.password=${MAIL_APP_PASSWORD:}
```

### IntelliJ IDEA

1. **Run → Edit Configurations…**
2. Izaberi konfiguraciju `TicketingApplication`
3. Polje **Environment variables** → klikni ikonicu sa desne strane
4. Dodaj dva unosa:
   - `MAIL_USERNAME` = `ime.prezime@gmail.com`
   - `MAIL_APP_PASSWORD` = `abcdefghijklmnop`
5. **Apply → OK**

### NetBeans

**Run → Set Project Configuration → Customize… → Run**, pa u polje
*Environment Variables* dodaj iste dve varijable.

### Terminal (PowerShell)

```powershell
$env:MAIL_USERNAME = "ime.prezime@gmail.com"
$env:MAIL_APP_PASSWORD = "abcdefghijklmnop"
./mvnw spring-boot:run
```

### Terminal (Git Bash / Linux / macOS)

```bash
export MAIL_USERNAME="ime.prezime@gmail.com"
export MAIL_APP_PASSWORD="abcdefghijklmnop"
./mvnw spring-boot:run
```

Varijable postavljene ovako vaze samo za tu sesiju terminala.

## 4. Provera

Prijavi se kao MANAGER ili TECHNICIAN i promeni status nekog tiketa.
U konzoli backend-a treba da se pojavi:

```
Obavestenje o promeni statusa tiketa 1 poslato na stanar@primer.com
```

## Iskljucivanje slanja

Za rad bez interneta ili kada e-mail nije potreban:

```
MAIL_ENABLED=false
```

## Vazno

Slanje e-poste je **best effort**. Ako SMTP ne radi, nema interneta ili varijable nisu
postavljene, promena statusa tiketa se svejedno uspesno izvrsava — greska se samo upise
u log. Korisnik nikada ne vidi gresku zbog e-maila.

Slanje se izvrsava asinhrono (`@Async`, izvrsilac `emailExecutor`), pa HTTP odgovor
korisniku ne ceka SMTP server.
