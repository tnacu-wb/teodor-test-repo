# Digital Opera Test Data Update

Next.js application for updating Oracle Opera Cloud PMS data via OHIP APIs.

## Quick Start

```bash
cd tools/opera-ohip-app
npm install
cp .env.example .env.local
# Edit .env.local with your OHIP credentials
npm run dev
```

Open http://localhost:3000

## Features

- Update Daily Rates (auto-batches large room type lists)
- Clear Hotel Restrictions (date range support)
- Update Hotel Availability / Sell Limits
  - **Room Type level** — free-text code value (e.g. FMTRPL, FMTACC, DOUBLE)
  - **Room Class level** — dropdown selection (ST, PP, BG, SV, PV, BV, SE)
- **Update Package Code** — select package from dropdown, set unit price, all other fields auto-fill
- Multi-environment support (UAT, SIT, PERF)
- Docker deployment ready
- Health check endpoints (/api/ping, /api/health)
- SSRF protection via input validation

## Update Package Code

Select a package code from the dropdown and only enter the new unit price. All other fields (description, posting rhythm, price calculation rule, inventory items, etc.) are auto-populated from the stored package configuration.

### User Inputs

| Field               | Required | Notes                   |
| ------------------- | -------- | ----------------------- |
| Hotel ID            | Yes      | e.g. HEAPTI             |
| Package Code        | Yes      | Dropdown — 30+ packages |
| Unit Price          | Yes      | New price in GBP        |
| Schedule Start Date | Yes      | Defaults to 2022-12-01  |
| Schedule End Date   | Yes      | Defaults to 2045-12-21  |

### Auto-filled Fields (from package config)

- Description & short description
- Posting rhythm (EveryNight, ArrivalNight, LastNight)
- Price calculation rule (FlatRate, PerRoom, PerAdult)
- Web bookable flag
- Inventory items
- Add to rate, print separate line, sell separate, etc.

### API Endpoint

```
PUT /rtp/v0/hotels/{hotelId}/packages/{packageCode}
```

### Example Payload

```json
{
  "packageCode": {
    "header": {
      "primaryDetails": { "description": "Meal Deal Dinner", "shortDescription": "Meal Deal Dinner" },
      "transactionDetails": {
        "allowance": false,
        "packagePostingRules": { "transactionCode": { "code": "156", "type": "Inclusive" } }
      },
      "postingAttributes": {
        "inventoryItems": [],
        "addToRate": false,
        "printSeparateLine": true,
        "sellSeparate": true,
        "postNextDay": false,
        "forecastNextDay": false,
        "webBookable": true,
        "formulaFunctionArguments": [],
        "catering": false,
        "postingRhythm": { "type": "EveryNight" },
        "priceCalculationRule": "FlatRate"
      }
    },
    "schedules": [
      {
        "newTimeSpan": { "startDate": "2022-12-01", "endDate": "2045-12-21" },
        "schedulePrices": [{ "unitPrice": "8" }, { "bucket": "Bucket2" }, { "bucket": "Bucket3" }],
        "newMinNights": "1",
        "newMaxNights": "999",
        "newMinPersons": "1",
        "newMaxPersons": "4"
      }
    ],
    "hotelId": "HEAPTI",
    "code": "MD2DIN",
    "adjustOverlappingRange": true
  }
}
```

## Sell Limits — Code Category Options

| Code Category | Code Values                    | Input Type |
| ------------- | ------------------------------ | ---------- |
| Room Type     | Any room type code (free text) | Text input |
| Room Class    | ST, PP, BG, SV, PV, BV, SE     | Dropdown   |

### Room Class Values

| Code | Description     |
| ---- | --------------- |
| ST   | Standard        |
| PP   | Premier Plus    |
| BG   | Bigger          |
| SV   | Superior        |
| PV   | Premier View    |
| BV   | Bigger View     |
| SE   | Suite/Executive |

### Example Payload (Room Class)

```json
{
  "sellLimitsByDateRange": [
    {
      "sellLimitDateRanges": [
        {
          "actionType": "SET_AVAILABLE",
          "startDate": "2026-08-19",
          "endDate": "2026-08-22",
          "sunday": true,
          "monday": true,
          "tuesday": true,
          "wednesday": true,
          "thursday": true,
          "friday": true,
          "saturday": true,
          "amount": "2",
          "flatOrPercentage": "F"
        }
      ],
      "hotelId": "HOTEL1",
      "codeCategory": "roomClass",
      "codeValue": "ST"
    }
  ]
}
```
