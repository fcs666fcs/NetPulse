
import http from 'node:http';
import { URL } from 'node:url';

const port = Number(process.env.PORT || 8080);
const publicBaseUrl = (process.env.PUBLIC_BASE_URL || `http://localhost:${port}`).replace(/\/$/, '');
const nodeId = process.env.NODE_ID || 'tokyo-01';
const region = process.env.REGION || 'JP';
const nodeName = process.env.NODE_NAME || 'Tokyo 01';
const weight = Number(process.env.WEIGHT || 1);

const servers = process.env.NODES_JSON
  ? JSON.parse(process.env.NODES_JSON)
  : [{ id: nodeId, name: nodeName, region, baseUrl: publicBaseUrl, weight }];

const oneMiB = Buffer.allocUnsafe(1024 * 1024);
for (let i = 0; i < oneMiB.length; i++) oneMiB[i] = (i * 31 + 17) & 0xff;

function json(res, status, data) {
  const body = JSON.stringify(data);
  res.writeHead(status, {
    'Content-Type': 'application/json; charset=utf-8',
    'Cache-Control': 'no-store',
    'Content-Length': Buffer.byteLength(body),
    'Access-Control-Allow-Origin': '*',
  });
  res.end(body);
}

function empty(res) {
  res.writeHead(204, {
    'Cache-Control': 'no-store, no-cache, must-revalidate',
    'Pragma': 'no-cache',
    'Access-Control-Allow-Origin': '*',
  });
  res.end();
}

function streamDownload(res, bytes) {
  const total = Math.max(1, Math.min(bytes, 64 * 1024 * 1024));
  res.writeHead(200, {
    'Content-Type': 'application/octet-stream',
    'Content-Encoding': 'identity',
    'Cache-Control': 'no-store, no-cache, must-revalidate',
    'Pragma': 'no-cache',
    'Content-Length': total,
    'Access-Control-Allow-Origin': '*',
    'X-Content-Type-Options': 'nosniff',
  });

  let sent = 0;
  function write() {
    while (sent < total) {
      const count = Math.min(oneMiB.length, total - sent);
      const chunk = oneMiB.subarray(0, count);
      sent += count;
      if (!res.write(chunk)) {
        res.once('drain', write);
        return;
      }
    }
    res.end();
  }
  write();
}

const server = http.createServer((req, res) => {
  try {
    if (req.method === 'OPTIONS') {
      res.writeHead(204, {
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Methods': 'GET,POST,OPTIONS',
        'Access-Control-Allow-Headers': 'Content-Type, Cache-Control',
      });
      return res.end();
    }

    const url = new URL(req.url, publicBaseUrl);
    if (url.pathname === '/api/v1/servers' && req.method === 'GET') {
      return json(res, 200, { version: 1, servers });
    }
    if (url.pathname === '/empty' && req.method === 'GET') return empty(res);
    if (url.pathname === '/health' && req.method === 'GET') {
      return json(res, 200, { ok: true, uptimeSec: process.uptime() });
    }
    if (url.pathname === '/meta' && req.method === 'GET') {
      return json(res, 200, { name: nodeName, region, protocol: 'netpulse-v1', version: '1.0.0' });
    }
    if (url.pathname === '/download' && req.method === 'GET') {
      const bytes = Number(url.searchParams.get('bytes') || 16 * 1024 * 1024);
      return streamDownload(res, Number.isFinite(bytes) ? bytes : 16 * 1024 * 1024);
    }
    if (url.pathname === '/upload' && req.method === 'POST') {
      const maxBytes = 64 * 1024 * 1024;
      let size = 0;
      let rejected = false;
      req.on('data', chunk => {
        size += chunk.length;
        if (size > maxBytes && !rejected) {
          rejected = true;
          res.writeHead(413, { 'Content-Type': 'application/json; charset=utf-8', 'Cache-Control': 'no-store' });
          res.end(JSON.stringify({ error: { code: 'PAYLOAD_TOO_LARGE', message: 'Upload payload is too large', retryable: false } }));
          req.destroy();
        }
      });
      req.on('end', () => {
        if (rejected) return;
        res.writeHead(204, {
          'Cache-Control': 'no-store',
          'Access-Control-Allow-Origin': '*',
          'X-Received-Bytes': String(size),
        });
        res.end();
      });
      return;
    }
    return json(res, 404, { error: { code: 'NOT_FOUND', message: 'Not found', retryable: false } });
  } catch (error) {
    return json(res, 500, { error: { code: 'UNKNOWN', message: String(error?.message || error), retryable: true } });
  }
});

server.headersTimeout = 15_000;
server.requestTimeout = 60_000;
server.keepAliveTimeout = 5_000;
server.listen(port, () => console.log(`NetPulse node listening on ${publicBaseUrl}`));
