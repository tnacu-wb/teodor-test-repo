'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { ROOM_CODES } from '@whitbread-eos/api';
import { FormSelect } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import { MutableRefObject, useEffect, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

interface Props {
  onSubmit: (data: Record<string, any>) => void;
  formRef: MutableRefObject<HTMLFormElement | null>;
  icons: Record<string, string>;
  roomRequirements?: Record<string, any>;
  onDirtyChange?: (isDirty: boolean) => void;
}

export const RoomRequirementsForm = ({
  onSubmit,
  icons,
  roomRequirements,
  formRef,
  onDirtyChange,
}: Readonly<Props>) => {
  const { t } = useTranslation('profile');
  const hasChildren = roomRequirements?.children > 0;
  const hasMultipleAdults = roomRequirements?.adults > 1 && !hasChildren;

  const adultsOptions = [
    {
      value: 1,
      displayValue: '1',
    },
    {
      value: 2,
      displayValue: '2',
    },
  ];

  const childrenOptions = [
    {
      value: 0,
      displayValue: '0',
    },
    {
      value: 1,
      displayValue: '1',
    },
    {
      value: 2,
      displayValue: '2',
    },
  ];

  const cotRequiredOptions = [
    {
      value: true,
      displayValue: t('roomrequirements.cotrequired.true'),
    },
    {
      value: false,
      displayValue: t('roomrequirements.cotrequired.false'),
    },
  ];

  let initialTypeOptions;
  const childrenTypeOptions = [
    {
      value: ROOM_CODES.family,
      displayValue: t('roomrequirements.type.family'),
    },
  ];
  const multipleAdultsTypeOptions = [
    {
      value: ROOM_CODES.double,
      displayValue: t('roomrequirements.type.double'),
    },
    {
      value: ROOM_CODES.twin,
      displayValue: t('roomrequirements.type.twin'),
    },
    {
      value: ROOM_CODES.accessible,
      displayValue: t('roomrequirements.type.accessible'),
    },
  ];
  const singleAdultTypeOptions = [
    {
      value: ROOM_CODES.double,
      displayValue: t('roomrequirements.type.double'),
    },
    {
      value: ROOM_CODES.single,
      displayValue: t('roomrequirements.type.single'),
    },
    {
      value: ROOM_CODES.accessible,
      displayValue: t('roomrequirements.type.accessible'),
    },
  ];

  if (hasChildren) {
    initialTypeOptions = childrenTypeOptions;
  } else if (hasMultipleAdults) {
    initialTypeOptions = multipleAdultsTypeOptions;
  } else {
    initialTypeOptions = singleAdultTypeOptions;
  }

  const [typeOptions, setTypeOptions] = useState(initialTypeOptions);

  const findTypeDisplayValue = (value: string, defaultValue: string, childrenValue: string) => {
    if (hasChildren) {
      return childrenValue;
    }

    if (!value) {
      return defaultValue;
    }

    const obj = typeOptions.find((typeOption) => {
      return typeOption.value === value;
    });

    return obj?.displayValue;
  };

  const displayDefaultAdults = (defaultValue: number | string) =>
    roomRequirements?.adults && roomRequirements?.adults > 0
      ? roomRequirements?.adults
      : defaultValue;

  const displayDefaultCot = roomRequirements?.cotRequired
    ? t('roomrequirements.cotrequired.true')
    : t('roomrequirements.cotrequired.false');

  const displayDefaultType = findTypeDisplayValue(
    roomRequirements?.type,
    t('roomrequirements.type.double'),
    t('roomrequirements.type.family')
  );

  const displayDefaultTypeValue = hasChildren
    ? ROOM_CODES.family
    : roomRequirements?.adults && roomRequirements?.type && roomRequirements?.type !== ''
      ? roomRequirements?.type
      : ROOM_CODES.double;

  const schema = z.object({
    adults: z.object({
      displayValue: z.any(),
      value: z.number(),
    }),
    children: z.object({
      displayValue: z.any(),
      value: z.number(),
    }),
    cotRequired: z.object({
      displayValue: z.any(),
      value: z.boolean(),
    }),
    type: z.object({
      displayValue: z.any(),
      value: z.string(),
    }),
  });

  const {
    control,
    handleSubmit,
    formState: { errors, isDirty },
    trigger,
    setValue,
    watch,
  } = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      adults: {
        displayValue: displayDefaultAdults('1'),
        value: displayDefaultAdults(1),
      },
      children: {
        displayValue: roomRequirements?.children ?? '0',
        value: roomRequirements?.children ?? 0,
      },
      cotRequired: {
        displayValue: displayDefaultCot,
        value: roomRequirements?.cotRequired ?? false,
      },
      type: {
        displayValue: displayDefaultType,
        value: displayDefaultTypeValue,
      },
    },
  });

  const handleRoomRequirementsSubmit = (data: z.infer<typeof schema>) => {
    const { adults, children, cotRequired, type } = data;
    const roomRequirementsData = {
      adults: adults.value,
      children: children.value,
      cotRequired: cotRequired.value,
      type: type.value,
    };
    onSubmit(roomRequirementsData);
  };

  const childrenValues = watch('children');
  const adultsValues = watch('adults');
  const typeValues = watch('type');

  useEffect(() => {
    onDirtyChange?.(
      isDirty ||
        childrenValues?.value !== (roomRequirements?.children ?? 0) ||
        adultsValues?.value !== displayDefaultAdults(1)
    );
  }, [isDirty, childrenValues, adultsValues]);

  const handleTypeOnChange = (optionsType: string, selectedValue: string | number) => {
    if (optionsType === 'adults') {
      if (selectedValue === 1) {
        if (typeValues?.value === ROOM_CODES.twin) {
          setValue('type', singleAdultTypeOptions[0]);
        }
        if (childrenValues?.value === 0) {
          setTypeOptions(singleAdultTypeOptions);
        }
        setValue('adults', adultsOptions[0]);
      }

      if (selectedValue === 2) {
        if (typeValues?.value === ROOM_CODES.single) {
          setValue('type', multipleAdultsTypeOptions[0]);
        }
        if (childrenValues?.value === 0) {
          setTypeOptions(multipleAdultsTypeOptions);
        }
        setValue('adults', adultsOptions[1]);
      }
    } else if (optionsType === 'children') {
      if (selectedValue === 1 || selectedValue === 2) {
        setValue('children', childrenOptions[selectedValue]);
        setValue('type', childrenTypeOptions[0]);
        setTypeOptions(childrenTypeOptions);
      }
      if (
        selectedValue == 0 &&
        adultsValues?.value === 1 &&
        typeValues?.value === ROOM_CODES.family
      ) {
        setValue('children', childrenOptions[0]);
        setValue('type', singleAdultTypeOptions[0]);
        setTypeOptions(singleAdultTypeOptions);
      }
      if (
        selectedValue == 0 &&
        adultsValues?.value === 2 &&
        typeValues?.value === ROOM_CODES.family
      ) {
        setValue('children', childrenOptions[0]);
        setValue('type', multipleAdultsTypeOptions[0]);
        setTypeOptions(multipleAdultsTypeOptions);
      }
    }
  };

  return (
    <form
      ref={formRef as MutableRefObject<HTMLFormElement>}
      onSubmit={handleSubmit(handleRoomRequirementsSubmit)}
      className={formStyle}
      data-testid="Room-Requirements-Form"
    >
      <Controller
        name="adults"
        control={control}
        render={({ field }) => (
          <>
            <label htmlFor="Adults" className={labelStyle}>
              {t('roomrequirements.adults.label')}
            </label>
            <FormSelect
              {...field}
              id="Adults"
              showLabel={true}
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
              options={adultsOptions}
              onBlur={() => {
                trigger('adults');
              }}
              onChange={(valuesObj: Record<string, string | number>) => {
                handleTypeOnChange('adults', valuesObj.value);
              }}
            />
          </>
        )}
      />

      <Controller
        name="children"
        control={control}
        render={({ field }) => (
          <>
            <label htmlFor="Children" className={labelStyle}>
              {t('roomrequirements.children.label')}
            </label>
            <FormSelect
              {...field}
              id="Children"
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
              options={childrenOptions}
              onBlur={() => {
                trigger('children');
              }}
              onChange={(valuesObj: Record<string, string | number>) => {
                handleTypeOnChange('children', valuesObj.value);
              }}
            />
          </>
        )}
      />

      <Controller
        name="cotRequired"
        control={control}
        render={({ field }) => (
          <>
            <label htmlFor="CotRequired" className={labelStyle}>
              {t('roomrequirements.cot.label')}
            </label>
            <FormSelect
              {...field}
              id="CotRequired"
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
              options={cotRequiredOptions}
              onBlur={() => {
                trigger('cotRequired');
              }}
            />
          </>
        )}
      />

      <Controller
        name="type"
        control={control}
        render={({ field }) => (
          <>
            <label htmlFor="Type" className={labelStyle}>
              {t('roomrequirements.roomType.label')}
            </label>
            <FormSelect
              {...field}
              id="Type"
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
              options={typeOptions}
              onBlur={() => {
                trigger('type');
              }}
            />
          </>
        )}
      />
    </form>
  );
};

const formStyle = 'flex flex-col mobile:w-full w-1/3';
const labelStyle = 'font-bold mt-[1.5rem] mb-[1rem] leading-[1.5rem]';
