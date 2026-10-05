import type { QueryClient } from '@tanstack/react-query';
import { QueryClientProvider } from '@tanstack/react-query';
import React from 'react';

export interface InjectedAppProps {
  queryClient: QueryClient;
}

export function appWrapper<P>(Component: React.FC<P>) {
  return function AppWrapperComponent({ queryClient, ...restOfProps }: P & InjectedAppProps) {
    return (
      <QueryClientProvider client={queryClient}>
        <Component {...(restOfProps as unknown as P & InjectedAppProps)} />
      </QueryClientProvider>
    );
  };
}
