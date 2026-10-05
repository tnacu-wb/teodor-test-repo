'use client';

import { useTranslation } from '@whitbread-eos/utils';
import { useFormContext } from 'react-hook-form';

import {
  PriceInputsSection,
  PriceFormValues,
} from '~components/innBusiness/forms/AlertsForms/PriceInputsSection';

type Props = {
  icons: Record<string, string>;
};

export const PriceAlertsFormWrapper = ({ icons }: Readonly<Props>) => {
  const { t } = useTranslation(['company', 'global']);

  const {
    control,
    formState: { errors },
    trigger,
    clearErrors,
  } = useFormContext<PriceFormValues>();

  return (
    <div data-testid="PriceAlertsFormWrapper-form">
      <PriceInputsSection
        control={control}
        errors={errors}
        trigger={trigger}
        clearErrors={clearErrors}
        icons={icons}
        t={t}
        baseTestId="PriceAlertsFormWrapper-PriceInputs"
        ukLabelKey="company.coMngt.alerts.price.uk.label"
        londonLabelKey="company.coMngt.alerts.price.london.label"
        euLabelKey="company.coMngt.alerts.price.eu.label"
        placeholderKey="company.coMngt.alerts.price.placeholder"
        fieldNamePrefix="priceAlerts"
      />
    </div>
  );
};
