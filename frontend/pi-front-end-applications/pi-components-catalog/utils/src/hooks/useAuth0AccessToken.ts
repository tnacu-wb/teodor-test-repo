import { useQuery } from '@tanstack/react-query';

async function fetchAuth0AccessToken(): Promise<string | null> {
  const res = await fetch('/api/auth/access-token');
  if (!res.ok) return null;
  const data = await res.json();
  return data.accessToken ?? null;
}

/**
 * Fetches the Auth0 access token from the /api/auth/access-token Next.js API route.
 * That route uses auth0.getSession() server-side to safely retrieve the token.
 * Only enabled when `enabled` is true (i.e. FT_PI_AUTH0_LOGIN is on).
 * Safe to call from any component — returns null when disabled.
 */
export function useAuth0AccessToken(enabled = false) {
  const {
    data: accessToken,
    isLoading,
    error,
  } = useQuery({
    queryKey: ['auth0AccessToken'],
    queryFn: fetchAuth0AccessToken,
    enabled,
    staleTime: 4 * 60 * 1000, // 4 min (token refresh before 5 min expiry)
    // Stop polling once a fetch fails rather than hammering the endpoint every 4 min
    refetchInterval: (query) => (query.state.status === 'error' ? false : 4 * 60 * 1000),
    retry: 1,
  });

  return { accessToken: accessToken ?? null, isLoading, error };
}
