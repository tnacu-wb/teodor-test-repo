import { useState, useEffect, RefObject } from 'react';

export const useFloatingHeader = (tableHeaderRef: RefObject<HTMLTableSectionElement>) => {
  const [showFloatingHeader, setShowFloatingHeader] = useState(false);

  useEffect(() => {
    const headerEl = tableHeaderRef.current;
    if (!headerEl) return;

    const observer = new window.IntersectionObserver(
      ([entry]) => {
        // Show floating header only when the original header is out of view
        // and the intersection ratio is 0 (completely out of view from top)
        setShowFloatingHeader(!entry.isIntersecting && entry.boundingClientRect.top < 0);
      },
      {
        threshold: 0,
        rootMargin: '0px',
      }
    );

    observer.observe(headerEl);
    return () => observer.disconnect();
  }, [tableHeaderRef]);

  return { showFloatingHeader };
};
