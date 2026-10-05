import { KeyboardEvent } from 'react';

export function onActivationKeyDown(action: () => void) {
  return (e: KeyboardEvent<HTMLElement>) => {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      action();
    }
  };
}

export function onPanelEscapeKeyDown(onClose: () => void) {
  return (e: KeyboardEvent<HTMLDivElement>) => {
    if (e.key === 'Escape') {
      onClose();
    }
  };
}
