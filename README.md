eFacilityTicketing

Web aplikacija za prijavu, praćenje i upravljanje kvarovima i zahtevima za održavanje u stambenim i poslovnim zgradama.

Tehnologije:

Backend: 
Java 17, Spring Boot 3.2, Spring Security (JWT), Spring Data JPA

Baza podataka: 
MySQL (H2 za testiranje)

Frontend: 
React 18, TypeScript, Vite, Tailwind CSS

Ostali alati i biblioteke: 
Axios, Recharts (grafici i analitika), MapStruct, Lombok, Apache POI (Excel export), OpenPDF (PDF export), JavaMail (email obaveštenja)

Funkcionalnosti:
Korisnici i autorizacija:
Autentifikacija preko JWT tokena, registracija i podela uloga (stanari, tehničari / upravnici).

Upravljanje objektima:
Registar zgrada i pojedinačnih stanova ili lokal.

Rad sa tiketima: 
Kreiranje tiketa za kvar, određivanje prioriteta, dodeljivanje tehničarima i menjanje statusa.

Istorija i komentari:
Vodjenje komentara na samom tiketu i automatsko bilježenje svake promene statusa (audit log).

Analitika i dashboard: 
Pregled statistike i stanja tiketa po zgradama uz grafičke prikaze.

Izveštaji i notifikacije:
Izvoz podataka u PDF i Excel formatu, kao i slanje email obaveštenja o promenama na tiketima.
