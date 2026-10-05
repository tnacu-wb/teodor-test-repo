# GraphQL API Client Library

A shared, queries-only GraphQL client library for the Playwright + TypeScript QA framework. Provides reusable GraphQL communication, typed query wrappers, and validation helpers.

## Quick Start

```typescript
import { createGraphQLClient, createContentAPI, createAvailabilityHelpers } from '@api';

test('verify hotel content', async ({ request }) => {
  const client = createGraphQLClient(request);
  const contentApi = createContentAPI(client);

  const dlp = await contentApi.getDlpContent('/gb/en/hotels/england/west-sussex.html');
  expect(dlp.hotels.length).toBeGreaterThan(0);
});
```

## Modules

| Module | Import | Purpose |
|--------|--------|---------|
| GraphQLClient | `createGraphQLClient(request)` | Core typed query execution with retry logic |
| ContentAPI | `createContentAPI(client)` | DLP content, hotel info, TripAdvisor reviews |
| BasketAPI | `createBasketAPI(client)` | Basket status retrieval and validation |
| BookingConfirmationHelpers | `createBookingConfirmationHelpers(client)` | Booking confirmation validation |
| AvailabilityHelpers | `createAvailabilityHelpers(client)` | Hotel availability, meals, booking flow |

## Environment Setup

Set this environment variable (or add to a `.env` file in the `qa/` directory):

```bash
ENV=uat                    # or 'dit' — selects the API endpoint
```

The GraphQL API endpoint does not require API key headers. The `ENV` variable determines which environment URL is used (`uat` or `dit`). If unset, it defaults to `uat`.

## Running Library Tests

```bash
# Run all API library tests (unit + integration)
npm run test:api

# Run only mock/fixture tests (fast, no network required)
npm run test:api:unit

# Run the full E2E browser tests (separate from API library)
npm test
```

## Usage in Test Specs

### Availability Pre-check in beforeAll

```typescript
import { createGraphQLClient, createAvailabilityHelpers } from '@api';
import type { AvailabilityResponse } from '@api';

let availability: AvailabilityResponse;

test.beforeAll(async ({ request }) => {
  const client = createGraphQLClient(request);
  const helpers = createAvailabilityHelpers(client);

  availability = await helpers.ensureHotelAvailability('GATGAT', {
    startDate: '2025-07-01',
    endDate: '2025-07-03',
    rooms: [{ adults: 2, children: 0 }],
    adults: 2,
    children: 0,
  });
});
```

### Booking Confirmation Validation

```typescript
import { createGraphQLClient, createBookingConfirmationHelpers } from '@api';

test('validate booking details', async ({ request }) => {
  const client = createGraphQLClient(request);
  const helpers = createBookingConfirmationHelpers(client);

  const booking = await helpers.getBookingConfirmation(basketReference);
  await helpers.validateHotel(booking, { id: 'GATGAT', name: 'London Gatwick Airport' });
  await helpers.validateStayingDates(booking, { arrivalDate: '2025-07-01', departureDate: '2025-07-03' });
  await helpers.validateCurrency(booking, 'GBP');
  await helpers.validatePaymentCard(booking, 'card', '4111111111111234');
});
```

### Basket Status Polling

```typescript
import { createGraphQLClient, createBasketAPI } from '@api';

test('wait for basket completion', async ({ request }) => {
  const client = createGraphQLClient(request);
  const basket = createBasketAPI(client);

  // Polls with exponential backoff (1s -> 2s -> 4s), max 30s
  await basket.waitForBasketStatus(basketRef, 'COMPLETED');
});
```

## Troubleshooting

### "Unsupported ENV value"

The `ENV` variable must be `'uat'` or `'dit'`. If unset, it defaults to `'uat'`.

### 5xx Retries

The client automatically retries HTTP 5xx and GraphQL errors with `extensions.errorType` starting with "5". Retries use exponential backoff (1s -> 2s -> 4s) with a default max of 3 retries. If all retries fail, the error includes full context (operation, variables, response).

### Timeouts

The default timeout is 30 seconds per request. If you see timeout errors, the API may be slow or unresponsive. The timeout is configurable via `GraphQLClientOptions`:

```typescript
new GraphQLClient(request, url, { timeout: 60000 }); // 60s timeout
```

### "No availability found for hotel"

The `ensureHotelAvailability` helper advances dates by 1 day across 7 attempts. If no availability is found, check:
- Is the hotel open for the requested dates?
- Does UAT have inventory for the requested room types (DB for double, FM for family)?
- The error logs the hotel inventory — check what's actually available.
