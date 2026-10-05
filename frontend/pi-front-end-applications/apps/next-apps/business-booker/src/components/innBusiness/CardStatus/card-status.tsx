'use client';

import {
  CustomerAccountDetails,
  LOCALES,
  CARD_STATUS_WL_TYPE,
  CARD_STATUS_TYPE,
} from '@whitbread-eos/api';
import { cn, useTranslation } from '@whitbread-eos/utils';
import Link from 'next/link';

import { DataTableRow } from '~components/innBusiness/DataTable/index';

import { ActivateCardButton } from './activate-card-button';

export type CardStatusProps = {
  status: CARD_STATUS_WL_TYPE;
  isActivated: boolean;
  locale?: LOCALES;
  className?: string;
  cardDetails?: DataTableRow;
  icons?: Record<string, string>;
  accountHolder?: CustomerAccountDetails;
  token?: string;
  onActivation?: () => void;
  afterActivation?: () => void;
};

const labelType = {
  [CARD_STATUS_TYPE.DISPATCHING]: 'cardMgmt.cardStatus.options.dispatching',
  [CARD_STATUS_TYPE.ACTIVATE]: 'cardMgmt.cardStatus.options.activate',
  [CARD_STATUS_TYPE.ACTIVE]: 'cardMgmt.cardStatus.options.active',
  [CARD_STATUS_TYPE.NOT_ACTIVATED]: 'cardMgmt.cardStatus.options.notActivated',
  [CARD_STATUS_TYPE.CANCELLED]: 'cardMgmt.cardStatus.options.cancelled',
  [CARD_STATUS_TYPE.EXPIRED]: 'cardMgmt.cardStatus.options.expired',
};

export function CardStatus({
  locale,
  status,
  isActivated,
  className,
  cardDetails,
  icons,
  accountHolder,
  token,
  onActivation,
  afterActivation,
}: CardStatusProps) {
  const { t } = useTranslation('cards');
  const isIBPay = !!accountHolder;
  const isMyIBCard = isIBPay && cardDetails?.myCard;

  const getType = () => {
    if (status === CARD_STATUS_WL_TYPE.HOT) {
      return CARD_STATUS_TYPE.ACTIVE;
    }

    if (status === CARD_STATUS_WL_TYPE.CURRENT) {
      return isActivated
        ? CARD_STATUS_TYPE.ACTIVE
        : isMyIBCard
          ? CARD_STATUS_TYPE.ACTIVATE
          : CARD_STATUS_TYPE.NOT_ACTIVATED;
    }

    if (status === CARD_STATUS_WL_TYPE.PENDING) {
      return CARD_STATUS_TYPE.DISPATCHING;
    }

    return CARD_STATUS_TYPE.CANCELLED;
  };

  const type = getType();

  const cardStatusElement = () => {
    if (type !== CARD_STATUS_TYPE.ACTIVATE) {
      return t(labelType[type]);
    } else if (isIBPay) {
      return (
        <ActivateCardButton
          cardDetails={cardDetails!}
          icons={icons!}
          accountHolder={accountHolder}
          token={token ?? ''}
          locale={locale}
          onActivation={onActivation}
          afterActivation={afterActivation}
        />
      );
    } else {
      return (
        <Link className={linkStyle} href="#">
          {t(labelType[type])}
        </Link>
      );
    }
  };

  return (
    <span data-testid="CardStatus" className={className}>
      <span className={cn(bulletStyle, bulletTypeStyle[type] || 'bg-error')} />
      {cardStatusElement()}
    </span>
  );
}

const bulletStyle = 'inline-block h-3 w-3 rounded-md bg-secondaryColor mr-2';
const bulletTypeStyle = {
  [CARD_STATUS_TYPE.DISPATCHING]: 'bg-warning',
  [CARD_STATUS_TYPE.ACTIVATE]: 'bg-warning',
  [CARD_STATUS_TYPE.ACTIVE]: 'bg-success',
  [CARD_STATUS_TYPE.CANCELLED]: 'bg-error',
  [CARD_STATUS_TYPE.EXPIRED]: 'bg-error',
  [CARD_STATUS_TYPE.NOT_ACTIVATED]: 'bg-warning',
};
const linkStyle = 'text-secondaryColor underline';
