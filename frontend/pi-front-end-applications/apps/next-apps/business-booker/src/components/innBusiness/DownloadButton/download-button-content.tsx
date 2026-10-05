'use client';

import { Button } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, cn } from '@whitbread-eos/utils/server';
import Image from 'next/image';
import React from 'react';

type Props = {
  altText: string;
  buttonText: string;
  testId?: string;
  className?: string;
  icons: Record<string, string>;
  handleDownloadCSV?: () => Promise<void>;
};

export function DownloadButtonContent({
  altText,
  buttonText,
  testId,
  className,
  icons,
  handleDownloadCSV,
}: Props) {
  return (
    <>
      <Button
        variant="downloadButton"
        size="downloadButton"
        data-testid={testId ? `${testId}-DownloadButton` : 'DownloadButton'}
        className={cn(buttonStyle, className)}
        onClick={handleDownloadCSV}
      >
        <Image
          alt={altText}
          src={formatIBAssetsUrl(icons['icon.download-icon'])}
          width={24}
          height={24}
          className={iconStyle}
          data-testid={testId ? `${testId}-DownloadButton-Icon` : 'DownloadButton-Icon'}
        />
        <span
          data-testid={testId ? `${testId}-DownloadButton-label` : 'DownloadButton-label'}
          className={textStyle}
        >
          {buttonText}
        </span>
      </Button>
    </>
  );
}

const buttonStyle = 'mobile:w-14 mobile:h-14';
const textStyle = 'mobile:hidden';
const iconStyle = 'mr-2 mobile:mr-0';
