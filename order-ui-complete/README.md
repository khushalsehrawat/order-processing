# OrderFlow Angular Frontend

Frontend for the Order Processing System assignment.

## Requirements covered
- Paginated order list
- Status display: Pending, In Progress, Placed, Shipped, Cancelled, Needs Attention
- CSV upload
- Order details
- Execution steps
- Undo/compensation steps
- Retry Undo button for Needs Attention
- Mark Shipped button for Placed orders
- Audit timeline
- Auto-refresh every five seconds
- Responsive UI

## Local development

The coordinator must run on port 8080.

```bash
npm install
npm start
```

Open:

```text
http://localhost:4200
```

`npm start` uses `proxy.conf.json`, so browser requests to `/api` are forwarded to `http://localhost:8080`.

## Add to the main project

Place this folder as:

```text
order-processing-system/
└── order-ui/
```

## Docker

The included Nginx configuration forwards `/api` to:

```text
http://coordinator-service:8080
```

Add this service to the root `docker-compose.yml`:

```yaml
order-ui:
  build: ./order-ui
  ports:
    - "4200:80"
  depends_on:
    coordinator-service:
      condition: service_started
```

Then run:

```bash
docker compose up --build
```

## API contract expected

- `GET /api/orders?page=0&size=20`
- `GET /api/orders/{id}`
- `POST /api/imports/orders` using multipart key `file`
- `POST /api/orders/{id}/mark-shipped`
- `POST /api/orders/{id}/retry-compensation`
