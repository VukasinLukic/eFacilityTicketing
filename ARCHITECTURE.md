# Arhitektura Spring Boot + React Projekta

> Bazirana na analizi RentaCar primer projekta. Koristi se kao template za prilagodavanje na bilo koji novi projekat.

---

## Sadrzaj

1. [Pregled arhitekture](#1-pregled-arhitekture)
2. [Backend — Paket struktura](#2-backend--paket-struktura)
3. [Backend — Sloj po sloj](#3-backend--sloj-po-sloj)
   - [Model (Entiteti)](#31-model-entiteti)
   - [DTO](#32-dto)
   - [Repository](#33-repository)
   - [Mapper](#34-mapper)
   - [Service](#35-service)
   - [Controller](#36-controller)
   - [Response Wrapper](#37-response-wrapper)
   - [Konfiguracija](#38-konfiguracija)
4. [Frontend — Konekcija sa backendom](#4-frontend--konekcija-sa-backendom)
5. [Tok podataka end-to-end](#5-tok-podataka-end-to-end)
6. [Kako prilagoditi za novi projekat](#6-kako-prilagoditi-za-novi-projekat)

---

## 1. Pregled arhitekture

Projekat koristi klasicnu **troslojna (3-tier) arhitekturu**:

```
┌─────────────────────────────────────────────┐
│              FRONTEND (React)               │
│   Components → Services (Axios) → API       │
└──────────────────┬──────────────────────────┘
                   │ HTTP/JSON (REST API)
┌──────────────────▼──────────────────────────┐
│              BACKEND (Spring Boot)          │
│                                             │
│  Controller → Service → Mapper → Repository │
│                                    │        │
│              DTO ◄────────────────►Entity   │
└──────────────────┬──────────────────────────┘
                   │ JPA/Hibernate
┌──────────────────▼──────────────────────────┐
│              DATABASE (MySQL)               │
└─────────────────────────────────────────────┘
```

### Stack

| Sloj       | Tehnologija                          |
|------------|--------------------------------------|
| Backend    | Java 21+, Spring Boot 3.2, Maven     |
| ORM        | Spring Data JPA / Hibernate          |
| Mapping    | MapStruct                            |
| Database   | MySQL 8.0                            |
| Frontend   | React 18+, TypeScript, Axios         |
| Stilovi    | TailwindCSS                          |
| Routing    | React Router v6                      |
| Charts     | Recharts                             |

---

## 2. Backend — Paket struktura

```
com.{company}.{appName}/
│
├── model/                  ← JPA entiteti (tabele u bazi)
│   └── enums/              ← Enum tipovi koji se koriste u entitetima
│
├── dto/                    ← Data Transfer Objects (sta API prima/vraca)
│
├── repository/             ← JPA interfejsi za pristup bazi
│   (u primeru: JPARepo/)
│
├── mapper/                 ← Konverzija Entity ↔ DTO
│
├── service/                ← Poslovna logika
│
├── controller/             ← REST API endpointi
│
└── config/                 ← CORS, Security, i ostale konfiguracije
    (u primeru: connection/) ← Response wrapper klase
```

**Pravilo imenovanja**: svaka domenova oblast (npr. `Car`, `Client`, `Rental`) ima po jednu klasu u svakom sloju.

---

## 3. Backend — Sloj po sloj

### 3.1 Model (Entiteti)

Entiteti su Java klase koje direktno mapiraju na tabele u MySQL bazi.

**Marker interfejs** (prazni interfejs, samo za tip-safety u genericsima):
```java
// model/DomainEntity.java
public interface DomainEntity { }
```

**Primer entiteta**:
```java
@Entity
@Table(name = "city")
public class City implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    // getteri, setteri (ili @Data iz Lomboka)
}
```

**Kljucne anotacije**:

| Anotacija | Znacenje |
|-----------|----------|
| `@Entity` | Ova klasa je JPA entitet (mapira na tabelu) |
| `@Table(name = "...")` | Ime tabele u bazi (opciono ako je isto ime) |
| `@Id` | Primarni kljuc |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | Auto-increment |
| `@Column(nullable = false, unique = true)` | Ogranicenja kolone |
| `@ManyToOne(fetch = FetchType.LAZY)` | Veza vise-prema-jedan (strani kljuc) |
| `@OneToMany(mappedBy = "...")` | Veza jedan-prema-vise (suprotna strana) |
| `@JoinColumn(name = "fk_kolona")` | Ime FK kolone u tabeli |
| `@Enumerated(EnumType.STRING)` | Cuva enum kao String u bazi |

**Primer relacije**:
```java
@Entity
public class Rental implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int rentalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;                    //FK prema Car tabeli

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_jmbg")
    private Client client;              // FK prema Client tabeli

    private LocalDate startDate;
    private LocalDate endDate;
}
```

**Enum primer**:
```java
// model/enums/VehicleCategory.java
public enum VehicleCategory {
    EKONOMIK, STANDARD, PREMIUM, VAN
}
```

---

### 3.2 DTO

DTO (Data Transfer Object) je klasa koja definise **sta se salje/prima kroz API** — ne cuva se u bazi.

**Marker interfejs**:
```java
// dto/DomainDTO.java
public interface DomainDTO { }
```

**Pravila za DTO**:
- DTO **ne sme** da sadrzi JPA anotacije
- Ako entitet ima relacije (npr. `Car` ima `CarModel`), DTO takodje ima ugnjezdeni DTO (ne entitet):
  ```java
  public class CarDTO implements DomainDTO {
      private int id;
      private String licensePlate;
      private CarModelDTO carModel;   // ← ugnjezdeni DTO, ne CarModel entitet!
  }
  ```
- DTO moze imati manje polja od entiteta (nikad ne izlazuj passworde, interne flagove, itd.)

**Primer jednostavnog DTO**:
```java
public class CityDTO implements DomainDTO {
    private int id;
    private String name;
    // getteri i setteri
}
```

---

### 3.3 Repository

Repository je interfejs koji pruzaju CRUD operacije nad bazom. Spring Data JPA automatski implementira metode.

```java
// repository/CityRepository.java
public interface CityRepository extends JpaRepository<City, Integer> {
    // Nasledjuje: findAll(), findById(), save(), deleteById(), existsById()...
}
```

**Prilagodene metode po konvenciji**:
```java
public interface ClientRepository extends JpaRepository<Client, String> {

    // Spring generise SQL automatski po imenu metode:
    List<Client> findBySurnameStartsWith(String name);
    Optional<Client> findByUsername(String username);
    boolean existsByJmbg(String jmbg);

    // Native SQL query (kad konvencija nije dovoljna):
    @Query(value = "SELECT * FROM client WHERE jmbg = :jmbg", nativeQuery = true)
    Client getPerson(@Param("jmbg") String jmbg);
}
```

**Konvencija imenovanja metoda**:
- `findBy{Polje}` — trazi po polju
- `findBy{Polje}StartsWith` — LIKE 'vrednost%'
- `findBy{Polje}Containing` — LIKE '%vrednost%'
- `existsBy{Polje}` — vraca boolean
- `deleteBy{Polje}` — brise po polju
- `countBy{Polje}` — broji

---

### 3.4 Mapper

Mapper konvertuje entitet u DTO i obrnuto. U ovom projektu koristi se **MapStruct** (primer koristi rucno pisanje, ali princip je isti).

**Bazni interfejs**:
```java
// mapper/BaseMapper.java
public interface BaseMapper<DTO extends DomainDTO, DB extends DomainEntity> {
    DTO toDomainDTO(DB entity);
    DB toDomainEntity(DTO dto);
}
```

**MapStruct implementacija** (za vas projekat koji koristi MapStruct):
```java
// mapper/CityMapper.java
@Mapper(componentModel = "spring")   // Spring bean, moze se injektovati
public interface CityMapper extends BaseMapper<CityDTO, City> {

    @Override
    CityDTO toDomainDTO(City entity);

    @Override
    City toDomainEntity(CityDTO dto);
}
```

**MapStruct sa relacijama** (kad DTO sadrzi ugnjezdeni DTO):
```java
@Mapper(componentModel = "spring", uses = {CityMapper.class})
public interface ClientMapper extends BaseMapper<ClientDTO, Client> {

    @Override
    ClientDTO toDomainDTO(Client entity);

    @Override
    Client toDomainEntity(ClientDTO dto);
    // MapStruct ce automatski koristiti CityMapper za konverziju city polja
}
```

**Rucna implementacija** (kao u primeru, bez MapStruct):
```java
@Component
public class CityMapper implements BaseMapper<CityDTO, City> {

    @Override
    public CityDTO toDomainDTO(City entity) {
        CityDTO dto = new CityDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    @Override
    public City toDomainEntity(CityDTO dto) {
        City city = new City();
        city.setId(dto.getId());
        city.setName(dto.getName());
        return city;
    }
}
```

**Kljucna razlika MapStruct vs rucno**: MapStruct generiše kod pri kompajliranju, rucno se pise svako polje. Za projekat sa vise polja, MapStruct je znacajno brzi za razvoj.

---

### 3.5 Service

Service sloj sadrzi **poslovnu logiku**. Ovde se obavlja validacija, koordinacija vise repozitorijuma, transformacija podataka.

```java
@Service           // Spring bean, registrovan kao servis
@Transactional     // Sve metode se izvrsavaju u DB transakciji
public class CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    // Konstruktorska injekcija (preporuceno nad @Autowired)
    public CityService(CityRepository cityRepository, CityMapper cityMapper) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
    }

    public List<CityDTO> getAll() {
        return cityRepository.findAll()
                .stream()
                .map(cityMapper::toDomainDTO)
                .collect(Collectors.toList());
    }

    public String addCity(CityDTO dto) {
        try {
            City city = cityMapper.toDomainEntity(dto);
            cityRepository.save(city);
            return "City added successfully!";
        } catch (Exception e) {
            return "Error adding city!";
        }
    }
}
```

**Smernice za service sloj**:
- Uvek koristiti **konstruktorsku injekciju** (ne `@Autowired` na polju)
- `@Transactional` na nivou klase pokriva sve metode; staviti `@Transactional(readOnly = true)` na GET metode za optimizaciju
- Service **ne sme** da vraca entitete — uvek vraca DTO
- Validacija i poslovna pravila idu ovde, ne u controller

---

### 3.6 Controller

Controller prima HTTP zahteve i delegira logiku servisu. Ne sadrzi poslovnu logiku.

```java
@RestController                              // Kombinuje @Controller i @ResponseBody
@RequestMapping("/cities")                   // Bazni URL za sve metode
@CrossOrigin("http://localhost:3000")        // Dozvoli zahteve sa React frontend-a
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAll() {
        List<CityDTO> cities = cityService.getAll();
        return ResponseEntity.ok(
            HttpResponse.getResponseWithData("Cities fetched", Map.of("cities", cities), HttpStatus.OK)
        );
    }

    @PostMapping("/add")
    public ResponseEntity<Response> add(@RequestBody CityDTO cityDTO) {
        String result = cityService.addCity(cityDTO);
        return ResponseEntity.ok(HttpResponse.getResponse(result, HttpStatus.OK));
    }

    @GetMapping("/getCity/{id}")
    public ResponseEntity<Response> getCity(@PathVariable int id) {
        CityDTO city = cityService.getCity(id);
        return ResponseEntity.ok(
            HttpResponse.getResponseWithData("City fetched", Map.of("city", city), HttpStatus.OK)
        );
    }

    @PostMapping("/delete")
    public ResponseEntity<Response> delete(@RequestBody CityDTO cityDTO) {
        String result = cityService.deleteCity(cityDTO.getId());
        return ResponseEntity.ok(HttpResponse.getResponse(result, HttpStatus.OK));
    }
}
```

**HTTP anotacije**:
| Anotacija | HTTP metoda | Tipicna upotreba |
|-----------|-------------|------------------|
| `@GetMapping` | GET | Citanje podataka |
| `@PostMapping` | POST | Kreiranje, update, delete |
| `@PutMapping` | PUT | Update cijelog resursa |
| `@DeleteMapping` | DELETE | Brisanje |
| `@PatchMapping` | PATCH | Parcijalni update |

**Parametri**:
```java
@GetMapping("/search/{query}")
public ResponseEntity<Response> search(
    @PathVariable String query,           // Iz URL-a: /search/honda
    @RequestParam(required = false) int page  // Iz URL-a: /search/honda?page=1
) { ... }

@PostMapping("/add")
public ResponseEntity<Response> add(
    @RequestBody CarDTO carDTO            // Iz JSON body-a
) { ... }
```

---

### 3.7 Response Wrapper

Svi odgovori API-ja imaju uniformnu strukturu kroz `Response` klasu.

```java
// connection/Response.java
@JsonInclude(JsonInclude.Include.NON_NULL)  // Ne ukljucuj null polja u JSON
public class Response {
    private String message;
    private Map<?, ?> data;
    private HttpStatus status;

    public Response(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public Response(String message, Map<?, ?> data, HttpStatus status) {
        this.message = message;
        this.data = data;
        this.status = status;
    }
    // getteri...
}
```

```java
// connection/HttpResponse.java
public class HttpResponse {
    public static Response getResponseWithData(String message, Map<?, ?> data, HttpStatus status) {
        return new Response(message, data, status);
    }

    public static Response getResponse(String message, HttpStatus status) {
        return new Response(message, status);
    }
}
```

**Primer JSON odgovora** koji stize na frontend:
```json
{
  "message": "Cities fetched",
  "data": {
    "cities": [
      { "id": 1, "name": "Beograd" },
      { "id": 2, "name": "Novi Sad" }
    ]
  }
}
```

---

### 3.8 Konfiguracija

**`src/main/resources/application.properties`**:
```properties
spring.application.name=eFacilityTicketing

# Baza podataka
spring.datasource.url=jdbc:mysql://localhost:3306/efacility
spring.datasource.username=root
spring.datasource.password=

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Port (opciono, default je 8080)
server.port=8080
```

**`ddl-auto` opcije**:
| Vrednost | Ponasanje |
|----------|-----------|
| `create` | Brise i ponovo kreira tabele pri pokretanju |
| `create-drop` | Kreira pri pokretanju, brise pri zaustavljanju |
| `update` | Dodaje nove kolone/tabele, ne brise postojece |
| `validate` | Samo proverava da li schema odgovara entitetima |
| `none` | Nista ne radi (za produkciju) |

**`pom.xml` kljucne zavisnosti**:
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.x</version>
</parent>

<dependencies>
    <!-- Web / REST API -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- JPA / Hibernate -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- MySQL Driver -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- MapStruct -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>1.5.5.Final</version>
        <scope>provided</scope>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

---

## 4. Frontend — Konekcija sa backendom

Frontend komunicira sa backendom iskljucivo kroz HTTP zahteve. Ova sekcija opisuje **kako React aplikacija zove Spring Boot API** — ne dizajn komponenti.

### 4.1 Environment konfiguracija

```typescript
// src/config/api.ts
export const API_BASE_URL = "http://localhost:8080";
```

### 4.2 Response model

Svaki API odgovor ima istu strukturu, pa se definiise jedan TypeScript tip:

```typescript
// src/types/Response.ts
export interface ApiResponse<T = any> {
  message: string;
  data?: {
    [key: string]: T;   // npr: { cities: City[] } ili { city: City }
  };
}
```

### 4.3 API Service pattern

Svaka domenova oblast ima svoj servis koji grupise sve API pozive:

```typescript
// src/services/cityService.ts
import axios from "axios";
import { API_BASE_URL } from "../config/api";
import { ApiResponse } from "../types/Response";
import { City } from "../types/City";

const BASE = `${API_BASE_URL}/cities`;

export const cityService = {

  getAll: async (): Promise<City[]> => {
    const res = await axios.get<ApiResponse<City[]>>(`${BASE}/all`);
    return res.data.data!.cities;
  },

  getCity: async (id: number): Promise<City> => {
    const res = await axios.get<ApiResponse<City>>(`${BASE}/getCity/${id}`);
    return res.data.data!.city;
  },

  add: async (city: City): Promise<string> => {
    const res = await axios.post<ApiResponse>(`${BASE}/add`, city);
    return res.data.message;
  },

  delete: async (city: City): Promise<string> => {
    const res = await axios.post<ApiResponse>(`${BASE}/delete`, city);
    return res.data.message;
  },
};
```

### 4.4 TypeScript modeli

Modeli na frontendu odgovaraju DTO-ovima na backendu:

```typescript
// src/types/City.ts
export interface City {
  id: number;
  name: string;
}

// src/types/Client.ts
import { City } from "./City";

export interface Client {
  jmbg: string;
  name: string;
  surname: string;
  age: number;
  city: City;           // ugnjezdeni objekat, kao u ClientDTO
  mobile: string;
  username: string;
  password: string;
}
```

### 4.5 Koriscenje servisa u React komponentama

```typescript
// src/components/CityList.tsx
import { useEffect, useState } from "react";
import { cityService } from "../services/cityService";
import { City } from "../types/City";

export default function CityList() {
  const [cities, setCities] = useState<City[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    cityService.getAll()
      .then(setCities)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div>Ucitavanje...</div>;

  return (
    <ul>
      {cities.map(city => (
        <li key={city.id}>{city.name}</li>
      ))}
    </ul>
  );
}
```

### 4.6 Axios globalna konfiguracija (opciono)

```typescript
// src/config/axiosConfig.ts
import axios from "axios";
import { API_BASE_URL } from "./api";

axios.defaults.baseURL = API_BASE_URL;
axios.defaults.headers.common["Content-Type"] = "application/json";

// Interceptor za greske
axios.interceptors.response.use(
  response => response,
  error => {
    console.error("API greska:", error.response?.data?.message);
    return Promise.reject(error);
  }
);
```

### 4.7 Routing struktura

```typescript
// src/App.tsx
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/dashboard" />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/cities" element={<CityList />} />
        <Route path="/cities/add" element={<CityAdd />} />
        {/* Dodavati rute za svaku domensku oblast */}
      </Routes>
    </BrowserRouter>
  );
}
```

---

## 5. Tok podataka end-to-end

Primer: Korisnik klikne "Dodaj grad" → formiraj zahtev → spremi u bazu.

```
Korisnik popuni formu → CityAdd.tsx prikuplja podatke kao City objekat
    ↓
cityService.add(city) → axios.post("http://localhost:8080/cities/add", city)
    ↓
HTTP POST /cities/add sa JSON body: {"id":0,"name":"Nis"}
    ↓
CityController.add(@RequestBody CityDTO cityDTO)
    ↓
cityService.addCity(cityDTO) → validacija, provjera duplikata
    ↓
cityMapper.toDomainEntity(cityDTO) → konverzija u City entitet
    ↓
cityRepository.save(city) → INSERT INTO city (name) VALUES ('Nis')
    ↓
vraca String poruku: "City added successfully!"
    ↓
HttpResponse.getResponse(result, HttpStatus.OK) → Response objekat
    ↓
JSON odgovor: {"message": "City added successfully!"}
    ↓
res.data.message u frontendu → prikaz notifikacije korisniku
```

---

## 6. Kako prilagoditi za novi projekat

### Korak 1: Definisi domenske objekte

Nabroji sve entitete koji postoje u novom projektu. Za svaki entitet:
- Koja polja ima?
- Kakve su veze (ManyToOne, OneToMany)?
- Koji enumi postoje?

### Korak 2: Kopiraj i preimenuj strukturu

Za svaku domensku oblast (npr. `Ticket`, `Facility`, `User`):

| Sta kreirati | Primer za Ticket |
|--------------|------------------|
| `model/Ticket.java` | Entitet sa JPA anotacijama |
| `dto/TicketDTO.java` | DTO bez JPA anotacija |
| `repository/TicketRepository.java` | JPA interfejs |
| `mapper/TicketMapper.java` | MapStruct mapper |
| `service/TicketService.java` | Poslovna logika |
| `controller/TicketController.java` | REST endpointi |
| `src/types/Ticket.ts` | TypeScript interfejs |
| `src/services/ticketService.ts` | Axios pozivi |

### Korak 3: Prilagodi application.properties

```properties
spring.application.name=efacility-ticketing
spring.datasource.url=jdbc:mysql://localhost:3306/{IME_BAZE}
spring.datasource.username={USERNAME}
spring.datasource.password={PASSWORD}
```

### Korak 4: Prilagodi CORS

U svakom controlleru promeniti:
```java
@CrossOrigin("http://localhost:3000")   // React port je 3000, ne 4200
```

Ili globalno u konfiguracionoj klasi:
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
```

### Korak 5: Prilagodi package ime

U `pom.xml` i svim Java fajlovima promeniti:
```
com.rentaCar.rentaCarBackend → com.{company}.{projectName}
```

### Korak 6: Dodaj/ukloni zavisnosti u pom.xml

Standardni set za ovaj stack je vec definisan u sekciji 3.8. Dodati specijalne ako projekat zahteva Security, JWT, Email, itd.

---

### Checklist za novi projekat

- [ ] Definisati sve entitete i njihove relacije (ER dijagram)
- [ ] Kreirati MySQL bazu i podesiti `application.properties`
- [ ] Za svaki entitet: Model → DTO → Repository → Mapper → Service → Controller
- [ ] Podesiti `@CrossOrigin` na `http://localhost:3000`
- [ ] Na frontendu: za svaki entitet: TypeScript tip → Service → Komponente
- [ ] Podesiti `API_BASE_URL` na `http://localhost:8080`
- [ ] Pokrenuti backend (`mvn spring-boot:run`) i frontend (`npm start`) i testirati API pozive
