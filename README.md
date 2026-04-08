# StudyPlanner

Application Spring Boot permettant aux étudiants de planifier et organiser leurs sessions de révision.

## Prérequis

- Java 17+
- Maven 3.6+

## Lancer l'application

```bash
mvn spring-boot:run
```

L'application démarre sur `http://localhost:8080`.

## Utilisateurs de test

| Username | Password   | Rôle |
|----------|------------|------|
| alice    | alice123   | USER |
| bob      | bob123     | USER |
| charlie  | charlie123 | USER |

## Accès H2 Console

URL : `http://localhost:8080/h2-console`

| Champ        | Valeur                  |
|--------------|-------------------------|
| JDBC URL     | `jdbc:h2:mem:studyplannerdb` |
| Username     | `sa`                    |
| Password     | *(vide)*                |

## Accès Actuator

- Health : `http://localhost:8080/actuator/health`
- Info   : `http://localhost:8080/actuator/info`

Le health indicator personnalisé surveille le nombre total de sessions :
- `≤ 30` → UP (Charge normale)
- `31–50` → UP (Charge élevée - warning)
- `> 50` → DOWN (Surcharge critique)

---

## Exemples de requêtes curl

### Créer une session (POST /api/sessions)

```bash
curl -X POST http://localhost:8080/api/sessions \
  -u alice:alice123 \
  -H "Content-Type: application/json" \
  -d '{
    "subject": "Java Spring Boot",
    "description": "Révision des concepts IoC et DI",
    "startTime": "2026-06-01T14:00:00",
    "endTime": "2026-06-01T16:00:00"
  }'
```

Réponse `201 Created` :
```json
{
  "id": 1,
  "studentName": "alice",
  "subject": "Java Spring Boot",
  "description": "Révision des concepts IoC et DI",
  "startTime": "2026-06-01T14:00:00",
  "endTime": "2026-06-01T16:00:00",
  "createdAt": "2026-04-07T10:00:00",
  "updatedAt": "2026-04-07T10:00:00"
}
```

---

### Lister ses sessions (GET /api/sessions)

```bash
curl http://localhost:8080/api/sessions \
  -u alice:alice123
```

Réponse `200 OK` :
```json
[
  {
    "id": 1,
    "studentName": "alice",
    "subject": "Java Spring Boot",
    "description": "Révision des concepts IoC et DI",
    "startTime": "2026-06-01T14:00:00",
    "endTime": "2026-06-01T16:00:00",
    "createdAt": "2026-04-07T10:00:00",
    "updatedAt": "2026-04-07T10:00:00"
  }
]
```

---

### Obtenir une session par ID (GET /api/sessions/{id})

```bash
curl http://localhost:8080/api/sessions/1 \
  -u alice:alice123
```

Réponse `200 OK` : session détaillée.
Réponse `404 Not Found` : session inexistante ou appartient à un autre utilisateur.

---

### Modifier une session (PUT /api/sessions/{id})

```bash
curl -X PUT http://localhost:8080/api/sessions/1 \
  -u alice:alice123 \
  -H "Content-Type: application/json" \
  -d '{
    "subject": "Mathématiques",
    "description": "Algèbre linéaire",
    "startTime": "2026-06-02T09:00:00",
    "endTime": "2026-06-02T11:00:00"
  }'
```

Réponse `200 OK` : session mise à jour.

---

### Supprimer une session (DELETE /api/sessions/{id})

```bash
curl -X DELETE http://localhost:8080/api/sessions/1 \
  -u alice:alice123
```

Réponse `204 No Content`.

---

## Format des erreurs

Toutes les erreurs suivent ce format :

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Erreur de validation des données",
  "errors": {
    "subject": "La matière est obligatoire",
    "startTime": "La date de début doit être dans le futur"
  },
  "timestamp": "2026-04-07T10:00:00"
}
```

| Code              | HTTP | Description                        |
|-------------------|------|------------------------------------|
| VALIDATION_ERROR  | 400  | Données invalides                  |
| NOT_FOUND         | 404  | Ressource inexistante              |
| BUSINESS_ERROR    | 422  | Erreur métier (ex: endTime avant startTime) |
| INTERNAL_ERROR    | 500  | Erreur serveur                     |

---

## Isolation des données

Chaque utilisateur ne peut voir et modifier **que ses propres sessions**. Tenter d'accéder à la session d'un autre utilisateur retourne `404 Not Found` (sans révéler son existence).

```bash
# Alice crée une session (id=1)
curl -X POST http://localhost:8080/api/sessions \
  -u alice:alice123 \
  -H "Content-Type: application/json" \
  -d '{"subject":"Java","startTime":"2026-06-01T14:00:00","endTime":"2026-06-01T16:00:00"}'

# Bob tente d'y accéder → 404
curl http://localhost:8080/api/sessions/1 \
  -u bob:bob123
```

---

## Sans authentification → 401

```bash
curl http://localhost:8080/api/sessions
# HTTP 401 Unauthorized
```
