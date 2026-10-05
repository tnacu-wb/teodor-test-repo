'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { CompanyDetailsCard, LOCALES } from '@whitbread-eos/api';
import { FormSelect, CardIcon } from '@whitbread-eos/atoms/ui';
import { getPathForLocale, useTranslation, formatIBAssetsUrl } from '@whitbread-eos/utils';
import Link from 'next/link';
import { MutableRefObject, useEffect, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

const optionEmpty = {
  cardNumber: '',
  displayValue: '',
  value: '',
};

type Props = {
  onSubmit: (data: Record<string, string>) => void;
  cards: CompanyDetailsCard[];
  icons: Record<string, string>;
  locale: LOCALES;
  selectedCardId?: string;
  formRef: MutableRefObject<HTMLFormElement | null>;
  paymentTypeOnly?: boolean;
  onDirtyChange?: (isDirty: boolean) => void;
};

const NONE_CARD_ID = '1';

export const getPreselectedCard = (
  cards: CompanyDetailsCard[],
  selectedCardId?: string,
  paymentTypeOnly?: boolean
) => {
  if (paymentTypeOnly) {
    return NONE_CARD_ID;
  }

  if (selectedCardId && cards.some((card) => card.cardId === selectedCardId)) {
    return selectedCardId;
  }

  return NONE_CARD_ID;
};

export function PaymentCardForm({
  cards,
  onSubmit,
  icons,
  locale,
  selectedCardId,
  formRef,
  paymentTypeOnly,
  onDirtyChange,
}: Props) {
  const { t } = useTranslation('users');
  const [selectedCard] = useState(getPreselectedCard(cards, selectedCardId, paymentTypeOnly));

  const optionNone = {
    cardNumber: 'None',
    displayValue: t('userMgmt.manageEmployee.card.label'),
    value: NONE_CARD_ID,
  };

  const schema = z.object({
    cardId: z.object({
      cardNumber: z.string(),
      displayValue: z.any(),
      value: z.string().min(1, t('userMgmt.employee.add.payment.cardName.placeholder')),
    }),
  });

  const getSelectOptions = [
    optionNone,
    ...cards.map((card) => {
      const cardNumber = card.cardNumber.slice(-8);

      return {
        cardNumber,
        value: card.cardId,
        displayValue: (
          <div key={card.cardId} className={cardContainerStyle}>
            <CardIcon type={card.cardType} className={cardIconStyle} icons={icons} />
            {card.cardLabel} {cardNumber}
          </div>
        ),
      };
    }),
  ];

  const findSelectedCard = () => {
    const cardDetails = getSelectOptions?.find(
      (card: Record<string, any>) => card.value === selectedCard
    );
    if (cardDetails?.value) {
      return cardDetails;
    }
    return optionEmpty;
  };

  const {
    control,
    handleSubmit,
    formState: { errors },
    trigger,
    watch,
  } = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      cardId: selectedCard
        ? selectedCard !== optionNone.value
          ? findSelectedCard()
          : optionNone
        : optionNone,
    },
  });

  useEffect(() => {
    const subscription = watch(() => {
      onDirtyChange?.(true);
    });
    return () => subscription.unsubscribe();
  }, [watch, onDirtyChange]);

  const handleCardSubmit = (data: z.infer<typeof schema>) => {
    const { cardId } = data;
    onSubmit({ centralCardId: cardId.value });
  };

  return (
    <form
      ref={formRef as MutableRefObject<HTMLFormElement>}
      className={formStyle}
      onSubmit={handleSubmit(handleCardSubmit)}
      data-testid="PaymentCardForm"
    >
      <h3 data-testid="Card-Name-Heading" className={subHeadingStyle}>
        {paymentTypeOnly
          ? t('userMgmt.employee.invite.payment.paymentType')
          : t('userMgmt.employee.add.payment.cardName')}
        {!paymentTypeOnly && (
          <>
            {' *'}
            <Link className={linkStyle} href={getPathForLocale(locale, `manage/cards`)}>
              {t('userMgmt.manageEmployee.label')}
            </Link>
          </>
        )}
      </h3>

      <Controller
        name="cardId"
        control={control}
        render={({ field }) => (
          <FormSelect
            {...field}
            id="cardId"
            placeholder={t('userMgmt.employee.add.payment.cardName.placeholder')}
            errors={errors}
            errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
            arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
            options={getSelectOptions}
            onBlur={() => trigger('cardId')}
          />
        )}
      />
    </form>
  );
}

const formStyle = 'mt-12';
const subHeadingStyle = 'font-bold text-lg mt-8 mb-6 flex justify-between items-center';
const cardIconStyle = 'mr-2 max-h-[22px]';
const cardContainerStyle = 'flex items-center';
const linkStyle = 'flex font-medium text-sm underline text-secondaryColor';
