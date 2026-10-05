/**
 * GraphQL API Client Library — Public API
 *
 * Centralized entry point for all API client modules. This barrel export provides
 * convenient access to the GraphQL client, domain-specific API helpers, factory
 * functions, error classes, and typed response/input interfaces.
 *
 * All exports are named (no default exports) for tree-shaking compatibility.
 *
 * @module api
 *
 * @example Importing types and helpers
 * ```typescript
 * import { BasketAPI, ContentAPI, AvailabilityHelpers } from '@api';
 * import type { AvailabilityCriteria, BookingConfirmationResponse } from '@api';
 * ```
 */

// ─── Core Client ────────────────────────────────────────────────────────────────
export { GraphQLClient, GraphQLError, ValidationError } from './graphql/graphqlClient';
export type { GraphQLClientOptions } from './graphql/graphqlClient';

// ─── API Modules ────────────────────────────────────────────────────────────────
export { ApiBookingConfirmationHelpers } from './graphql/bookingConfirmationHelpers';

// ─── API Models ───────────────────────────────────────────────────────
export { ApiFixtures } from './apiFixtures';
export type { ApiFixtureJsonData } from './apiFixtures';
export * from './graphql/apiBasketCalls';
export * from './graphql/apiCalls';
export * from './graphql/apiContentCalls';
export * from './apiHelpers';
export * from './graphql/apiReservationCalls';
export * from './graphql/apiSlugsCalls';
export * from './authZeroApiCalls';
export * from './graphql/bookingConfirmationHelpers';
export * from './entityApiCalls';
export * from './ohip/ohipHelpers';
export * as ApiRequests from './requests';
export * as ApiResponses from './response';

// ─── AEM Dictionary (Direct REST) ──────────────────────────────────────────────
export { ApiDictionary } from './aem/apiDictionary';
