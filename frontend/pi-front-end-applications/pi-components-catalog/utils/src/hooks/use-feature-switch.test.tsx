import { renderHook } from '@testing-library/react';

import useFeatureSwitch from './use-feature-switch';

const featureSwitch1 = 'FEATURE_SWITCH_1';
const featureSwitch2 = 'FEATURE_SWITCH_2';
const featureSwitch3 = 'FEATURE_SWITCH_3';

const mockGetConfig = jest.fn();
const mockConfig = (value = 'true') => ({
  publicRuntimeConfig: {
    [featureSwitch1]: value,
    [`${featureSwitch3}_GB`]: value,
  },
});

jest.mock('next/config', () => () => mockGetConfig());

describe('use-feature-switch custom hook', () => {
  beforeEach(() => {
    jest.resetModules();
  });

  it.each([
    [featureSwitch1, 'true', true],
    [featureSwitch1, 'TRUE', true],
    [featureSwitch1, 'True', true],
    [featureSwitch1, 'tru', false],
    [featureSwitch1, 'false', false],
    [featureSwitch2, 'true', false],
  ])(
    `should return the correct status of a feature switch [Check %s when ${featureSwitch1} is %s]`,
    (key, value, expected) => {
      mockGetConfig.mockImplementation(() => mockConfig(value));
      const { result } = renderHook(() => useFeatureSwitch({ featureSwitchKey: key }));

      expect(result.current).toEqual(expected);
    }
  );

  it('should return fallback when publicRuntimeConfig is not available and fallback is provided', () => {
    mockGetConfig.mockReturnValue({});
    const { result } = renderHook(() =>
      useFeatureSwitch({ featureSwitchKey: featureSwitch1, fallbackValue: true })
    );

    expect(result.current).toEqual(true);
  });

  it('should return false when publicRuntimeConfig is not available and no fallback is provided', () => {
    mockGetConfig.mockReturnValue({});
    const { result } = renderHook(() => useFeatureSwitch({ featureSwitchKey: featureSwitch1 }));

    expect(result.current).toEqual(false);
  });

  it('should return correct status of a feature switch for the specified country', () => {
    mockGetConfig.mockImplementation(() => mockConfig('true'));
    const { result } = renderHook(() =>
      useFeatureSwitch({ featureSwitchKey: featureSwitch3, country: 'gb' })
    );

    expect(result.current).toEqual(true);
  });

  it('should return false when getConfig returns null', () => {
    mockGetConfig.mockReturnValue(null);
    const { result } = renderHook(() => useFeatureSwitch({ featureSwitchKey: featureSwitch1 }));

    expect(result.current).toEqual(false);
  });
});
