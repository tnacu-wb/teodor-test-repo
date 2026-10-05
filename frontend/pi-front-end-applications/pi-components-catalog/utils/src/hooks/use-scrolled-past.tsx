'use client';

import { useEffect, useState } from 'react';

import { isInnBusinessApp } from '../server/validators';

export default function useScrolledPast(elementId: string) {
  const [scrolledPast, setScrolledPast] = useState(false);

  useEffect(() => {
    const isInnBusiness = isInnBusinessApp(window?.location?.host ?? '');
    const mainElement = document.querySelector('main');

    const hasScrolledPastLastRateCard = () => {
      const referencedElementScroll = isInnBusiness ? mainElement?.scrollTop : window.scrollY;

      const element = document.getElementById(elementId);
      if (
        element &&
        referencedElementScroll &&
        referencedElementScroll > element.getBoundingClientRect().top + element.offsetHeight
      ) {
        setScrolledPast(true);
      } else {
        setScrolledPast(false);
      }
    };

    const eventListenerElement = isInnBusiness ? mainElement : document;

    eventListenerElement?.addEventListener('scroll', hasScrolledPastLastRateCard);

    return () => {
      eventListenerElement?.removeEventListener('scroll', hasScrolledPastLastRateCard);
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return scrolledPast;
}
