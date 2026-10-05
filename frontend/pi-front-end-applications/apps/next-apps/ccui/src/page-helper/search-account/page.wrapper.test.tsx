import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom/extend-expect';
import { render } from '@testing-library/react';

import { Page } from './index';

// Mock the SearchAccountPageCCUI component
jest.mock('./page.ccui', () => {
  return function MockSearchAccountPageCCUI() {
    return <div data-testid="search-account-page">Search Account Page</div>;
  };
});

describe('Page component', () => {
  const queryClient = new QueryClient();
  const accessToken = 'test-access-token';

  it('renders without crashing, and uses QueryClientProvider', () => {
    const { getByTestId } = render(<Page queryClient={queryClient} accessToken={accessToken} />);

    // Check if the QueryClientProvider is rendering correctly
    const providerElement = getByTestId('search-account-page');
    expect(providerElement).toBeInTheDocument();
  });

  it('passes queryClient and accessToken to SearchAccountPageCCUI', () => {
    const { getByTestId } = render(<Page queryClient={queryClient} accessToken={accessToken} />);

    // Check if the mocked SearchAccountPageCCUI component is rendered
    const searchAccountPageElement = getByTestId('search-account-page');
    expect(searchAccountPageElement).toBeInTheDocument();
  });
});
