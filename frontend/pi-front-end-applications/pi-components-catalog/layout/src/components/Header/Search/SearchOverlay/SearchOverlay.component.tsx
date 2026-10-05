'use client';

import { cn } from '@whitbread-eos/utils';
import { useEffect } from 'react';

interface SearchOverlayProps {
  isVisible: boolean;
  onClose: () => void;
}

const SearchOverlay = ({ isVisible, onClose }: Readonly<SearchOverlayProps>) => {
  useEffect(() => {
    if (!isVisible) return;

    const handleEscapeKey = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        onClose();
      }
    };

    document.addEventListener('keydown', handleEscapeKey);
    return () => {
      document.removeEventListener('keydown', handleEscapeKey);
    };
  }, [isVisible, onClose]);

  if (!isVisible) {
    return null;
  }

  return (
    <div
      className={overlayStyle}
      onClick={onClose}
      data-testid="IB-Search-Overlay"
      aria-hidden="true"
    />
  );
};

export default SearchOverlay;

const overlayStyle = cn(
  'fixed top-[--headerHeight] left-0 right-0 bottom-0 bg-black/50 z-[35]',
  'animate-in fade-in-0 duration-200',
  'mobile:hidden'
);
