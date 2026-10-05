import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import React from 'react';

import { render } from '../../utils/test-utils';
import SearchQueryWrapper from './SearchQueryWrapper';

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    asPath: '/search',
    query: {},
  }),
}));

describe('SearchQueryWrapper', () => {
  test('renders Search component with QueryClientProvider', () => {
    const queryClient = new QueryClient();

    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <SearchQueryWrapper queryClient={queryClient} />
      </QueryClientProvider>
    );
    expect(getByTestId('loading-message'));
  });
});
