import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, RenderOptions } from '@testing-library/react';
import { FC, ReactElement } from 'react';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: false,
    },
  },
});

const AllTheProviders: FC = ({ children }) => {
  return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>;
};

const customRender = (ui: ReactElement, options?: Omit<RenderOptions, 'wrapper'>) =>
  render(ui, { wrapper: AllTheProviders, ...options });

// eslint-disable-next-line import-x/export -- intentionally overrides render with customRender below
export * from '@testing-library/react';
export * from 'jest-axe';
export { default as userEvent } from '@testing-library/user-event';
// eslint-disable-next-line import-x/export -- intentionally overrides render with customRender below
export { customRender as render };
