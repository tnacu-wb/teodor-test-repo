# Build Guide — Opera OHIP App

Step-by-step instructions to set up and run the Opera OHIP test data tool.

## Prerequisites

| Tool    | Version |
| ------- | ------- |
| Node.js | 22.0+   |
| npm     | 10+     |

## Step 1 — Navigate to the Project

```bash
cd ~/Desktop/digital-monorepo/digital-monorepo/tools/opera-ohip-app
```

## Step 2 — Install Dependencies

```bash
npm install
```

## Step 3 — Configure Environment

Copy the example env file:

```bash
cp .env.example .env.local
```

Edit `.env.local` with your actual OHIP credentials:

```bash
# UAT
UAT_OHIP_BASE_URL=https://your-uat-gateway-url.com
UAT_OHIP_CLIENT_ID=your-client-id
UAT_OHIP_CLIENT_SECRET=your-client-secret
UAT_OHIP_APP_KEY=your-app-key
UAT_OHIP_SCOPE=your-scope
UAT_OHIP_ENTERPRISE_ID=your-enterprise-id

# SIT (same format)
# PERF (same format)
```

Get credentials from your team or secrets manager.

## Step 4 — Start the App

```bash
npm run dev
```

The app starts at **http://localhost:3000**.

If port 3000 is busy:

```bash
npm run dev -- -p 3001
```

## Step 5 — Verify

Open http://localhost:3000 in your browser. You should see four tabs:

- Update Daily Rates
- Clear Hotel Restrictions
- Update Hotel Availability / Sell Limits
- Update Package Code

Check the health endpoint:

```bash
curl http://localhost:3000/api/health
```

Expected: `{"status":"healthy","ohip":"reachable",...}`

## Stop the App

Press `Ctrl+C` in the terminal.

## Production Build

```bash
npm run build
npm start
```

## Docker

```bash
docker build -t opera-ohip-app .
docker run -p 3000:3000 --env-file .env.local opera-ohip-app
```

## Troubleshooting

| Error                                            | Fix                                            |
| ------------------------------------------------ | ---------------------------------------------- |
| `getaddrinfo ENOTFOUND your-uat-gateway-url.com` | Update `.env.local` with real OHIP URLs        |
| `Port 3000 is in use`                            | Use `-p 3001` or kill the process on 3000      |
| `MODULE_NOT_FOUND`                               | Run `npm install`                              |
| `401 Unauthorized` from Opera                    | Check client ID/secret/app key in `.env.local` |
