import { PAYMENT_TYPES, PaymentCardInfo } from '@whitbread-eos/api';
import { useTranslation, getSavedCardType } from '@whitbread-eos/utils';
import * as React from 'react';

import { CardIcon } from '../../CardIcon';
import { RadioGroup } from '../../RadioGroup';
import { FormRadioGroup } from '../FormRadioGroup';

const CardIconWrapper: React.FC<{ type: string; icons: Record<string, string> }> = ({
  type,
  icons,
}) => (
  <div className={cardIconContainer}>
    <CardIcon type={type} icons={icons} className={cardIcon} />
  </div>
);
interface IBFormInputProps extends React.ComponentPropsWithoutRef<typeof FormRadioGroup> {
  selectedValue: string;
  icons: Record<string, string>;
  errors?: Record<string, any>;
  testId: string;
  labelsLocation: string;
  cardDetails?: PaymentCardInfo;
  showNewPibaOption?: boolean;
}

const formatExpiryDate = (expiryDate?: string) => {
  if (!expiryDate || expiryDate.length !== 4) {
    return expiryDate ?? '';
  }
  return `${expiryDate.slice(0, 2)}/${expiryDate.slice(2)}`;
};

export const FormCardType = React.forwardRef<
  React.ElementRef<typeof RadioGroup>,
  React.ComponentPropsWithoutRef<typeof RadioGroup> & IBFormInputProps
>(
  (
    {
      cardDetails,
      labelsLocation,
      testId,
      icons,
      errors,
      selectedValue,
      showNewPibaOption = true,
      ...props
    },
    ref
  ) => {
    const isCardsLabels = labelsLocation === 'cards';
    const { t } = useTranslation(['profile', 'cards']);

    const hasSavedCard = !!cardDetails?.cardNumber;
    const keepCardOption = {
      value: getSavedCardType(cardDetails?.cardType) ?? '',
      label: (
        <div className="flex flex-col">
          <div className="flex">
            <span>{`${t(
              'cards.centrallyStoredCard.card.type.existing'
            )} (${cardDetails?.cardNumber?.slice(-8)})`}</span>
            <div className={iconsContainerStyle}>
              <CardIconWrapper
                key={cardDetails?.cardType}
                type={cardDetails?.cardType ?? ''}
                icons={icons}
              />
            </div>
          </div>
          <div className="flex">
            <div className="flex flex-col">
              <span className="font-normal text-sm text-darkGrey2">{cardDetails?.nameOnCard}</span>
              <span className="font-normal text-sm text-darkGrey2">{`${t(
                'cards.centrallyStoredCard.card.expiry'
              )} ${formatExpiryDate(cardDetails?.expiryDate)}`}</span>
            </div>
            <div className="text-[13px] ml-auto mt-auto text-secondaryColor border border-secondaryColor rounded-full font-bold px-2">
              {t('cards.centrallyStoredCard.card.storage')}
            </div>
          </div>
        </div>
      ),
    };
    const paymentTypeItems = [
      {
        value: PAYMENT_TYPES.NEW_CARD,
        label: (
          <div className="flex">
            <span>
              {isCardsLabels
                ? t('cards.centrallyStoredCard.card.type.newCredit.label')
                : t('profile.payment.paymentTypeNewDebitCreditCard')}
            </span>
            <div className={iconsContainerStyle}>
              {['VI', 'MC', 'AX', 'DN'].map((type) => (
                <CardIconWrapper key={type} type={type} icons={icons} />
              ))}
            </div>
          </div>
        ),
      },
      ...(showNewPibaOption
        ? [
            {
              value: PAYMENT_TYPES.NEW_PIBA,
              label: (
                <div className="flex">
                  <span>
                    {isCardsLabels
                      ? t('cards.centrallyStoredCard.card.type.newInnBusiness.label')
                      : t('profile.payment.paymentTypeNewInnBusinessPayCard')}
                  </span>
                  <div className={iconsContainerStyle}>
                    {<CardIconWrapper key="PI" type="PI" icons={icons} />}
                  </div>
                </div>
              ),
            },
          ]
        : []),
    ];

    return (
      <FormRadioGroup
        {...props}
        ref={ref}
        variant="col-styled"
        items={hasSavedCard ? [keepCardOption, ...paymentTypeItems] : paymentTypeItems}
        errors={errors}
        errorIcon={icons['icon.notification.error']}
        selectedValue={selectedValue}
        data-testid={testId}
      />
    );
  }
);
FormCardType.displayName = 'FormCardType';

const cardIconContainer = 'w-[40px] h-[24px] rounded-[2px] flex items-center justify-center';
const cardIcon = 'w-full h-full object-contain';
const iconsContainerStyle = 'flex gap-1 ml-auto';
