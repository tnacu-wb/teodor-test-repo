'use client';

import { formatIBAssetsUrl, cn } from '@whitbread-eos/utils';
import Image from 'next/image';

export type CardIconProps = {
  type: string;
  className?: string;
  icons: Record<string, string>;
};

const CardMapping: Record<string, string> = {
  AC: 'mastercard',
  AM: 'amex',
  AT: 'piba',
  DI: 'dinners',
  DL: 'visa',
  EL: 'visa',
  MA: 'mastercard',
  VI: 'visa',
  MC: 'mastercard',
  AX: 'amex',
  PI: 'piba',
  DN: 'dinners',
  VS: 'visa',
  BD: 'piba',
  PE: 'piba',
};

export function CardIcon({ type, className, icons }: CardIconProps) {
  const url = icons[`icon.payment.${CardMapping[type] || type}`];

  if (url) {
    return (
      <Image
        data-testid="CardIcon"
        className={className}
        src={formatIBAssetsUrl(url)}
        alt="card-icon"
        width={40}
        height={24}
      />
    );
  }

  return <div data-testid="CardIcon" className={cn(cardIconStyle, className)}></div>;
}

const cardIconStyle = 'bg-lightGrey3 min-w-[40px] min-h-[24px]';
