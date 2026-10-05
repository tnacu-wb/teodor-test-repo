import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { analytics } from '@whitbread-eos/utils';

import { Analytics } from './Analytics';

jest.mock('@whitbread-eos/utils', () => ({
  analytics: {
    update: jest.fn(),
  },
}));

describe('Cost centres Analytics', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (window as any).analyticsData = {};
  });

  it('updates analytics with page name and default validation', () => {
    render(<Analytics pageName="Cost Centre Management" />);

    expect(analytics.update).toHaveBeenCalledWith({
      pageName: 'Cost Centre Management',
      validation: '',
    });
  });

  it('spreads current analyticsData into analytics update', () => {
    (window as any).analyticsData = { foo: 'bar' };
    render(<Analytics pageName="Cost Centre Management" />);

    expect(analytics.update).toHaveBeenCalledWith({
      foo: 'bar',
      pageName: 'Cost Centre Management',
      validation: '',
    });
  });

  it('uses provided validation', () => {
    render(<Analytics pageName="Cost Centre Management" validation="Some warning" />);

    expect(analytics.update).toHaveBeenCalledWith({
      pageName: 'Cost Centre Management',
      validation: 'Some warning',
    });
  });

  it('does not update analytics when pageName is undefined', () => {
    render(<Analytics />);

    expect(analytics.update).not.toHaveBeenCalled();
  });

  it('handles missing window.analyticsData gracefully', () => {
    delete (window as any).analyticsData;
    render(<Analytics pageName="Cost Centre Management" />);

    expect(analytics.update).toHaveBeenCalledWith({
      pageName: 'Cost Centre Management',
      validation: '',
    });
  });
});
