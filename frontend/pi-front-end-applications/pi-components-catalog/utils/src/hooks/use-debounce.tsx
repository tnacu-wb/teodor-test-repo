'use client';

import debounce from 'lodash/debounce';
import { useEffect, useMemo, useRef } from 'react';

export default function useDebounce(callback: () => void, delay = 500) {
  const ref = useRef<any>(undefined);

  useEffect(() => {
    ref.current = callback;
  }, [callback]);

  const debouncedCallback = useMemo(() => {
    const func = () => {
      ref.current?.();
    };

    return debounce(func, delay);
  }, []);

  return debouncedCallback;
}
