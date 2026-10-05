'use client';

import { useEffect, useRef, useState } from 'react';

import { ReviewChangesModal } from './review-changes-modal';

type Props = {
  isOpen?: boolean;
  onDiscard?: () => void;
  onContinue?: () => void;
  navigationGuardEnabled?: boolean;
  alwaysShowOnExternalNavigation?: boolean;
  dialogTitle?: string;
  dialogDescription?: string;
  testId?: string;
};

export function ReviewChanges({
  isOpen,
  onDiscard,
  onContinue,
  navigationGuardEnabled = true,
  alwaysShowOnExternalNavigation = false,
  dialogTitle,
  dialogDescription,
  testId,
}: Readonly<Props>) {
  const [pendingLink, setPendingLink] = useState<HTMLAnchorElement | null>(null);

  const bypassRef = useRef(false);

  useEffect(() => {
    if (!navigationGuardEnabled || isOpen === true) return;

    const handleClick = (e: MouseEvent) => {
      if (bypassRef.current) return;

      const link = (e.target as HTMLElement).closest('a[href]') as HTMLAnchorElement | null;
      if (!link) return;
      if (e.metaKey || e.ctrlKey || e.shiftKey || e.altKey || e.button !== 0) return;

      const href = link.getAttribute('href');
      if (!href || href.startsWith('http') || href.startsWith('//') || href.startsWith('#')) return;
      if (link.target && link.target !== '_self') return;
      if (link.hasAttribute('download')) return;
      if (href.includes('access-restricted')) return;
      if (alwaysShowOnExternalNavigation && href.includes('/business-pay/apply/')) return;

      e.preventDefault();
      e.stopPropagation();
      e.stopImmediatePropagation();
      setPendingLink(link);
    };

    document.addEventListener('click', handleClick, true);
    return () => document.removeEventListener('click', handleClick, true);
  }, [navigationGuardEnabled, isOpen, alwaysShowOnExternalNavigation]);

  const isManualControlActive = isOpen === true;

  const handleDiscard = () => {
    if (pendingLink) {
      const link = pendingLink;
      setPendingLink(null);
      bypassRef.current = true;
      link.click();
      bypassRef.current = false;
    }
  };

  const handleContinue = () => {
    setPendingLink(null);
  };

  return (
    <ReviewChangesModal
      testId={testId || 'ReviewChanges'}
      open={isOpen || pendingLink !== null}
      onOpenChange={isManualControlActive ? onContinue : handleContinue}
      onDiscard={isManualControlActive ? onDiscard : handleDiscard}
      onContinue={isManualControlActive ? onContinue : handleContinue}
      dialogTitle={dialogTitle}
      dialogDescription={dialogDescription}
    />
  );
}
