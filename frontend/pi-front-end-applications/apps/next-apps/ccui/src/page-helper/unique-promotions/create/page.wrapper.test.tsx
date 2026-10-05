import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import * as React from 'react';

import { render } from '../../../utils/test-utils';
import { Page } from './index';

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  QueryClientProvider: ({ children }: { children: React.ReactNode }) => <>{children}</>,
}));

jest.mock('./page.ccui', () => ({
  __esModule: true,
  default: () => <div data-testid="CreateNewPromoCode-Form" />,
}));

describe('Page', () => {
  it('should render Create New Promo Code Page Wrapper', async () => {
    const queryClient = new QueryClient();

    const { findByTestId } = render(<Page queryClient={queryClient} />);

    expect(await findByTestId('CreateNewPromoCode-Form')).toBeInTheDocument();
  });
});
