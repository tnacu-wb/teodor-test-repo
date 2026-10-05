import { useUser } from '@auth0/nextjs-auth0';
import { useEffect } from 'react';

interface Auth0LoginWatcherProps {
  onChange: (isLoggedIn: boolean) => void;
}

// useUser() has no way to be conditionally disabled - it always fetches
// /auth/profile via SWR. UserContextProvider only mounts this (via the
// authWatcher slot) when FT_PI_AUTH0_LOGIN is on, keeping that request off
// the legacy cookie-flow path.
export function Auth0LoginWatcher({ onChange }: Readonly<Auth0LoginWatcherProps>) {
  const { user, isLoading } = useUser();

  useEffect(() => {
    if (!isLoading) {
      onChange(!!user);
    }
  }, [user, isLoading, onChange]);

  return null;
}
