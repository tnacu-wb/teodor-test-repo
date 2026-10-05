import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { analytics } from '@whitbread-eos/utils';

import Analytics from './analytics';

jest.mock('@whitbread-eos/utils', () => ({
  analytics: {
    update: jest.fn(),
  },
}));

describe('Analytics Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (window as any).analyticsData = { innBusiness: {} };
    (window as any)._satellite = { track: jest.fn() };
  });

  it('should update analytics with notifications', () => {
    const notifications = {
      isAccountSuspended: true,
      hasUpcomingSpending: false,
    };

    render(<Analytics notifications={notifications} />);

    expect(window._satellite.track).toHaveBeenCalledWith('error');
    expect(analytics.update).toHaveBeenCalledWith({
      innBusiness: {},
      validation: 'PIBA Account suspended,PIBA account exceeds limit',
    });
  });

  it('should not update analytics if notifications are undefined', () => {
    render(<Analytics notifications={undefined} />);

    expect(analytics.update).not.toHaveBeenCalled();
  });

  it('should not update analytics if no notifications are true', () => {
    const notifications = {
      isAccountSuspended: false,
    };

    render(<Analytics notifications={notifications} />);

    expect(window._satellite.track).not.toHaveBeenCalled();
    expect(analytics.update).not.toHaveBeenCalled();
  });

  it('should update analytics with validations', () => {
    const validations = ['Validation error 1', 'Validation error 2'];

    render(<Analytics validations={validations} />);

    expect(window._satellite.track).toHaveBeenCalledWith('error');
    expect(analytics.update).toHaveBeenCalledWith({
      innBusiness: {},
      validation: 'Validation error 1,Validation error 2',
    });
  });

  it('should not update analytics if validations is undefined', () => {
    render(<Analytics validations={undefined} />);

    expect(window._satellite.track).not.toHaveBeenCalled();
    expect(analytics.update).not.toHaveBeenCalled();
  });

  it('should not update analytics if validations is an empty array', () => {
    render(<Analytics validations={[]} />);

    expect(window._satellite.track).not.toHaveBeenCalled();
    expect(analytics.update).not.toHaveBeenCalled();
  });
});
