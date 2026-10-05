import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { analytics } from '@whitbread-eos/utils';

import { Analytics } from './Analytics';

jest.mock('@whitbread-eos/utils', () => ({
  analytics: {
    update: jest.fn(),
  },
}));

describe('Analytics Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (window as any).analyticsData = {};
  });

  it('should update analytics with page name', () => {
    const pageName = 'Test Page';
    render(<Analytics pageName={pageName} />);

    expect(analytics.update).toHaveBeenCalledWith({
      pageName: 'Test Page',
    });
  });

  it('should not call analytics.update if pageName is undefined', () => {
    render(<Analytics />);
    expect(analytics.update).not.toHaveBeenCalled();
  });

  it('should spread current analyticsData into analytics.update', () => {
    (window as any).analyticsData = { foo: 'bar', validation: '' };
    render(<Analytics pageName="Another Page" />);
    expect(analytics.update).toHaveBeenCalledWith({
      foo: 'bar',
      validation: '',
      pageName: 'Another Page',
    });
  });

  it('should call analytics.remove with ["validation"] if validation exists', () => {
    (window as any).analyticsData = { validation: 'some error', foo: 'bar' };
    analytics.remove = jest.fn();
    render(<Analytics pageName="Validation Page" />);
    expect(analytics.remove).toHaveBeenCalledWith(['validation']);
  });

  it('should not call analytics.remove if validation is empty', () => {
    (window as any).analyticsData = { validation: '', foo: 'bar' };
    analytics.remove = jest.fn();
    render(<Analytics pageName="No Validation" />);
    expect(analytics.remove).not.toHaveBeenCalled();
  });

  it('should handle missing window.analyticsData gracefully', () => {
    delete (window as any).analyticsData;
    render(<Analytics pageName="Missing Data" />);
    expect(analytics.update).toHaveBeenCalledWith({
      pageName: 'Missing Data',
    });
  });
});
