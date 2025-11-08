# Individual Project

An application that gives users full visibility and control over their personal data stored across different organizations and databases.

## Overview

User data is fragmented across companies and stored in multiple, disconnected databases. There is no transparency or unified way for users to access or manage their information.  
This application solves that by allowing users to search and view all their data in a single, consistent format, regardless of how or where it is stored.

Each user is identified by a unique identifier shared across systems. In this implementation, we use the **AFM number** (Greek equivalent of a social security number).

The project is organized as a **monorepo**:
- **Frontend**: React-based interface.
- **Backend (federation)**: Spring Boot service for data integration and ontology mapping.

## Features

### User Perspective
- Search and view all personal data stored across multiple companies.
- See data in a uniform, ontology-based format (built on [Schema.org](https://schema.org/)).
- Hide or restrict access to specific data fields.
- Create custom **privacy categories** (Private, Public, Very_Private).
- Set access levels (No Access, Read, Read/Write).
- Define optional prices for companies to access certain data.
- Filter and query data semantically (e.g. “Orders with price > 100” or “Orders with item = ice cream”).

### Data Owner Perspective
- Upload their database schema directly to the platform.
- Choose the database type (MySQL or MongoDB supported).
- Map each table and column to ontology entities and properties:
  - **Datatype properties** for direct values (e.g. Person → firstName).
  - **Object properties** for relations (e.g. Person → Address → street).
- Define the column that contains the **AFM** number to enable user-level linking.
- Automatically integrate their data into the global ontology mapping.

### Ontology Mapping
- Built on **Schema.org** ontology for standardization.
- The ontology defines the structure and relationships between data types.
- Each new database is semantically mapped to the ontology, creating a unified model.
- User categorizations (privacy, access, etc.) are embedded in the ontology mapping itself, enforcing data controller compliance.

### Backend (Spring Boot)
- Provides REST APIs for both user and data owner operations.
- Uses **OWL API** to handle ontology files (`.owl`) for mapping and reasoning.
- Layers:
  - **DB Connector Layer**: Connects to multiple databases.
  - **Ontology Layer**: Maps schema structures to ontology entities.
  - **User Data Layer**: Merges ontology mappings with user privacy configurations.

### Frontend (React)
- Modern interface built for intuitive data exploration.
- Two main views:
  - **User View** for searching and managing personal data.
  - **Data Owner View** for uploading and mapping schemas.

### Testing Setup
For demonstration, two test databases are provided and run through Docker:
- One **MySQL** database.
- One **MongoDB** database.  
Both are configured without credentials for simplicity.

## Architecture

```
individual-project/
├── frontend/              # React web interface
└── federation/            # Spring Boot backend (API + Ontology + DB integration)
```

### Key Components
- `federation/ontology/` – OWL files and ontology service logic  
- `federation/db/` – Database connectors for MySQL and MongoDB  
- `federation/mapping/` – Services for schema-to-ontology mapping  
- `frontend/` – User and Data Owner UI

## Technologies

**Frontend**
- React
- Tailwind CSS (if used)
- Axios for API requests

**Backend**
- Spring Boot
- OWL API
- MongoDB Driver
- MySQL Connector
- Docker for test databases

## Running Locally

### Prerequisites
- Docker
- Node.js and npm
- Java 17+

### Steps

1. **Start the test databases**
   ```
   docker compose up -d
   ```

2. **Run the backend**
   ```
   cd federation
   ./mvnw spring-boot:run
   ```

3. **Run the frontend**
   ```
   cd frontend
   npm install
   npm start
   ```

4. Access the app at `http://localhost:3000`

## Example Use Case

- A company uploads their MySQL schema describing customer orders.
- Another company uploads a MongoDB schema containing user addresses.
- A user with AFM `123456789` logs in.
- The system retrieves their data from both sources, maps it to ontology entities, and presents it uniformly:
  ```
  Person → Address → Street: "Athens 10"
  Person → Order → Item: "Ice Cream"
  Person → Order → Price: 120
  ```
- The user categorizes their address as Private and the order as Public.

## License
This project was developed as part of a **Bachelor’s Thesis** demonstration.
