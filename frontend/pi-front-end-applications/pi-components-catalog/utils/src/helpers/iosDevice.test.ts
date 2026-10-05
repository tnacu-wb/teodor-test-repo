import { isIOSDevice } from './iosDevice';

describe('isIOSDevice', () => {
  const originalNavigator = global.navigator;

  beforeEach(() => {
    // Reset navigator to original
    Object.defineProperty(global, 'navigator', {
      value: { ...originalNavigator },
      writable: true,
    });
  });

  afterEach(() => {
    // Restore original navigator
    Object.defineProperty(global, 'navigator', {
      value: originalNavigator,
      writable: true,
    });
  });

  it('should return true when userAgentData.platform is "iOS"', () => {
    Object.defineProperty(global.navigator, 'userAgentData', {
      value: { platform: 'iOS' },
      writable: true,
    });
    Object.defineProperty(global.navigator, 'userAgent', {
      value: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
      writable: true,
    });

    expect(isIOSDevice()).toBe(true);
  });

  it('should return true when userAgent contains "iPhone"', () => {
    Object.defineProperty(global.navigator, 'userAgentData', {
      value: undefined,
      writable: true,
    });
    Object.defineProperty(global.navigator, 'userAgent', {
      value: 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)',
      writable: true,
    });

    expect(isIOSDevice()).toBe(true);
  });

  it('should return true when userAgent contains "iPad"', () => {
    Object.defineProperty(global.navigator, 'userAgentData', {
      value: undefined,
      writable: true,
    });
    Object.defineProperty(global.navigator, 'userAgent', {
      value: 'Mozilla/5.0 (iPad; CPU OS 14_0 like Mac OS X)',
      writable: true,
    });

    expect(isIOSDevice()).toBe(true);
  });

  it('should return true when userAgent contains "iPod"', () => {
    Object.defineProperty(global.navigator, 'userAgentData', {
      value: undefined,
      writable: true,
    });
    Object.defineProperty(global.navigator, 'userAgent', {
      value: 'Mozilla/5.0 (iPod; CPU iPhone OS 14_0 like Mac OS X)',
      writable: true,
    });

    expect(isIOSDevice()).toBe(true);
  });

  it('should return false when userAgentData.platform is not "iOS" and userAgent does not match iOS devices', () => {
    Object.defineProperty(global.navigator, 'userAgentData', {
      value: { platform: 'Android' },
      writable: true,
    });
    Object.defineProperty(global.navigator, 'userAgent', {
      value: 'Mozilla/5.0 (Linux; Android 10; SM-G975F)',
      writable: true,
    });

    expect(isIOSDevice()).toBe(false);
  });

  it('should return false when userAgentData is undefined and userAgent does not match iOS devices', () => {
    Object.defineProperty(global.navigator, 'userAgentData', {
      value: undefined,
      writable: true,
    });
    Object.defineProperty(global.navigator, 'userAgent', {
      value: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
      writable: true,
    });

    expect(isIOSDevice()).toBe(false);
  });
});
