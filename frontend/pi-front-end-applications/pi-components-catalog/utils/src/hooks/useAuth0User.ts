import { useQuery } from '@tanstack/react-query';

export interface UserProfile {
  email?: string;
  name?: string;
  nickname?: string;
  picture?: string;
}

interface UseAuth0UserResult {
  user: UserProfile | null;
  loading: boolean;
  error: Error | null;
  refetch: () => Promise<void>;
}

async function fetchAuth0User(): Promise<UserProfile | null> {
  const res = await fetch('/api/auth/user');
  if (!res.ok) return null;
  const data = await res.json();
  return data.user ?? null;
}

/**
 * Hook to fetch user profile from Auth0 session including metadata.
 * Calls /api/auth/user which uses auth0.getSession() server-side.
 * Only works when Auth0 is enabled (FT_PI_AUTH0_LOGIN).
 * For legacy mode, components should continue using decodeIdToken.
 */
export function useAuth0User(enabled = false): UseAuth0UserResult {
  const {
    data: user,
    isLoading,
    error,
    refetch,
  } = useQuery({
    queryKey: ['auth0User'],
    queryFn: fetchAuth0User,
    enabled,
    staleTime: 5 * 60 * 1000, // 5 min
    retry: 1,
  });

  return {
    user: user ?? null,
    loading: isLoading,
    error: error as Error | null,
    refetch: async () => {
      await refetch();
    },
  };
}
