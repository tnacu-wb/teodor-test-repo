import '@testing-library/jest-dom';

import { revalidateCacheOnLink } from './revalidate-link';

jest.mock('next/cache', () => ({
  revalidatePath: (path: string) => {
    return path;
  },
}));

describe('revalidateCacheOnLink', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call revalidateCacheOnLink', async () => {
    const result = await revalidateCacheOnLink('/test-path');
    expect(result).toBe(undefined);
  });
});
