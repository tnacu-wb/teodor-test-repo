import { useEffect, useRef, MutableRefObject } from 'react';

type UseIframeProps = {
  iframeData: string;
  providerUrl?: string;
  iframeId?: string;
  isPiba?: boolean;
  hideBorder?: boolean;
};

export const useIframe = ({
  iframeData,
  providerUrl = '',
  iframeId = 'payment-iframe',
  isPiba = false,
  hideBorder = false,
}: UseIframeProps): MutableRefObject<HTMLDivElement | null> => {
  const iframeContainerRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (!iframeData || document.getElementById(iframeId)) return;

    const iframeContent = window.atob(iframeData);
    const iframe = document.createElement('iframe');

    Object.assign(iframe.style, {
      width: '100%',
      height: '100%',
      border: `1px solid ${hideBorder ? 'transparent' : '#E5E7EB'}`,
      borderRadius: '4px',
      overflow: 'hidden',
      minHeight: '600px',
      maxHeight: '800px',
    });

    iframe.id = iframeId;
    iframe.allow = `payment ${providerUrl}`;

    iframe.srcdoc = iframeContent;

    if (iframeContainerRef.current) {
      iframeContainerRef.current.appendChild(iframe);
    }

    return () => {
      const existingIframe = document.getElementById(iframeId);
      if (existingIframe) existingIframe.remove();
    };
  }, [iframeData, providerUrl, iframeId, isPiba]);

  return iframeContainerRef;
};
