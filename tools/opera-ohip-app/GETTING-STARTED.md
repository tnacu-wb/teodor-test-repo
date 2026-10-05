# Getting Started — Opera OHIP App

Instructions for running the Opera OHIP app locally from your terminal.

## Prerequisites

- Node.js (v18 or later)
- npm (comes with Node.js)
- OHIP API credentials for at least one environment (UAT, SIT, or PERF)

## Step-by-Step

### 1. Navigate to the app directory

From the repository root (`digital-monorepo/digital-monorepo`):

```bash
cd tools/opera-ohip-app
```

If your terminal is in the outer `digital-monorepo` folder, go one level deeper first:

```bash
cd digital-monorepo/tools/opera-ohip-app
```

Or use the full path:

```bash
cd /Users/rizwan/Desktop/digital-monorepo/digital-monorepo/tools/opera-ohip-app
```

### 2. Install dependencies

```bash
npm install
```

### 3. Set up environment variables

Copy the example env file and fill in your credentials:

```bash
cp .env.example .env.local
```

Open `.env.local` in your editor and replace the placeholder values with your actual OHIP credentials:

```
UAT_OHIP_BASE_URL=https://...
UAT_OHIP_CLIENT_ID=...
UAT_OHIP_CLIENT_SECRET=...
UAT_OHIP_APP_KEY=...
UAT_OHIP_SCOPE=...
UAT_OHIP_ENTERPRISE_ID=...
```

Repeat for SIT and PERF if needed.

### 4. Start the development server

```bash
npm run dev
```

The app will start at **http://localhost:3000**.

### 5. (Optional) Build and run for production

```bash
npm run build
npm run start
```

This compiles the app and serves it in production mode on http://localhost:3000.

## Running with Docker

```bash
docker build -t opera-ohip-app .
docker run -p 3000:3000 --env-file .env.local opera-ohip-app
```

## Available Scripts

| Command                 | Description                               |
| ----------------------- | ----------------------------------------- |
| `npm run dev`           | Start in development mode (hot reload)    |
| `npm run build`         | Create a production build                 |
| `npm run start`         | Start the production build                |
| `npm run lint`          | Run linting checks                        |
| `npm run type-check`    | Type-check with `tsc --noEmit`            |
| `npm run test`          | Run the unit tests once                   |
| `npm run test:watch`    | Run the unit tests in watch mode          |
| `npm run test:coverage` | Run the unit tests with a coverage report |
| `npm run format`        | Format the codebase with Prettier         |
| `npm run format:check`  | Check formatting without writing          |

## Health Checks

Once running, verify the app is healthy:

```bash
curl http://localhost:3000/api/ping
curl http://localhost:3000/api/health
```

## Troubleshooting

- **Port already in use** — Kill the process on port 3000 or start on a different port: `npm run dev -- -p 3001`
- **Missing credentials** — Ensure `.env.local` exists and contains valid values for at least one environment.
- **Node version issues** — Check your version with `node -v`. Upgrade to v18+ if needed.
