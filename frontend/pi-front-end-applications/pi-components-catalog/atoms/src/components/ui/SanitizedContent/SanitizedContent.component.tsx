'use client';

import { renderSanitizedHtml } from '@whitbread-eos/utils';
import React from 'react';

interface Props {
  children: React.ReactElement;
  replacements?: Record<string, string>;
}

const SanitizedContent = ({ children, replacements = {} }: Readonly<Props>) => {
  const childrenArray = React.Children.toArray(children);

  const htmlFragments: string[] = [];
  childrenArray.forEach((child) => {
    if (typeof child === 'string') {
      let modifiedHtml = child;
      for (const [key, value] of Object.entries(replacements)) {
        modifiedHtml = modifiedHtml.replace(new RegExp(key, 'g'), value);
      }
      htmlFragments.push(modifiedHtml);
    } else {
      htmlFragments.push(String(child));
    }
  });

  const html = htmlFragments.join('');
  return <>{renderSanitizedHtml(html)}</>;
};

export { SanitizedContent };
