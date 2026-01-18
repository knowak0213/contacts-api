# Contacts API

Aplikacja REST API do zarządzania kontaktami z demonstracją protokołów sieciowych.

## Technologie

- **Java 17** + Spring Boot 3.5
- **PostgreSQL** - baza danych
- **Spring Security** - uwierzytelnianie (Basic Auth + Form Login)
- **Spring Data JPA** - ORM
- **Apache Commons Net** - FTP
- **Spring Mail** - SMTP

## Wymagania

- Java 17+
- Docker (dla PostgreSQL)
- Maven

## Uruchomienie

```bash
# 1. Uruchom bazę danych PostgreSQL
docker-compose up -d

# 2. Uruchom aplikację
./mvnw spring-boot:run
```

Aplikacja działa na: `http://localhost:8080`

## Domyślni użytkownicy

| Username | Password | Rola |
|----------|----------|------|
| admin | admin | ADMIN |
| user | user | USER |

## Endpointy API

### Autoryzacja (`/api/auth` - publiczne)

| Metoda | Endpoint | Opis |
|--------|----------|------|
| POST | `/api/auth/register` | Rejestracja użytkownika |
| POST | `/api/auth/login` | Logowanie |

### Kontakty (`/api/contacts` - wymaga autoryzacji)

| Metoda | Endpoint | Opis |
|--------|----------|------|
| GET | `/api/contacts` | Lista kontaktów użytkownika |
| POST | `/api/contacts` | Dodaj kontakt |
| PUT | `/api/contacts/{id}` | Aktualizuj kontakt |
| DELETE | `/api/contacts/{id}` | Usuń kontakt |

### Protokoły sieciowe (`/api/network` - wymaga autoryzacji)

| Metoda | Endpoint | Protokół | Opis |
|--------|----------|----------|------|
| GET | `/api/network/tcp-test` | **TCP** | Test połączenia TCP |
| GET | `/api/network/udp-test` | **UDP** | Test wysyłania pakietu UDP |
| POST | `/api/network/send-email` | **SMTP** | Wysyłanie emaila |
| POST | `/api/network/ftp-upload` | **FTP** | Eksport kontaktów na FTP |
| GET | `/api/network/protocols` | - | Lista dostępnych protokołów |

### Zewnętrzne API (`/api/external` - wymaga autoryzacji)

| Metoda | Endpoint | Opis |
|--------|----------|------|
| GET | `/api/external/validate-phone/{number}` | Walidacja numeru telefonu (NumVerify API) |
| GET | `/api/external/check-format/{number}` | Sprawdzenie formatu numeru |

## Przykłady użycia (PowerShell)

```powershell
# Rejestracja
curl.exe -X POST 'http://localhost:8080/api/auth/register' -H 'Content-Type: application/json' -d '{\"username\":\"test\",\"email\":\"test@test.pl\",\"password\":\"test123\"}'

# Pobranie kontaktów
curl.exe -u test:test123 'http://localhost:8080/api/contacts'

# Test TCP
curl.exe -u test:test123 'http://localhost:8080/api/network/tcp-test'

# Test UDP
curl.exe -u test:test123 'http://localhost:8080/api/network/udp-test'

# Walidacja telefonu
curl.exe -u test:test123 'http://localhost:8080/api/external/validate-phone/48123456789'
```

## Konfiguracja

Edytuj `src/main/resources/application.properties`:

### SMTP (Gmail)
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### NumVerify API
```properties
numverify.api.key=your-api-key
```

## Struktura projektu

```
src/main/java/.../contactsapi/
├── config/          # Konfiguracja (Security, DataInitializer)
├── controller/      # Kontrolery REST
│   ├── AuthController.java
│   ├── ContactController.java
│   ├── NetworkController.java
│   └── ExternalApiController.java
├── dto/             # Obiekty transferowe
├── model/           # Encje JPA (User, Contact)
├── repository/      # Repozytoria Spring Data
└── service/         # Logika biznesowa
    ├── ContactService.java
    ├── NetworkService.java      # TCP, UDP, FTP
    ├── EmailService.java        # SMTP
    ├── PhoneValidationService.java  # Zewnętrzne API
    └── UserService.java
```

## Autor

Projekt na przedmiot "Sieci komputerowe" - UKEN, Semestr 5
