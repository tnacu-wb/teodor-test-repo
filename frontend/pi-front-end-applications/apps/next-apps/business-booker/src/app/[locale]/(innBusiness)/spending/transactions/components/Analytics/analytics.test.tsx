import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { analytics } from '@whitbread-eos/utils';

import Analytics from './analytics';

jest.mock('@whitbread-eos/utils', () => ({
  analytics: {
    update: jest.fn(),
    remove: jest.fn(),
  },
}));

describe('Transactions Analytics', () => {
  const originalWindow = { ...window };

  beforeEach(() => {
    (window as any).analyticsData = {};
    jest.clearAllMocks();
  });

  afterEach(() => {
    window.analyticsData = originalWindow.analyticsData;
  });

  it('updates the page name and clears existing validation data', () => {
    (window as any).analyticsData = {
      foo: 'bar',
      validation: 'Old error',
    };

    render(<Analytics pageName="Transactions" />);

    expect(analytics.update).toHaveBeenCalledWith({
      foo: 'bar',
      pageName: 'Transactions',
    });
    expect(analytics.remove).toHaveBeenCalledWith(['validation']);
  });

  it('updates innBusiness transactions count', () => {
    (window as any).analyticsData = {
      innBusiness: {
        statements: 5,
      },
    };

    render(<Analytics transactionsCount={3} />);

    expect(analytics.update).toHaveBeenCalledWith({
      innBusiness: {
        statements: 5,
        transactions: 3,
      },
    });
  });

  it('does nothing when optional props are not provided', () => {
    render(<Analytics />);

    expect(analytics.update).not.toHaveBeenCalled();
    expect(analytics.remove).not.toHaveBeenCalled();
  });
});
