import getConfig from 'next/config';

import { akamaiImageLoader, isIVMEnabled } from './akamaiImageLoader';

jest.mock('next/config');

describe('akamaiImageLoader', () => {
  it('should return the correct URL with width parameter', () => {
    const props = { src: 'http://example.com/image.jpg', width: 500 };
    const result = akamaiImageLoader(props);
    expect(result).toBe('http://example.com/image.jpg?imwidth=500');
  });
});

describe('isIVMEnabled', () => {
  it('should return true when NEXT_IMAGE_UNOPTIMIZED is set to "true"', () => {
    (getConfig as jest.Mock).mockImplementation(() => ({
      publicRuntimeConfig: { NEXT_IMAGE_UNOPTIMIZED: 'true' },
    }));
    expect(isIVMEnabled()).toBe(true);
  });

  it('should return false when NEXT_IMAGE_UNOPTIMIZED is not set to "true"', () => {
    (getConfig as jest.Mock).mockImplementation(() => ({
      publicRuntimeConfig: { NEXT_IMAGE_UNOPTIMIZED: 'false' },
    }));
    expect(isIVMEnabled()).toBe(false);
  });

  it('should return false when NEXT_IMAGE_UNOPTIMIZED is not defined', () => {
    (getConfig as jest.Mock).mockImplementation(() => ({
      publicRuntimeConfig: {},
    }));
    expect(isIVMEnabled()).toBe(false);
  });

  it('should return false when getConfig returns false', () => {
    (getConfig as jest.Mock).mockImplementation(() => false);
    expect(isIVMEnabled()).toBe(false);
  });
});
