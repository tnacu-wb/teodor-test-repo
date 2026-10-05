'use client';

import { Switch, Label } from '@whitbread-eos/atoms/ui';
import { useTranslation } from '@whitbread-eos/utils';
import { useFormContext } from 'react-hook-form';

const getAlertOptions = (t: (key: string) => string) => [
  {
    key: 'sameDayBooking' as const,
    label: t('company.coMngt.alerts.booking.alert.sameDay.label'),
  },
  {
    key: 'weekendArrival' as const,
    label: t('company.coMngt.alerts.booking.alert.weekendNight.label'),
  },
  {
    key: 'partWeekendBooking' as const,
    label: t('company.coMngt.alerts.booking.alert.partWeekend.label'),
  },
];

export function AlertToggles() {
  const { t } = useTranslation(['company']);
  const ALERTS = getAlertOptions(t);
  const baseDataTestId = 'AlertToggles';

  const { watch, setValue } = useFormContext();
  const alertToggles = watch('alertToggles');

  const handleToggle = (key: string) => {
    setValue(`alertToggles.${key}`, !alertToggles[key], {
      shouldValidate: true,
      shouldDirty: true,
    });
  };

  return (
    <div className={containerStyle} data-testid={`${baseDataTestId}-container`}>
      {ALERTS.map((alert) => (
        <div key={alert.key} className={rowStyle}>
          <Switch
            id={alert.key}
            checked={alertToggles[alert.key]}
            onCheckedChange={() => handleToggle(alert.key)}
            data-testid={`${baseDataTestId}-switch-${alert.key}`}
            className={switchStyle}
          />
          <div className={labelContainerStyle}>
            <Label className={labelStyle}>{alert.label}</Label>
          </div>
        </div>
      ))}
    </div>
  );
}

const containerStyle = 'self-stretch flex flex-col justify-start items-start gap-6';
const rowStyle = 'self-stretch inline-flex flex-row justify-start items-center gap-4';
const switchStyle = 'mobile:order-2';
const labelContainerStyle =
  'flex-1 inline-flex flex-col justify-center items-center mobile:order-1';
const labelStyle = 'self-stretch text-neutral-800 text-sm font-normal leading-tight';
