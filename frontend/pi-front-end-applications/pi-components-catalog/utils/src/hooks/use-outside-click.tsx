'use client';

import { useEffect, useRef, useState } from 'react';

export default function useOutsideClick() {
  const [isOpen, setIsOpen] = useState(false);
  const elementRef = useRef<HTMLDivElement | null>(null);
  const iconRef = useRef<HTMLImageElement | null>(null);

  useEffect(() => {
    const handleClickOutside = (event: TouchEvent | MouseEvent) => {
      const target = event.target as Node;
      if (
        isOpen &&
        elementRef.current &&
        iconRef.current &&
        !elementRef.current.contains(target) &&
        !iconRef.current.contains(target)
      ) {
        setIsOpen(false);
      }
    };

    document.addEventListener('touchend', handleClickOutside);
    document.addEventListener('mouseup', handleClickOutside);

    return () => {
      document.removeEventListener('touchend', handleClickOutside);
      document.removeEventListener('mouseup', handleClickOutside);
    };
  }, [isOpen]);

  return { isOpen, setIsOpen, elementRef, iconRef };
}
