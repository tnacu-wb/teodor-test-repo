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

describe('Analytics Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should do nothing', () => {
    render(<Analytics />);
    expect(analytics.update).not.toHaveBeenCalled();
    expect(analytics.remove).not.toHaveBeenCalled();
  });

  it('should clear validation and set pageName on mount', () => {
    const mockPageName = 'Test Page';

    render(<Analytics pageName={mockPageName} />);
    expect(analytics.update).toHaveBeenCalledWith({ pageName: mockPageName });
    expect(analytics.remove).toHaveBeenCalledWith(['validation']);
  });

  it('should set invoices on mount', () => {
    const mockInvoices = 5;

    render(<Analytics invoices={mockInvoices} />);
    expect(analytics.update).toHaveBeenCalledWith({
      innBusiness: {
        invoices: mockInvoices,
      },
    });
  });

  it('should set validation when account is suspended', () => {
    render(<Analytics isSuspended />);
    expect(analytics.update).toHaveBeenCalledWith({
      validation: 'PIBA Account suspended,PIBA account exceeds limit',
    });
  });
});
