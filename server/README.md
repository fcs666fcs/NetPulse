# NetPulse Test Node

This is a minimal control + test node for NetPulse v1.

## Endpoints

- `GET /api/v1/servers`
- `GET /empty`
- `GET /download?bytes=16777216`
- `POST /upload`
- `GET /health`
- `GET /meta`

## Environment

`PUBLIC_BASE_URL` must be the public URL that clients can reach.
`NODES_JSON` can provide a central list of multiple nodes, for example:

```json
[
  {"id":"tokyo-01","name":"Tokyo 01","region":"JP","baseUrl":"https://tokyo.example.com","weight":1},
  {"id":"seoul-01","name":"Seoul 01","region":"KR","baseUrl":"https://seoul.example.com","weight":1}
]
```

For production, put the service behind HTTPS and monitor bandwidth, CPU, memory, concurrent requests and error rates. The test server itself should not be considered a public arbitrary file server; keep the endpoint sizes and concurrency bounded.
