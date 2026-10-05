import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import React from 'react';

import { render } from '../../utils/test-utils';
import AnalyticsProviderCCUI from './AnalyticsProviderCCUI';

const props = {
  children: <div data-testid="test">Test</div>,
};

describe('<AnalyticsProviderCCUI />', () => {
  const queryClient = new QueryClient();
  it('should render the component', () => {
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <AnalyticsProviderCCUI {...props} queryClient={queryClient} />
      </QueryClientProvider>
    );
    expect(getByTestId('test')).toBeInTheDocument();
  });
});
