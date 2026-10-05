'use client';

import { RadioGroup, RadioGroupItem, Label } from '@whitbread-eos/atoms/ui';
import { useTranslation } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';
import { useFormContext } from 'react-hook-form';

const getFrequencyOptions = (t: (key: string) => string) => [
  { id: 'no-alerts', value: 'none', label: t('company.coMngt.alerts.frequency.options.noAlerts') },
  {
    id: 'immediately',
    value: 'immediately',
    label: t('company.coMngt.alerts.frequency.options.immediately'),
  },
  { id: 'daily', value: 'daily', label: t('company.coMngt.alerts.frequency.options.daily') },
  { id: 'weekly', value: 'weekly', label: t('company.coMngt.alerts.frequency.options.weekly') },
  { id: 'monthly', value: 'monthly', label: t('company.coMngt.alerts.frequency.options.monthly') },
];

export function FrequencyRadios() {
  const baseDataTestId = 'FrequencyRadios';
  const { t } = useTranslation(['company']);
  const FREQUENCY_OPTIONS = getFrequencyOptions(t);

  const [selectedFrequency, setSelectedFrequency] = useState<string>('');
  const { setValue, getValues } = useFormContext();

  useEffect(() => {
    const initialValue = getValues('frequency');
    if (initialValue) {
      setSelectedFrequency(initialValue);
    }
  }, [getValues]);

  const handleFrequencyChange = (value: string) => {
    setSelectedFrequency(value);
    setValue('frequency', value, {
      shouldValidate: true,
      shouldDirty: true,
    });
  };

  const getLabelClassName = (value: string) => {
    return `${styles.label} ${selectedFrequency === value ? styles.selectedLabel : ''}`;
  };

  const getRadioClassName = (value: string) => {
    return `${styles.radio} ${
      selectedFrequency === value ? styles.selectedRadio : styles.unselectedRadio
    }`;
  };

  return (
    <RadioGroup
      value={selectedFrequency}
      onValueChange={handleFrequencyChange}
      className={styles.radioGroup}
      name="frequency"
      data-testid={`${baseDataTestId}-group`}
    >
      {FREQUENCY_OPTIONS.map((option) => (
        <Label key={option.id} htmlFor={option.id} className={getLabelClassName(option.value)}>
          <RadioGroupItem
            value={option.value}
            id={option.id}
            className={getRadioClassName(option.value)}
            bulletFillStyle={styles.bulletFill}
          />
          <div className={styles.labelText} id={option.id}>
            {option.label}
          </div>
        </Label>
      ))}
    </RadioGroup>
  );
}

const styles = {
  radioGroup: 'mt-2 gap-0',
  label:
    'flex items-center space-x-2 cursor-pointer border border-t-0 first:border-t p-4 bg-white first:rounded-t last:rounded-b h-[unset]',
  selectedLabel: 'outline outline-2 outline-primaryColor -outline-offset-2',
  radio: 'border-2 self-start mt-[2px]',
  selectedRadio: 'color-primaryColor border-primaryColor',
  unselectedRadio: 'border-lightGrey1',
  bulletFill: 'fill-primaryColor text-primaryColor',
  labelText: 'font-semibold text-base cursor-pointer',
};
