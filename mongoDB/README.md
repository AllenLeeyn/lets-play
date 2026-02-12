# MongoDB

This folder documents how to run **MongoDB** locally for the [Let's Play](../docs/lets-play.md) API. The API uses MongoDB for users and products; identifiers are MongoDB ObjectIds.

## Prerequisites

- [Docker](https://www.docker.com/) (for containerized setup), or
- [MongoDB Community Server](https://www.mongodb.com/try/download/community) and [mongosh](https://www.mongodb.com/docs/mongodb-shell/) (for a local install)

## Run MongoDB with Docker

### Option A: Docker Compose (recommended)

From the project root:

```bash
docker compose up -d
```

This starts MongoDB on port **27017** with a persistent volume. To stop: `docker compose down` (add `-v` to remove the volume and data).

### Option B: Single `docker run` command

Start a MongoDB container (default port **27017**):

```bash
docker run --name mongodb -p 27017:27017 -d mongodb/mongodb-community-server:latest
```

With a volume for persistence:

```bash
docker run --name mongodb -p 27017:27017 -v mongodb_data:/data/db -d mongodb/mongodb-community-server:latest
```

Useful commands:

- **Stop:** `docker stop mongodb`
- **Start again:** `docker start mongodb`
- **Remove container:** `docker rm -f mongodb` (data in a volume is preserved)

## Connect with mongosh

From your host machine:

```bash
mongosh --port 27017
```

Or explicitly to localhost:

```bash
mongosh "mongodb://localhost:27017"
```

## Connection string for the API

When running the Let's Play API locally, point it at this MongoDB instance. For example, in `application.properties` or environment:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/letsplay
```

Replace `letsplay` with your desired database name.

## References

- [MongoDB on Docker – compatibility](https://www.mongodb.com/resources/products/compatibilities/docker)
- [Install MongoDB Community with Docker (v7.0)](https://www.mongodb.com/docs/v7.0/tutorial/install-mongodb-community-with-docker/)
