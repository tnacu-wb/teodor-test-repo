import { renderHook } from '@testing-library/react';

import { BUNDLE_CHOICE_OPTIONS } from '../global-constants';
import useSoftBundles from './use-soft-bundles';

const classTargetVariant = BUNDLE_CHOICE_OPTIONS.class;
const roomOnlyVariant = BUNDLE_CHOICE_OPTIONS.roomOnly;

describe('useSoftBundles', () => {
  it('should return isSoftBundlesVisible as true when all conditions are met (featureFlag, rooms=1, children=0, cookieValue=class)', () => {
    const { result } = renderHook(() => useSoftBundles(true, classTargetVariant, 1, 0));
    expect(result.current.isSoftBundlesVisible).toBe(true);
  });

  it('should return isSoftBundlesVisible as true when all conditions are met (featureFlag, rooms=1, children=0, cookieValue=roomOnly)', () => {
    const { result } = renderHook(() => useSoftBundles(true, roomOnlyVariant, 1, 0));
    expect(result.current.isSoftBundlesVisible).toBe(true);
  });

  it('should return isSoftBundlesVisible as true when all conditions are met (featureFlag, rooms=1, children=0, cookieValue=rate)', () => {
    const { result } = renderHook(() => useSoftBundles(true, 'rate', 1, 0));
    expect(result.current.isSoftBundlesVisible).toBe(true);
  });

  it('should return isSoftBundlesVisible as false if featureFlag is false', () => {
    const { result } = renderHook(() => useSoftBundles(false, classTargetVariant, 1, 0));
    expect(result.current.isSoftBundlesVisible).toBe(false);
  });

  it('should return isSoftBundlesVisible as false if rooms is not 1', () => {
    const { result } = renderHook(() => useSoftBundles(true, classTargetVariant, 2, 0));
    expect(result.current.isSoftBundlesVisible).toBe(false);
  });

  it('should return isSoftBundlesVisible as false if children is not 0', () => {
    const { result } = renderHook(() => useSoftBundles(true, classTargetVariant, 1, 1));
    expect(result.current.isSoftBundlesVisible).toBe(false);
  });

  it('should return isSoftBundlesVisible as false if cookieValue is not class, rate, or roomOnly', () => {
    const { result } = renderHook(() => useSoftBundles(true, 'other', 1, 0));
    expect(result.current.isSoftBundlesVisible).toBe(false);
  });

  it('should default rooms to 1 and children to 0 if not provided', () => {
    const { result } = renderHook(() => useSoftBundles(true, classTargetVariant));
    expect(result.current.isSoftBundlesVisible).toBe(true);
  });
});
