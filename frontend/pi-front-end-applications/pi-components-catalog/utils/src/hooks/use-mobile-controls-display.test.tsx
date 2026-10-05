import { renderHook } from '@testing-library/react';
import { FT_PI_BB_CONTROLS_MOBILE_DISPLAY } from '@whitbread-eos/api';

import useFeatureToggle from './use-feature-toggle';
import useMobileControlsDisplay from './use-mobile-controls-display';
import { useScreenSize } from './use-screensize';

jest.mock('./use-feature-toggle');
jest.mock('./use-screensize');

describe('useMobileControlsDisplay', () => {
  const mockUseFeatureToggle = useFeatureToggle as jest.Mock;
  const mockUseScreenSize = useScreenSize as jest.Mock;

  beforeEach(() => {
    jest.resetAllMocks();
  });

  it('returns false if channel is undefined', () => {
    mockUseFeatureToggle.mockReturnValue({ [FT_PI_BB_CONTROLS_MOBILE_DISPLAY]: true });
    mockUseScreenSize.mockReturnValue({ isLessThanSm: false, isLessThanMd: false });

    const { result } = renderHook(() => useMobileControlsDisplay());
    expect(result.current).toBe(false);
  });

  it('returns false if feature toggle is disabled', () => {
    mockUseFeatureToggle.mockReturnValue({ [FT_PI_BB_CONTROLS_MOBILE_DISPLAY]: false });
    mockUseScreenSize.mockReturnValue({ isLessThanSm: true, isLessThanMd: true });

    const { result } = renderHook(() => useMobileControlsDisplay('pi'));
    expect(result.current).toBe(false);
  });

  it('returns false if channel is not in the valid list', () => {
    mockUseFeatureToggle.mockReturnValue({ [FT_PI_BB_CONTROLS_MOBILE_DISPLAY]: true });
    mockUseScreenSize.mockReturnValue({ isLessThanSm: true, isLessThanMd: true });

    const { result } = renderHook(() => useMobileControlsDisplay('xx'));
    expect(result.current).toBe(false);
  });

  it('returns false if screen is not mobile', () => {
    mockUseFeatureToggle.mockReturnValue({ [FT_PI_BB_CONTROLS_MOBILE_DISPLAY]: true });
    mockUseScreenSize.mockReturnValue({ isLessThanSm: false, isLessThanMd: false });

    const { result } = renderHook(() => useMobileControlsDisplay('pi'));
    expect(result.current).toBe(false);
  });

  it('returns true if feature toggle is enabled, valid channel, and mobile screen', () => {
    mockUseFeatureToggle.mockReturnValue({ [FT_PI_BB_CONTROLS_MOBILE_DISPLAY]: true });
    mockUseScreenSize.mockReturnValue({ isLessThanSm: false, isLessThanMd: true });

    const { result } = renderHook(() => useMobileControlsDisplay('BB'));
    expect(result.current).toBe(true);
  });

  it('handles lowercase/uppercase channel names correctly', () => {
    mockUseFeatureToggle.mockReturnValue({ [FT_PI_BB_CONTROLS_MOBILE_DISPLAY]: true });
    mockUseScreenSize.mockReturnValue({ isLessThanSm: true, isLessThanMd: false });

    const { result } = renderHook(() => useMobileControlsDisplay('Pi'));
    expect(result.current).toBe(true);
  });
});
