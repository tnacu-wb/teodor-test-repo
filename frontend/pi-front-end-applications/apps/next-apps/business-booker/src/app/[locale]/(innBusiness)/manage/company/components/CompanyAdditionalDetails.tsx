'use client';

import { IBFormSelectOption } from '@whitbread-eos/api';
import { FormSelect } from '@whitbread-eos/atoms/ui';
import { useTranslation, formatIBAssetsUrl } from '@whitbread-eos/utils';
import { Controller, useFormContext } from 'react-hook-form';

type AdditionalDetailsInformation = {
  companySector: string;
  averageMonthlyBooking: string;
  numberOfEmployee: string;
};
interface Props {
  icons: Record<string, string>;
  additionalDetailsInformation: AdditionalDetailsInformation;
  isEditMode: boolean;
}

function getDisplayValue(options: IBFormSelectOption[], value: string): string {
  const matchingOption = options.find((option) => option.value === value);
  return matchingOption?.displayValue || '';
}

export function CompanyAdditionalDetails({
  icons,
  additionalDetailsInformation,
  isEditMode,
}: Props) {
  const { t } = useTranslation('company');

  const companySectorOptions: IBFormSelectOption[] = [
    {
      value: 'Agriculture',
      displayValue: t('coMngt.additionalDetails.coSector.options.agriculture'),
    },
    {
      value: 'Mining',
      displayValue: t('coMngt.additionalDetails.coSector.options.mining'),
    },
    {
      value: 'Manufacturing',
      displayValue: t('coMngt.additionalDetails.coSector.options.manufacturing'),
    },
    {
      value: 'Electricity',
      displayValue: t('coMngt.additionalDetails.coSector.options.electricity'),
    },
    {
      value: 'Water',
      displayValue: t('coMngt.additionalDetails.coSector.options.water'),
    },
    {
      value: 'Construction',
      displayValue: t('coMngt.additionalDetails.coSector.options.construction'),
    },
    {
      value: 'Wholesale',
      displayValue: t('coMngt.additionalDetails.coSector.options.wholesale'),
    },
    {
      value: 'Transportation',
      displayValue: t('coMngt.additionalDetails.coSector.options.transportation'),
    },
    {
      value: 'Accommodation',
      displayValue: t('coMngt.additionalDetails.coSector.options.accommodation'),
    },
    {
      value: 'Information',
      displayValue: t('coMngt.additionalDetails.coSector.options.information'),
    },
    {
      value: 'Financial',
      displayValue: t('coMngt.additionalDetails.coSector.options.financial'),
    },
    {
      value: 'RealEstate',
      displayValue: t('coMngt.additionalDetails.coSector.options.realState'),
    },
    {
      value: 'Professional',
      displayValue: t('coMngt.additionalDetails.coSector.options.professional'),
    },
    {
      value: 'Support',
      displayValue: t('coMngt.additionalDetails.coSector.options.support'),
    },
    {
      value: 'PublicAdministration',
      displayValue: t('coMngt.additionalDetails.coSector.options.publicAdmin'),
    },
    {
      value: 'Education',
      displayValue: t('coMngt.additionalDetails.coSector.options.education'),
    },
    {
      value: 'Health',
      displayValue: t('coMngt.additionalDetails.coSector.options.health'),
    },
    {
      value: 'Arts',
      displayValue: t('coMngt.additionalDetails.coSector.options.arts'),
    },
    {
      value: 'Other',
      displayValue: t('coMngt.additionalDetails.coSector.options.other'),
    },
    {
      value: 'Households',
      displayValue: t('coMngt.additionalDetails.coSector.options.households'),
    },
    {
      value: 'Extraterritorial',
      displayValue: t('coMngt.additionalDetails.coSector.options.extraterritorial'),
    },
  ];
  const averageMonthlyBookingsOptions: IBFormSelectOption[] = [
    {
      value: '1-10 rooms',
      displayValue: t('coMngt.additionalDetails.avgBookings.options.1rooms'),
    },
    {
      value: '11-50 rooms',
      displayValue: t('coMngt.additionalDetails.avgBookings.options.11rooms'),
    },
    {
      value: '51-200 rooms',
      displayValue: t('coMngt.additionalDetails.avgBookings.options.51rooms'),
    },
    {
      value: '201-500 rooms',
      displayValue: t('coMngt.additionalDetails.avgBookings.options.201rooms'),
    },
    {
      value: '500 + rooms/More than 500 bookings per year.',
      displayValue: t('coMngt.additionalDetails.avgBookings.options.500rooms'),
    },
  ];
  const numberOfEmployeesOptions: IBFormSelectOption[] = [
    {
      value: '0-9 employees / (Less than 10 employees)',
      displayValue: t('coMngt.additionalDetails.numEmployees.options.0emp'),
    },
    {
      value: '10-49 employees',
      displayValue: t('coMngt.additionalDetails.numEmployees.options.10emp'),
    },
    {
      value: '50-249 employees',
      displayValue: t('coMngt.additionalDetails.numEmployees.options.50emp'),
    },
    {
      value: '250-2000 employees',
      displayValue: t('coMngt.additionalDetails.numEmployees.options.250emp'),
    },
    {
      value: '2000 or more employees',
      displayValue: t('coMngt.additionalDetails.numEmployees.options.2000emp'),
    },
  ];

  const { control, getValues } = useFormContext();

  return isEditMode ? (
    <div
      data-testid="CompanyAdditionalDetails-container"
      className={additionalDetailsContainerStyle}
    >
      <div className="w-full">
        <div className="pt-2 pb-6">
          <span className={titleStyle}>{t('coMngt.additionalDetails.coSector.title')}</span>
        </div>
        <Controller
          name="companySector"
          control={control}
          render={({ field }) => (
            <FormSelect
              {...field}
              id="companySector"
              options={companySectorOptions}
              placeholder={t('coMngt.additionalDetails.select.placeholder')}
              arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
              showLabel={false}
              className="mb-6"
              onChange={(data: IBFormSelectOption) => field.onChange(data?.value)}
              value={companySectorOptions.find(
                (companySectorOption) => companySectorOption.value === getValues('companySector')
              )}
            />
          )}
        />
        <div className="pb-6">
          <span className={titleStyle}>{t('coMngt.additionalDetails.avgBookings.title')}</span>
        </div>
        <Controller
          name="averageMonthlyBooking"
          control={control}
          render={({ field }) => (
            <FormSelect
              {...field}
              id="averageMonthlyBooking"
              options={averageMonthlyBookingsOptions}
              placeholder={t('coMngt.additionalDetails.select.placeholder')}
              arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
              showLabel={false}
              className="mb-6"
              onChange={(data: IBFormSelectOption) => field.onChange(data?.value)}
              value={averageMonthlyBookingsOptions.find(
                (averageMonthlyBookingsOption) =>
                  averageMonthlyBookingsOption.value === getValues('averageMonthlyBooking')
              )}
            />
          )}
        />
        <div className="pb-6">
          <span className={titleStyle}>{t('coMngt.additionalDetails.numEmployees.title')}</span>
        </div>
        <Controller
          name="numberOfEmployee"
          control={control}
          render={({ field }) => (
            <FormSelect
              {...field}
              id="numberOfEmployee"
              options={numberOfEmployeesOptions}
              placeholder={t('coMngt.additionalDetails.select.placeholder')}
              arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
              showLabel={false}
              onChange={(data: IBFormSelectOption) => field.onChange(data?.value)}
              value={numberOfEmployeesOptions.find(
                (numberOfEmployeesOption) =>
                  numberOfEmployeesOption.value === getValues('numberOfEmployee')
              )}
            />
          )}
        />
      </div>
    </div>
  ) : (
    <div
      data-testid="CompanyAdditionalDetails-container"
      className={additionalDetailsContainerStyle}
    >
      <div className={`${subDivStyle} pt-2 pb-4`}>
        <span className={titleStyle}>{t('coMngt.additionalDetails.coSector.title')}</span>
        <span>
          {getDisplayValue(companySectorOptions, additionalDetailsInformation?.companySector) ||
            t('coMngt.notSelected')}
        </span>
      </div>
      <div className={`${subDivStyle} pb-4`}>
        <span className={titleStyle}>{t('coMngt.additionalDetails.avgBookings.title')}</span>
        <span>
          {getDisplayValue(
            averageMonthlyBookingsOptions,
            additionalDetailsInformation?.averageMonthlyBooking
          ) || t('coMngt.notSelected')}
        </span>
      </div>
      <div className={subDivStyle}>
        <span className={titleStyle}>{t('coMngt.additionalDetails.numEmployees.title')}</span>
        <span>
          {getDisplayValue(
            numberOfEmployeesOptions,
            additionalDetailsInformation?.numberOfEmployee
          ) || t('coMngt.notSelected')}
        </span>
      </div>
    </div>
  );
}

const additionalDetailsContainerStyle =
  'flex flex-col w-full items-start text-base text-darkGrey1 font-normal mobile:overflow-auto';
const titleStyle = 'font-bold';
const subDivStyle = 'flex flex-col';
