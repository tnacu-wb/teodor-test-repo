'use client';

import { BookingAllowancesCriteria } from '@whitbread-eos/api';
import type { SwitchState } from '@whitbread-eos/api';
import { useTranslation } from '@whitbread-eos/utils';
import { getDefaultSwitchState } from '@whitbread-eos/utils/server';
import { MutableRefObject, useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';

import { RenderSwitch } from './helpers/helpers';

interface Props {
  onSubmit: (data: any, switchState: SwitchState) => void;
  formRef: MutableRefObject<HTMLFormElement | null>;
  bookingAllowances: BookingAllowancesCriteria;
  onDirtyChange?: (isDirty: boolean) => void;
}

const areSwitchStatesEqual = (first: SwitchState, second: SwitchState) =>
  Object.keys(first).every((key) => {
    const typedKey = key as keyof SwitchState;
    return first[typedKey] === second[typedKey];
  });

export const BookingOptionsForm = ({
  onSubmit,
  formRef,
  bookingAllowances,
  onDirtyChange,
}: Readonly<Props>) => {
  const { t } = useTranslation('company');
  const initialSwitchState = getDefaultSwitchState(bookingAllowances?.upsellItemsAllowed ?? []);
  const [switchState, setSwitchState] = useState<SwitchState>(initialSwitchState);

  const { handleSubmit } = useForm({
    defaultValues: {},
  });

  useEffect(() => {
    if (bookingAllowances?.upsellItemsAllowed) {
      setSwitchState(getDefaultSwitchState(bookingAllowances?.upsellItemsAllowed));
    }
  }, [bookingAllowances]);

  useEffect(() => {
    const isDirty = !areSwitchStatesEqual(switchState, initialSwitchState);
    onDirtyChange?.(isDirty);
  }, [switchState, initialSwitchState, onDirtyChange]);

  const handleCheckedChange = (switchName: keyof SwitchState) => {
    setSwitchState((prevState) => ({
      ...prevState,
      [switchName]: !prevState[switchName],
    }));
  };

  const handleSubmitForm = (data: Record<string, string>) => {
    const finalData = {
      ...data,
      switchState,
    };
    onSubmit(finalData, switchState);
  };

  const renderBookingOptionsForm = () => {
    return (
      <div data-testid="BookingOptionsForm-container" className={'max-w-[620px]'}>
        <div className={'pb-10'}>
          <h4
            data-testid="BookingOptionsForm-title-booking-options"
            className={`${headingStyle} pb-2`}
          >
            {t('coMngt.allowances.bookingOptions.title')}
          </h4>
          <span
            data-testid="BookingOptionsForm-description-booking-options"
            className={'text-darkGrey2'}
          >
            {t('coMngt.allowances.bookingOptions.subtitle')}
          </span>
        </div>
        <div data-testid="BookingOptionsForm-meals-container">
          <h4 data-testid="BookingOptionsForm-meals-title" className={`${headingStyle} pb-6`}>
            {t('coMngt.allowances.meals.title')}
          </h4>
          <div className="flex flex-col gap-4">
            {RenderSwitch(
              'premierInnBreakfast',
              switchState,
              'BookingOptionsForm',
              handleCheckedChange,
              t('coMngt.allowances.meals.piBreakfast.label'),
              t('coMngt.allowances.meals.piBreakfast.description')
            )}
            {RenderSwitch(
              'continentalBreakfast',
              switchState,
              'BookingOptionsForm',
              handleCheckedChange,
              t('coMngt.allowances.meals.contBreakfast.label'),
              t('coMngt.allowances.meals.contBreakfast.description')
            )}
            {RenderSwitch(
              'mealDeal',
              switchState,
              'BookingOptionsForm',
              handleCheckedChange,
              t('coMngt.allowances.meals.mealDeal.label'),
              t('coMngt.allowances.meals.mealDeal.description')
            )}
            {RenderSwitch(
              'hubBreakfast',
              switchState,
              'BookingOptionsForm',
              handleCheckedChange,
              t('coMngt.allowances.meals.hubBreakfast.label'),
              t('coMngt.allowances.meals.hubBreakfast.description')
            )}
          </div>
        </div>
        <div data-testid="BookingOptionsForm-other-extras-container" className={`pt-10`}>
          <h4
            data-testid="BookingOptionsForm-other-extras-title"
            className={`${headingStyle} pb-6`}
          >
            {t('coMngt.allowances.otherExtras.title')}
          </h4>
          <div className="flex flex-col gap-4">
            {RenderSwitch(
              'ultimateWifi',
              switchState,
              'BookingOptionsForm',
              handleCheckedChange,
              t('coMngt.allowances.otherExtras.FI24HR.label'),
              t('coMngt.allowances.otherExtras.FI24HR.description')
            )}
          </div>
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
      {renderBookingOptionsForm()}
    </form>
  );
};

const formStyle = 'flex flex-col mt-6';
const headingStyle = 'font-bold text-base';
