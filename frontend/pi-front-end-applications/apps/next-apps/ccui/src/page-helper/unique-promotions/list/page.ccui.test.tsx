import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import React, { ReactNode } from 'react';

import Page from './page.ccui';

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  PromoBatchesTable: () => (
    <table role="table">
      <thead>
        <tr>
          <th>Campaign</th>
          <th>Status</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>123</td>
          <td>Active</td>
        </tr>
      </tbody>
    </table>
  ),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: { children: ReactNode }) => (
    <div data-testid="error-boundary">{children}</div>
  ),
}));

const renderPage = (props: any = {}) => {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return render(<Page queryClient={queryClient} {...props} />);
};

describe('Page (PromoBatchTable)', () => {
  it('renders PromoBatchesTable inside QueryClientProvider and ErrorBoundary', () => {
    renderPage();

    expect(screen.getByRole('table')).toBeInTheDocument();
    expect(screen.getByText('Campaign')).toBeInTheDocument();
    expect(screen.getByText('Status')).toBeInTheDocument();
    expect(screen.getByTestId('error-boundary')).toBeInTheDocument();
  });

  it('does not crash when optional user prop is passed', () => {
    const mockUser = { sub: '12345', name: 'Test User' };

    expect(() => renderPage({ user: mockUser })).not.toThrow();
  });
});
