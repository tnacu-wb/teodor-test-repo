'use client';

import { useSearchParams } from 'next/navigation';
import { useCallback } from 'react';

export default function useUpdateSearchParams() {
  const searchParams = useSearchParams();

  const updateSearchParams = useCallback(
    (name: string, value?: string) => {
      const params = new URLSearchParams(searchParams?.toString());
      if (value) {
        params.set(name, value);
      } else {
        params.delete(name);
      }

      return `?${params}`;
    },
    [searchParams]
  );

  return { updateSearchParams };
}
