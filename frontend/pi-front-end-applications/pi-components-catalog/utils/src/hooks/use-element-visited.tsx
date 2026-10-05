'use client';

import { useSearchParams } from 'next/navigation';
import { useEffect, useState } from 'react';

export default function useElementVisited(elementId: string) {
  const searchParams = useSearchParams();
  const ARRdd = searchParams.get('ARRdd');

  const [elementVisited, setElementVisited] = useState(!ARRdd);

  useEffect(() => {
    const hasElementVisited = () => {
      const element = document.getElementById(elementId);
      if (element && window.scrollY > element.getBoundingClientRect().top + element.offsetHeight) {
        setElementVisited(true);
      }
    };

    document.addEventListener('scroll', hasElementVisited);

    return () => {
      document.removeEventListener('scroll', hasElementVisited);
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return elementVisited;
}
