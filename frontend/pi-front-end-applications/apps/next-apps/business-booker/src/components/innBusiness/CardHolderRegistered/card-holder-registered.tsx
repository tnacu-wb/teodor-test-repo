'use client';

import { cn, useTranslation } from '@whitbread-eos/utils';
import Link from 'next/link';
import React from 'react';

import { ResendCode } from '~components/innBusiness/ResendCode/ResendCode';

export type CardHolderRegisteredProps = {
  registered: boolean;
  className?: string;
  icons: Record<string, string>;
  tetheredGuid: string | undefined;
  scheme: string;
  cardId: string | undefined;
  onResendingCode?: (value: boolean) => void;
};

export function CardHolderRegistered({
  registered,
  className,
  icons,
  tetheredGuid,
  cardId,
  scheme,
  onResendingCode,
}: CardHolderRegisteredProps) {
  const [openResendCode, setOpenResendCode] = React.useState(false);
  const { t } = useTranslation('cards');
  return (
    <span data-testid="CardHolderRegistered" className={className}>
      <span className={cn(bulletStyle, registered ? 'bg-success' : 'bg-warning')} />

      {registered ? (
        t('cardMgmt.cardHolder.options.registered')
      ) : (
        <Link
          className={linkStyle}
          href="#"
          onClick={() => {
            setOpenResendCode(true);
            onResendingCode?.(false);
          }}
        >
          {t('cardMgmt.columns.resendCode')}
        </Link>
      )}
      <ResendCode
        open={openResendCode}
        onOpenChange={() => {
          setOpenResendCode(false);
          onResendingCode?.(true);
        }}
        cardId={cardId}
        tetheredGuid={tetheredGuid}
        scheme={scheme}
        icons={icons}
      />
    </span>
  );
}

const bulletStyle = 'inline-block h-3 w-3 rounded-md bg-secondaryColor mr-2';
const linkStyle = 'text-secondaryColor underline';
