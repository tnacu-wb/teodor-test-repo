import React from 'react';

import { render } from '../utils/test-utils';
import useElementDimensions from './use-element-dimensions';

const mockReferenceDimensions = { width: 30, height: 20 };
let mockDimensions = { width: 0, height: 0 };
let mockSelector = 'header';

global.ResizeObserver = jest.fn().mockImplementation((callback: () => void) => ({
  observe: () => callback(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

const Component = () => {
  mockDimensions = useElementDimensions(mockSelector);

  return <header />;
};

describe('useElementDimensions', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockSelector = 'header';

    Object.defineProperty(HTMLElement.prototype, 'offsetWidth', {
      configurable: true,
      value: mockReferenceDimensions.width,
    });
    Object.defineProperty(HTMLElement.prototype, 'offsetHeight', {
      configurable: true,
      value: mockReferenceDimensions.height,
    });
  });

  it('should return the header dimensions', () => {
    render(<Component />);

    expect(mockDimensions.width).toEqual(mockReferenceDimensions.width);
    expect(mockDimensions.height).toEqual(mockReferenceDimensions.height);
  });

  it('should not find the element', () => {
    mockSelector = 'footer';
    render(<Component />);

    expect(mockDimensions.width).toEqual(0);
    expect(mockDimensions.height).toEqual(0);
  });
});
