'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { Language, EmployeeCriteria } from '@whitbread-eos/api';
import { FormPersonTitle, FormInput, FormPhone } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation, parsePhoneNumber } from '@whitbread-eos/utils';
import { MutableRefObject, useEffect, useRef } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

interface Props {
  onSubmit: (data: Record<string, string>) => void;
  language: Language;
  icons: Record<string, string>;
  userDetails?: EmployeeCriteria;
  formRef: MutableRefObject<HTMLFormElement | null>;
  onDirtyChange?: (isDirty: boolean) => void;
  serverErrors?: Record<string, string>;
}

export const PersonalDetailsForm = ({
  language,
  onSubmit,
  icons,
  userDetails,
  formRef,
  onDirtyChange,
  serverErrors,
}: Readonly<Props>) => {
  const { t } = useTranslation('users');
  const serverErrorFieldsRef = useRef<string[]>([]);

  const schema = z.object({
    title: z.object({
      displayValue: z.string().min(1, t('userMgmt.employee.add.form.title.required')),
      value: z.string(),
    }),
    firstName: z
      .string()
      .min(2, t('userMgmt.employee.edit.error.firstNameFormat'))
      .max(30, t('userMgmt.employee.edit.error.firstNameFormat'))
      .regex(/^[\p{L} \-']*$/u, { message: t('userMgmt.employee.edit.error.firstNameFormat') }),
    lastName: z
      .string()
      .min(2, t('userMgmt.employee.edit.error.lastNameFormat'))
      .max(30, t('userMgmt.employee.edit.error.lastNameFormat'))
      .regex(/^[\p{L} \-']*$/u, { message: t('userMgmt.employee.edit.error.lastNameFormat') }),
    email: z.string().email({ message: t('userMgmt.employee.edit.error.emailFormat') }),
    phoneNumber: z.object({
      prefix: z.string(),
      phoneNumber: z
        .string()
        .min(1, t('userMgmt.employee.edit.error.phoneNumber.required'))
        .min(6, t('userMgmt.employee.edit.error.telephoneFormat'))
        .regex(/^\d+$/, { message: t('userMgmt.employee.edit.error.telephoneFormat') }),
    }),
    alternatePhoneNumber: z.object({
      prefix: z.string(),
      phoneNumber: z.union([
        z
          .string()
          .min(6, t('userMgmt.employee.edit.error.telephoneFormat'))
          .regex(/^\d+$/, { message: t('userMgmt.employee.edit.error.telephoneFormat') }),
        z.literal(''),
      ]),
    }),
  });

  const {
    control,
    handleSubmit,
    setError,
    clearErrors,
    formState: { errors, dirtyFields },
    trigger,
    watch,
  } = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      title: {
        displayValue: userDetails?.title ?? '',
        value: userDetails?.title ?? '',
      },
      firstName: userDetails?.firstName ?? '',
      lastName: userDetails?.lastName ?? '',
      email: userDetails?.emailAddress ?? '',
      phoneNumber: parsePhoneNumber(userDetails?.phoneNumber ?? '', language),
      alternatePhoneNumber: parsePhoneNumber(userDetails?.mobileNumber ?? '', language),
    },
  });

  const currentTitle = watch('title');
  const currentPhoneNumber = watch('phoneNumber');
  const currentAlternatePhoneNumber = watch('alternatePhoneNumber');
  useEffect(() => {
    const subscription = watch(({ title, phoneNumber, alternatePhoneNumber }) => {
      const titleHasChanged = title?.value !== currentTitle?.value;
      const phoneNumberHasChanged =
        phoneNumber?.phoneNumber !== currentPhoneNumber?.phoneNumber ||
        phoneNumber?.prefix !== currentPhoneNumber?.prefix;
      const alternatePhoneNumberHasChanged = currentAlternatePhoneNumber?.phoneNumber
        ? alternatePhoneNumber?.phoneNumber !== currentAlternatePhoneNumber?.phoneNumber ||
          alternatePhoneNumber?.prefix !== currentAlternatePhoneNumber?.prefix
        : false;

      if (
        titleHasChanged ||
        phoneNumberHasChanged ||
        alternatePhoneNumberHasChanged ||
        Object.keys(dirtyFields).length > 0
      ) {
        onDirtyChange?.(true);
      }
    });
    return () => subscription.unsubscribe();
  }, [
    watch,
    dirtyFields,
    onDirtyChange,
    currentTitle,
    currentPhoneNumber,
    currentAlternatePhoneNumber,
  ]);

  useEffect(() => {
    if (!serverErrors) {
      return;
    }

    const currentFields = Object.keys(serverErrors);
    if (serverErrorFieldsRef.current.length > 0) {
      clearErrors(serverErrorFieldsRef.current as any);
    }
    if (currentFields.length === 0) {
      serverErrorFieldsRef.current = [];
      return;
    }

    currentFields.forEach((field) => {
      setError(field as any, { type: 'server', message: serverErrors[field] });
    });
    serverErrorFieldsRef.current = currentFields;
  }, [serverErrors, setError, clearErrors]);

  const handlePersonalDetailsSubmit = (data: z.infer<typeof schema>) => {
    const { title, firstName, lastName, email, phoneNumber, alternatePhoneNumber } = data;
    const personalDetails = {
      title: title.value,
      firstName,
      lastName,
      emailAddress: email,
      mobileNumber: alternatePhoneNumber.phoneNumber
        ? `${alternatePhoneNumber?.prefix}${alternatePhoneNumber.phoneNumber}`
        : '',
      phoneNumber: `${phoneNumber?.prefix}${phoneNumber.phoneNumber}`,
    };
    onSubmit(personalDetails);
  };

  return (
    <form
      ref={formRef as MutableRefObject<HTMLFormElement>}
      onSubmit={handleSubmit(handlePersonalDetailsSubmit)}
      className={formStyle}
    >
      <h4 data-testid="Personal-Details-Heading" className={headingStyle}>
        {t('userMgmt.employee.add.form.heading')}
      </h4>
      <Controller
        name="title"
        control={control}
        render={({ field }) => (
          <FormPersonTitle
            {...field}
            id="Title"
            placeholder={t('userMgmt.employee.add.form.title')}
            errors={errors}
            onBlur={() => {
              trigger('title');
            }}
            icons={icons}
          />
        )}
      />
      <Controller
        name="firstName"
        control={control}
        render={({ field }) => (
          <FormInput
            {...field}
            id="First-Name"
            type={'text'}
            placeholder={t('userMgmt.employee.add.form.firstName') + ' *'}
            errors={errors}
            errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
            onBlur={() => {
              trigger('firstName');
            }}
          />
        )}
      />
      <Controller
        name="lastName"
        control={control}
        render={({ field }) => (
          <FormInput
            {...field}
            id="Last-Name"
            type={'text'}
            placeholder={t('userMgmt.employee.add.form.lastName') + ' *'}
            errors={errors}
            errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
            onBlur={() => {
              trigger('lastName');
            }}
          />
        )}
      />
      <Controller
        name="email"
        control={control}
        render={({ field }) => (
          <FormInput
            {...field}
            id="Email"
            type={'text'}
            placeholder={t('userMgmt.employee.add.form.email') + ' *'}
            errors={errors}
            errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
            onBlur={() => {
              trigger('email');
            }}
          />
        )}
      />
      <Controller
        name="phoneNumber"
        control={control}
        render={({ field }) => (
          <FormPhone
            {...field}
            id="Phone-Number"
            language={language}
            placeholder={t('userMgmt.employee.add.form.phone')}
            errors={errors}
            errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
            arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
            onBlur={() => {
              trigger('phoneNumber');
            }}
          />
        )}
      />
      <Controller
        name="alternatePhoneNumber"
        control={control}
        render={({ field }) => (
          <FormPhone
            {...field}
            id="Alternate-Phone-Number"
            language={language}
            placeholder={t('userMgmt.employee.add.form.altPhone')}
            errors={errors}
            errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
            arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
            onBlur={() => {
              trigger('alternatePhoneNumber');
            }}
          />
        )}
      />
    </form>
  );
};

const formStyle = 'flex flex-col gap-6 mt-8';
const headingStyle = 'font-bold text-xl';
