'use client';

import type { SwitchState } from '@whitbread-eos/api';
import { BookingAllowancesCriteria, FormData } from '@whitbread-eos/api';
import { useTranslation } from '@whitbread-eos/utils';
import { MutableRefObject, useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';

import { RenderSwitch } from './helpers/helpers';

interface Props {
  onSubmit: (data: FormData) => void;
  formRef: MutableRefObject<HTMLFormElement | null>;
  bookingAllowances: BookingAllowancesCriteria;
  onDirtyChange?: (isDirty: boolean) => void;
}

const isExtrasSwitchStateEqual = (first: SwitchState, second: SwitchState) =>
  first.carParking === second.carParking && first.additionalCosts === second.additionalCosts;

export const SelectExtrasForm = ({
  onSubmit,
  formRef,
  bookingAllowances,
  onDirtyChange,
}: Readonly<Props>) => {
  const { t } = useTranslation('company');
  const initialSwitchState: SwitchState = {
    carParking: bookingAllowances?.allowCarParking ?? false,
    additionalCosts: bookingAllowances?.allowAdditionalCosts ?? false,
  };
  const [switchState, setSwitchState] = useState<SwitchState>(initialSwitchState);

  const { handleSubmit } = useForm<FormData>({
    defaultValues: {
      switchState: initialSwitchState,
    },
  });

  useEffect(() => {
    const isDirty = !isExtrasSwitchStateEqual(switchState, initialSwitchState);
    onDirtyChange?.(isDirty);
  }, [switchState, initialSwitchState, onDirtyChange]);

  const handleCheckedChange = (switchName: keyof SwitchState) => {
    setSwitchState((prevState) => ({
      ...prevState,
      [switchName]: !prevState[switchName],
    }));
  };

  const handleSubmitForm = (data: any) => {
    const finalData = {
      ...data,
      switchState,
    };

    onSubmit(finalData);
  };

  const renderSelectExtrasForm = () => {
    return (
      <div data-testid="SelectExtrasForm-container" className={'max-w-[620px]'}>
        <div className={'pb-10 pt-12'}>
          <h4 data-testid="SelectExtrasForm-title-select-extras" className={`${headingStyle}`}>
            {t('coMngt.allowances.selectExtras.title')}
          </h4>
        </div>
        <div data-testid="SelectExtrasForm-select-extras-container">
          <div className="flex flex-col gap-4">
            {RenderSwitch(
              'carParking',
              switchState,
              'SelectExtrasForm',
              handleCheckedChange,
              '',
              t('coMngt.allowances.selectExtras.allowParking.label')
            )}
            {RenderSwitch(
              'additionalCosts',
              switchState,
              'SelectExtrasForm',
              handleCheckedChange,
              '',
              t('coMngt.allowances.selectExtras.additionalCosts.label')
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
      {renderSelectExtrasForm()}
    </form>
  );
};

const formStyle = 'flex flex-col';
const headingStyle = 'font-bold text-base';
