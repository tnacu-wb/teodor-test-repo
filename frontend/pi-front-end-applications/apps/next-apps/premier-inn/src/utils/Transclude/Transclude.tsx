import { renderSanitizedHtml } from '@whitbread-eos/utils';
import getConfig from 'next/config';
import React, { useEffect, useRef, useState } from 'react';

interface TranscludeProps {
  src: string;
  initialMarkup?: string;
}

export async function fetchMicroFrontend(src: string): Promise<string> {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const allowedUrls = [
    publicRuntimeConfig.NEXT_PUBLIC_ASSETS_URL_WITHOUT_BASIC,
    // Add other allowed URLs or domains here
  ];

  const isValidUrl = allowedUrls.some((allowedUrl) => src.startsWith(allowedUrl));

  if (src && isValidUrl) {
    const response = await fetch(src);
    if (!response.ok) {
      throw new Error(`Failed to fetch micro-frontend content from ${src}`);
    }
    return response.text();
  } else {
    throw new Error(`The URL ${src} is not allowed.`);
  }
}

function useTransclude(src: string, initialMarkup?: string) {
  const [markup, setHtmlContent] = useState<string | undefined>(initialMarkup);
  const isLoading = !markup;

  useEffect(() => {
    if (!initialMarkup) {
      // Execute only on the client side
      fetchMicroFrontend(src)
        .then(setHtmlContent)
        .catch((error) => {
          console.error('Error fetching micro-frontend:', error);
        });
    }
  }, [src, initialMarkup]);

  return { markup, isLoading };
}

const Transclude: React.FC<TranscludeProps> = ({ src, initialMarkup }) => {
  const { markup, isLoading } = useTransclude(src, initialMarkup);
  const ref: any = useRef(null);

  useEffect(() => {
    if (!isLoading && markup) {
      const slotHtml: any = document.createRange().createContextualFragment(markup);
      ref.current.innerHTML = '';
      ref.current.appendChild(slotHtml);
    }
  }, [isLoading, markup]);

  if (isLoading) return 'Loading...';

  return <div ref={ref}>{renderSanitizedHtml(markup || '')}</div>;
};

export default Transclude;
