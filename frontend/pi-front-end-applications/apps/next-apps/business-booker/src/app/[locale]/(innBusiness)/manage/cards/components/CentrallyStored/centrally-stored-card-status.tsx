'use client';

import { useTranslation, cn } from '@whitbread-eos/utils';

type Props = {
  expiryDate: string;
};

export function CentrallyStoredCardStatus({ expiryDate }: Props) {
  const { t } = useTranslation('cards');

  const month = parseInt(expiryDate.slice(0, 2), 10);
  const year = parseInt(expiryDate.slice(-2), 10) + 2000;
  const now = new Date();

  const isExpired =
    now.getFullYear() > year || (now.getFullYear() === year && now.getMonth() + 1 > month);

  return (
    <>
      <span className={cn(bulletStyle, isExpired ? bulletRedStyle : bulletGreenStyle)}></span>

      {isExpired
        ? t('cardMgmt.cardStatus.options.expired')
        : t('cardMgmt.cardStatus.options.active')}
    </>
  );
}

const bulletStyle = 'inline-block h-3 w-3 rounded-md bg-secondaryColor mr-2';
const bulletRedStyle = 'bg-error';
const bulletGreenStyle = 'bg-success';
