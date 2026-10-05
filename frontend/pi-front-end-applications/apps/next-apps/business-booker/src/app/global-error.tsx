'use client';

import { getPathForLocale, getLocaleByPathname } from '@whitbread-eos/utils';

export default function GlobalError() {
  const locale = getLocaleByPathname(window.location.pathname);
  return (
    <html>
      <head>
        <meta httpEquiv="refresh" content={`0;url=${getPathForLocale(locale, 'error')}`} />
      </head>
    </html>
  );
}
