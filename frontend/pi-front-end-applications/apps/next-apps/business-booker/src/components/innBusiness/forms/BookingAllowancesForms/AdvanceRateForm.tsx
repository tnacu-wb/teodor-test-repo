'use client';

import { BookingAllowancesCriteria } from '@whitbread-eos/api';
import { Switch } from '@whitbread-eos/atoms/ui';
import { useTranslation } from '@whitbread-eos/utils';
import { MutableRefObject, useState } from 'react';
import { useForm } from 'react-hook-form';

interface Props {
  onSubmit: (data: Record<string, any>) => void;
  formRef: MutableRefObject<HTMLFormElement | null>;
  bookingAllowances: BookingAllowancesCriteria;
}

export const AdvanceRateForm = ({ onSubmit, formRef, bookingAllowances }: Readonly<Props>) => {
  const { t } = useTranslation('company');
  const [advanceRate, setAdvanceRate] = useState<boolean>(
    bookingAllowances?.allowPremierSaverRates ?? false
  );

  const { handleSubmit } = useForm({
    defaultValues: {},
  });

  const handleCheckedChange = () => {
    setAdvanceRate(!advanceRate);
  };

  const renderSwitch = () => (
    <div className={switchContainerStyle}>
      <Switch
        checked={advanceRate}
        onCheckedChange={() => handleCheckedChange()}
        data-testid={`AdvanceRateForm-${advanceRate}-switcher`}
        className={switchStyle}
      />
      <div
        className={switchLabelStyle}
        data-testid={`AdvanceRateForm-${advanceRate}-label-container`}
      >
        <span className={'text-darkGrey2 text-normal text-sm'}>
          {t('coMngt.allowances.advanceRate.label')}
        </span>
      </div>
    </div>
  );

  const handleSubmitForm = (data: Record<string, any>) => {
    const finalData = {
      ...data,
      advanceRate,
    };
    onSubmit(finalData);
  };

  const renderAdvanceRateForm = () => {
    return (
      <div data-testid="AdvanceRateForm-container" className={'max-w-[620px]'}>
        <div className={'pb-10 pt-12'}>
          <h4 data-testid="AdvanceRateForm-title-advance-rate" className={`${headingStyle} pb-2`}>
            {t('coMngt.allowances.advanceRate.title')}
          </h4>
          <span data-testid="AdvanceRateForm-description-advance-rate" className={'text-darkGrey2'}>
            {t('coMngt.allowances.advanceRate.subtitle')}
          </span>
        </div>
        <div data-testid="AdvanceRateForm-advance-rate-container">
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
      {renderAdvanceRateForm()}
    </form>
  );
};

const formStyle = 'flex flex-col mt-12 border-y-[1px] border-lightGrey2';
const headingStyle = 'font-bold text-base';
const switchContainerStyle = 'flex gap-4 items-center';
const switchLabelStyle = 'flex flex-col mobile:order-1';
const switchStyle = 'mobile:order-2';
