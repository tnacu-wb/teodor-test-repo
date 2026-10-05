'use client';

import { useEffect, useState } from 'react';

function useElementDimensions(elementSelector: string) {
  const [dimensions, setDimensions] = useState({ width: 0, height: 0 });

  useEffect(() => {
    const element = document.querySelector(elementSelector) as HTMLElement;

    if (!element) {
      return;
    }

    const updateDimensions = () => {
      setDimensions({
        width: element.offsetWidth,
        height: element.offsetHeight,
      });
    };
    updateDimensions();

    const resizeObserver = new ResizeObserver(() => {
      updateDimensions();
    });

    resizeObserver.observe(element);

    return () => {
      resizeObserver.unobserve(element);
    };
  }, [elementSelector]);

  return dimensions;
}

export default useElementDimensions;
