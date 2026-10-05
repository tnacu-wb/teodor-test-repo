# Build Guide — Delivery Intelligence Portal

Step-by-step instructions to set up and run the portal from scratch.

## Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| Node.js | **18.17+** | LTS recommended (20.x or 22.x) |
| npm | **9+** | Comes with Node.js |

Check versions:

```bash
node --version   # Should show v18.17.0 or higher
npm --version    # Should show 9.x or higher
```

## Step 1 — Navigate to the Project

```bash
cd digital-monorepo/tools/delivery-intelligence-portal
```

## Step 2 — Install Dependencies

```bash
npm install
```

This installs:
- Next.js 15 (App Router)
- React 19
- Recharts (charting)
- Lucide React (icons)
- Tailwind CSS (styling)
- TypeScript

## Step 3 — Run Development Server

```bash
npm run dev
```

The app starts at **http://localhost:3000** (or the next available port if 3000 is occupied).

## Step 4 — Open in Browser

Navigate to http://localhost:3000. You should see the Executive Overview with all KPIs and charts.

## Configuration

### Data Source

All data lives in a single file:

```
lib/rawData.ts
```

This file contains:
- **meta**: Organisation, program, source, refresh date
- **config**: Health band thresholds (80%–110% Say/Do is "Good")
- **teams**: Team definitions with vendor, members, capacity
- **sprints**: Sprint-by-sprint committed/delivered/scope added data

### Updating Data

**To add a new sprint:**

Add entries to the `sprints` array in `lib/rawData.ts`:

```typescript
{
  id: 'blake-itc-26.2.3',
  name: '26.2.3',
  team: 'blake-itc',
  vendor: 'ITC',
  startDate: '2026-07-02',
  endDate: '2026-07-15',
  committed: 105,
  delivered: 102,
  scopeAdded: 3,
  carryForward: 3,
  goalMet: true,
},
```

**To add a new team:**

Add to the `teams` array:

```typescript
{
  id: 'new-team',
  name: 'New Team (Vendor)',
  vendor: 'Vendor Name',
  members: ['Person A', 'Person B'],
  capacity: 80,
},
```

### Using the Import UI

1. Click **Import** in the top bar
2. Choose JSON or CSV upload
3. Download templates from the **Templates** tab for the correct format
4. Upload your file — it validates before applying
5. Click **Import Data** to update the dashboard

### CSV Format (Sprints)

```csv
id,name,team,vendor,start_date,end_date,committed,delivered,scope_added,carry_forward,goal_met
blake-itc-26.2.3,26.2.3,blake-itc,ITC,2026-07-02,2026-07-15,105,102,3,3,true
```

### JSON Format (Full)

```json
{
  "meta": { "org": "Whitbread Digital", "program": "Digital Delivery", ... },
  "config": { "healthyBandLow": 0.8, "healthyBandHigh": 1.1, "carryThreshold": 0.2 },
  "teams": [...],
  "sprints": [...]
}
```

## Health Band Configuration

The `config` object in `rawData.ts` controls what's considered healthy:

| Setting | Default | Meaning |
|---------|---------|---------|
| `healthyBandLow` | 0.8 | Say/Do below 80% = At Risk |
| `healthyBandHigh` | 1.1 | Say/Do above 110% = over-committed |
| `carryThreshold` | 0.2 | Carry-forward above 20% triggers warning |

## Build for Production

```bash
npm run build
```

Output is in `.next/` directory. The app is configured for standalone output suitable for Docker.

## Run Production Build

```bash
npm run build
npm start
```

## Deploy to Vercel

The app is Vercel-ready out of the box:

1. Push to GitHub
2. Connect the repo to Vercel
3. Set the root directory to `tools/delivery-intelligence-portal`
4. Deploy

Or via CLI:

```bash
npx vercel
```

## Docker Deployment

```bash
docker build -t delivery-intelligence-portal .
docker run -p 3000:3000 delivery-intelligence-portal
```

## Troubleshooting

### Port already in use

```
Error: Port 3000 is already in use
```

**Fix:** Use a different port:

```bash
npm run dev -- -p 3001
```

### TypeScript errors after editing rawData.ts

Ensure all sprint objects match the `Sprint` interface:
- `committed`, `delivered`, `scopeAdded`, `carryForward` must be numbers
- `goalMet` must be boolean
- `team` must match a team `id` in the `teams` array

### Charts not rendering

**Fix:** Clear the `.next` cache and rebuild:

```bash
rm -rf .next
npm run dev
```

## Current Data

The portal currently contains data for:

- **9 teams** (5 ITC, 4 Cognizant)
- **8 sprints** (26.1.1 → 26.1.6, 26.2.1, 26.2.2)
- **72 sprint records** total
- **Date range**: March 2026 – July 2026
- **TPMs**: Blake Alce, Daniele Morisco, Nitin Ruparelia, Alex Callaway, Osman Mahmood
- **SMs**: Rizwan Vali Mohammed, Scott Norris, Robert Martin, Andrei Oiaga, Ciprian Muresan, Saumya Somanathan
