import { QueryClient } from '@tanstack/react-query';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';

// Returns a Redis-persisted QueryClient when `enabled` is true (feature flag) and
// Redis env is configured; otherwise a plain QueryClient.
export function createServerQueryClient(options?: { page?: string; enabled?: boolean }) {
  return getPersistentQueryClient(options);
}

// A server-side QueryClient has no observers, so an unobserved query's gcTime
// timer is what keeps its cached data (and the QueryClient itself) reachable
// after the request completes - Node's timer table holds a live reference to
// the closure until it fires. Call this once dehydrate(queryClient) has been
// captured to release that data immediately instead of leaking it for up to
// gcTime after every request.
export function clearServerQueryClient(queryClient: QueryClient) {
  queryClient.clear();
}
