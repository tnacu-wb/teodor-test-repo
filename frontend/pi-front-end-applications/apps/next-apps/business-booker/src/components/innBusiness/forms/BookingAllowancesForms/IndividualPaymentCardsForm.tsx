'use client';

import { BookingAllowancesCriteria } from '@whitbread-eos/api';
import { Switch } from '@whitbread-eos/atoms/ui';
import { useTranslation } from '@whitbread-eos/utils';
import { MutableRefObject, useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';

interface Props {
  onSubmit: (data: Record<number, number>) => void;
  formRef: MutableRefObject<HTMLFormElement | null>;
  bookingAllowances: BookingAllowancesCriteria;
  onDirtyChange?: (isDirty: boolean) => void;
}

export const IndividualPaymentCardsForm = ({
  onSubmit,
  formRef,
  bookingAllowances,
  onDirtyChange,
}: Readonly<Props>) => {
  const { t } = useTranslation('company');
  const initialIndividualPaymentCard = bookingAllowances?.allowIndividualCards ?? false;
  const [individualPaymentCard, setIndividualPaymentCard] = useState<boolean>(
    initialIndividualPaymentCard
  );

  const { handleSubmit } = useForm({
    defaultValues: {},
  });

  useEffect(() => {
    onDirtyChange?.(individualPaymentCard !== initialIndividualPaymentCard);
  }, [individualPaymentCard, initialIndividualPaymentCard, onDirtyChange]);

  const handleCheckedChange = () => {
    setIndividualPaymentCard(!individualPaymentCard);
  };

  const renderSwitch = () => (
    <div className={switchContainerStyle}>
      <Switch
        checked={individualPaymentCard}
        onCheckedChange={() => handleCheckedChange()}
        data-testid={`IndividualPaymentCardsForm-${individualPaymentCard}-switcher`}
        className={switchStyle}
      />
      <div
        className={switchLabelStyle}
        data-testid={`IndividualPaymentCardsForm-${individualPaymentCard}-label-container`}
      >
        <span className={'text-darkGrey2 text-normal text-sm'}>
          {t('coMngt.allowances.individualCards.label')}
        </span>
      </div>
    </div>
  );

  const handleSubmitForm = (data: Record<number, number>) => {
    const finalData = {
      ...data,
      individualPaymentCard,
    };
    onSubmit(finalData);
  };

  const renderIndividualPaymentCardsForm = () => {
    return (
      <div data-testid="IndividualPaymentCardsForm-container" className={'max-w-[620px]'}>
        <div className={'pb-10 pt-12'}>
          <h4
            data-testid="IndividualPaymentCardsForm-title-individual-cards"
            className={`${headingStyle} pb-2`}
          >
            {t('coMngt.allowances.individualCards.title')}
          </h4>
          <span
            data-testid="IndividualPaymentCardsForm-description-individual-cards"
            className={'text-darkGrey2'}
          >
            {t('coMngt.allowances.individualCards.subtitle')}
          </span>
        </div>
        <div data-testid="IndividualPaymentCardsForm-individual-cards-container">
          <div className="flex flex-col gap-4 pb-12">{renderSwitch()}</div>
        </div>
      </div>
    );
  };

  return (
    <form
      ref={formRef as MutableRefObject<HTMLFormElement>}
      onSubmit={handleSubmit(handleSubmitForm)}
      className={formStyle}
    >
      {renderIndividualPaymentCardsForm()}
    </form>
  );
};

const formStyle = 'flex flex-col mt-12 border-y-[1px] border-lightGrey2';
const headingStyle = 'font-bold text-base';
const switchContainerStyle = 'flex gap-4 items-center';
const switchLabelStyle = 'flex flex-col mobile:order-1';
const switchStyle = 'mobile:order-2';
