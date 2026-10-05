import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { analytics } from '@whitbread-eos/utils';

import Analytics from './analytics';

jest.mock('@whitbread-eos/utils', () => ({
  analytics: {
    update: jest.fn(),
  },
}));

describe('Analytics', () => {
  let originalWindow: any;

  beforeEach(() => {
    originalWindow = { ...window };
    (window as any).analyticsData = {};
    jest.clearAllMocks();
  });

  afterEach(() => {
    window.analyticsData = originalWindow.analyticsData;
  });

  it('should update pageName when pageName prop is provided', () => {
    (window as any).analyticsData = { foo: 'bar' };
    render(<Analytics pageName="TestPage" />);
    expect(analytics.update).toHaveBeenCalledWith({
      foo: 'bar',
      pageName: 'TestPage',
    });
  });

  it('should not update pageName if pageName prop is undefined', () => {
    render(<Analytics />);
    expect(analytics.update).not.toHaveBeenCalledWith(
      expect.objectContaining({ pageName: expect.anything() })
    );
  });

  it('renders nothing', () => {
    const { container } = render(<Analytics />);
    expect(container).toBeEmptyDOMElement();
  });
});
