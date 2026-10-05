import { renderHook } from '@testing-library/react';

import useUpdateSearchParams from './use-update-search-params';

jest.mock('next/navigation', () => ({
  useSearchParams: () => {
    return '?a=1';
  },
}));

describe('useUpdateSearchParams', () => {
  it('should update the search params', () => {
    const { result } = renderHook(() => useUpdateSearchParams());
    const { updateSearchParams } = result.current;

    const newPath = updateSearchParams('a', '2');
    expect(newPath).toBe('?a=2');
  });
});
