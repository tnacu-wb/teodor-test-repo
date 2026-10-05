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
    (window as any).analyticsData = { innBusiness: {} };
    (window as any)._satellite = { track: jest.fn() };
  });

  it('should update analytics with stays and bookings', () => {
    render(<Analytics stays={5} bookings={10} />);

    expect(analytics.update).toHaveBeenCalledWith({
      innBusiness: {
        stays: 5,
        bookings: 10,
      },
    });
  });

  it('should not update analytics if stays or bookings are undefined', () => {
    render(<Analytics stays={undefined} bookings={10} />);
    render(<Analytics stays={5} bookings={undefined} />);

    expect(analytics.update).not.toHaveBeenCalled();
  });

  it('should update analytics with applications', () => {
    render(<Analytics applications={3} />);

    expect(analytics.update).toHaveBeenCalledWith({
      innBusiness: {
        applications: 3,
      },
    });
  });

  it('should not update analytics if applications are undefined', () => {
    render(<Analytics applications={undefined} />);

    expect(analytics.update).not.toHaveBeenCalled();
  });

  it('should update analytics with notifications', () => {
    const notifications = {
      isAccountSuspended: true,
      isAccountClosed: false,
      profileUpdateRequired: true,
      hasAdHocNotification: true,
      employeeRequests: 3,
    };

    render(<Analytics notifications={notifications} />);

    expect(window._satellite.track).toHaveBeenCalledWith('error');
    expect(analytics.update).toHaveBeenCalledWith({
      innBusiness: {},
      validation:
        'PIBA Account suspended,PIBA account exceeds limit,BB Profile questions,Ad hoc BB notification,3 Employee requests',
    });
  });

  it('should not update analytics if notifications are undefined', () => {
    render(<Analytics notifications={undefined} />);

    expect(analytics.update).not.toHaveBeenCalled();
  });

  it('should not update analytics if no notifications are true', () => {
    const notifications = {
      isAccountSuspended: false,
      isAccountClosed: false,
      profileUpdateRequired: false,
      hasAdHocNotification: false,
    };

    render(<Analytics notifications={notifications} />);

    expect(window._satellite.track).not.toHaveBeenCalled();
    expect(analytics.update).not.toHaveBeenCalled();
  });

  it('should update analytics with pageName', () => {
    render(<Analytics pageName="home-page" />);

    expect(analytics.update).toHaveBeenCalledWith({
      innBusiness: {},
      pageName: 'home-page',
    });
  });

  it('should not update analytics if pageName is undefined', () => {
    render(<Analytics pageName={undefined} />);

    expect(analytics.update).not.toHaveBeenCalled();
  });

  it('should remove validation when pageName is provided and currentValidation exists', () => {
    (window as any).analyticsData = {
      innBusiness: {},
      validation: 'existing-validation',
    };

    render(<Analytics pageName="test-page" />);

    expect(analytics.update).toHaveBeenCalledWith({
      innBusiness: {},
      validation: 'existing-validation',
      pageName: 'test-page',
    });
    expect(analytics.remove).toHaveBeenCalledWith(['validation']);
  });

  it('should not remove validation when pageName is provided but no currentValidation exists', () => {
    (window as any).analyticsData = {
      innBusiness: {},
    };

    render(<Analytics pageName="test-page" />);

    expect(analytics.update).toHaveBeenCalledWith({
      innBusiness: {},
      pageName: 'test-page',
    });
    expect(analytics.remove).not.toHaveBeenCalled();
  });

  it('should handle account closed notification', () => {
    const notifications = {
      isAccountSuspended: false,
      isAccountClosed: true,
      profileUpdateRequired: false,
      hasAdHocNotification: false,
    };

    render(<Analytics notifications={notifications} />);

    expect(window._satellite.track).toHaveBeenCalledWith('error');
    expect(analytics.update).toHaveBeenCalledWith({
      innBusiness: {},
      validation: 'PIBA Account closed',
    });
  });
});
