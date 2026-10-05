import { act, renderHook } from '@testing-library/react';

import useFeatureToggle from './use-feature-toggle';

const mockSetFeatureToggles = jest.fn();

jest.mock('../store', () => ({
  useFeatureToggleData: jest.fn(() => ({
    featureToggles: { defaultFeature: false },
    setFeatureToggles: mockSetFeatureToggles,
  })),
}));

test('useFeatureToggle returns featureToggles without props', () => {
  const { result } = renderHook(() => useFeatureToggle());
  expect(result.current).toEqual({ defaultFeature: false });
});

test('useFeatureToggle merges props with featureToggles', () => {
  const { result } = renderHook(() => useFeatureToggle({ defaultFeature: true }));

  act(() => {
    result.current;
  });

  expect(mockSetFeatureToggles).toBeCalledWith({ defaultFeature: true });
});
