# Agile Delivery Intelligence Portal

Interactive dashboard for Whitbread Digital Delivery — Scrum & Jira analytics across all squads and vendors.

## Features

- **10 report views**: Executive Overview, Delivery Performance, Trend Analysis, Sprint Analytics, Jira Insights, Team Performance, Resource & Workload, Risk & Improvement, Data Register, Data Quality
- **Single source of truth**: All metrics calculated from one central data object
- **Dynamic filtering**: Filter by Team, Sprint, or Vendor — all views update instantly
- **Data import**: Upload JSON or CSV files to update dashboard data in-browser
- **CSV export**: Export the full data register
- **Present mode**: Fullscreen presentation with keyboard navigation
- **Responsive**: Desktop, tablet, and mobile layouts

## Quick Start

```bash
cd tools/delivery-intelligence-portal
npm install
npm run dev
```

Open **http://localhost:3000**

## Tech Stack

| Technology | Purpose |
|-----------|---------|
| Next.js 15 | App Router, React Server Components |
| TypeScript | Type safety |
| Tailwind CSS | Styling |
| Recharts | Charts and visualisations |
| Lucide React | Icons |

## Project Structure

```
delivery-intelligence-portal/
├── app/
│   ├── globals.css          # Tailwind + brand CSS variables
│   ├── layout.tsx           # Root layout with Inter font
│   └── page.tsx             # Main client component (state, filters, routing)
├── components/
│   ├── Sidebar.tsx          # Left navigation (3 groups, 10 views)
│   ├── TopBar.tsx           # Header with filters, nav, present mode
│   ├── KpiCard.tsx          # Reusable KPI tile
│   ├── ChartCard.tsx        # Chart wrapper with title
│   ├── InsightCard.tsx      # Textual insight card
│   ├── DataImport.tsx       # JSON/CSV upload modal
│   ├── DataTable.tsx        # Sortable/filterable table
│   ├── Drawer.tsx           # Right-side detail panel
│   ├── FilterChips.tsx      # Active filter badges
│   └── Gauge.tsx            # Circular progress indicator
├── features/
│   ├── executive/           # Executive Overview
│   ├── delivery/            # Delivery Performance
│   ├── trend/               # Trend Analysis
│   ├── sprint/              # Sprint Analytics
│   ├── jira/                # Jira Insights
│   ├── team/                # Team Performance
│   ├── resource/            # Resource & Workload
│   ├── risk/                # Risk & Improvement
│   ├── register/            # Data Register
│   └── quality/             # Data Quality
├── lib/
│   ├── rawData.ts           # Central data source (all sprint/team data)
│   ├── types.ts             # TypeScript interfaces
│   ├── metrics.ts           # Metrics calculation engine
│   ├── formatters.ts        # Display formatters (pct, pts, band)
│   ├── chartHelpers.ts      # Chart colours and utilities
│   └── insights.ts          # Auto-generated textual insights
├── package.json
├── tailwind.config.ts
├── tsconfig.json
└── next.config.js
```

## Keyboard Shortcuts

| Key | Action |
|-----|--------|
| `→` | Next view |
| `←` | Previous view |
| `F` | Toggle present mode |
| `Escape` | Close drawer / exit present mode |

## Updating Data

### Option 1: Edit rawData.ts directly

Open `lib/rawData.ts` and update the `sprints` array. All metrics recalculate automatically.

### Option 2: Use the Import UI

1. Click the **Import** button in the top bar
2. Upload a JSON or CSV file
3. Review the validation summary
4. Click **Import Data**

### Option 3: Download a template

Click **Import → Templates** to download CSV or JSON templates showing the expected format.

## Deployment

### Vercel

```bash
npm run build
# Deploy via Vercel CLI or Git integration
```

### Docker

```bash
docker build -t delivery-intelligence-portal .
docker run -p 3000:3000 delivery-intelligence-portal
```

## Brand Colours

| Token | Hex | Usage |
|-------|-----|-------|
| brand | #5c2d82 | Primary purple |
| brand-2 | #7b3faf | Hover/accent purple |
| good | #1f9d6b | Healthy / on-track |
| warn | #c8811a | Warning / attention |
| bad | #cf3b45 | At risk / critical |
| info | #2f6fed | Informational |
