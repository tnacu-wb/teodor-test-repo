'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import {
  Language,
  OptionType,
  LOCALES,
  ApplicationParticipant,
  DATE_TYPE,
} from '@whitbread-eos/api';
import { Button, FormPeoplePicker, Notification, SanitizedContent } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation, getPathForLocale } from '@whitbread-eos/utils';
import {
  getEmployeesWithFilteringOptions,
  shareApplication,
  removeParticipant,
} from '@whitbread-eos/utils/server';
import { format } from 'date-fns';
import Image from 'next/image';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useState, useEffect, ReactNode } from 'react';
import { useForm, FormProvider, Controller } from 'react-hook-form';
import { z } from 'zod';

import { AppDetailsCard } from './AppDetailsCard';

type BaseProps = {
  companyName: string;
  applicationReference: number;
  startedBy: string;
  companyId: string;
  icons: Record<string, string>;
  participants: ApplicationParticipant[];
  locale?: LOCALES;
  language?: Language;
  applicationGuid: string;
  applicationId: string;
  token: string;
  titleSection: ReactNode;
  bottomButton: ReactNode;
  showResumeLink?: boolean;
  showShareAppWithColleague?: boolean;
  baseDataTestId: string;
};

export function BaseApplicationComponent({
  companyName,
  applicationReference,
  startedBy,
  companyId,
  icons,
  locale,
  participants,
  applicationGuid,
  applicationId,
  token,
  titleSection,
  bottomButton,
  showResumeLink = false,
  showShareAppWithColleague = false,
  baseDataTestId,
}: Readonly<BaseProps>) {
  const router = useRouter();

  const { t } = useTranslation('payApplication');
  const [selectedEmployee, setSelectedEmployee] = useState<OptionType | null>(null);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  const formMethods = useForm({
    resolver: zodResolver(
      z.object({
        employeeId: z.string(),
      })
    ),
    defaultValues: { employeeId: '' },
  });

  const {
    control,
    trigger,
    formState: { errors },
    setError,
    setValue,
    reset,
  } = formMethods;

  const isSharedApp = participants.some(
    (participant: ApplicationParticipant) => !participant.initiator
  );

  const handleEmployeeChange = (employee: OptionType) => {
    setSelectedEmployee(employee);
  };

  useEffect(() => {
    if (selectedEmployee) {
      setValue('employeeId', selectedEmployee?.value ?? '');
    }
  }, [selectedEmployee, setValue]);

  const maxSharedParticipants =
    participants.filter((participant) => !participant.initiator).length >= 5;

  const handleShareApplication = async () => {
    if (!selectedEmployee || !selectedEmployee.employeeData) {
      return;
    }

    setIsSubmitting(true);

    try {
      const employeeData = selectedEmployee.employeeData;
      const email = employeeData.emailAddress;

      const emailExists = participants.some(
        (participant) => participant.email?.toLowerCase() === email?.toLowerCase()
      );

      if (emailExists) {
        setIsSubmitting(false);
        return;
      }

      const fullName = `${employeeData.title} ${employeeData.firstName} ${employeeData.lastName}`;

      const employeesResult = await getEmployeesWithFilteringOptions(
        companyId,
        20,
        token,
        1,
        email,
        '',
        undefined,
        false,
        false
      );

      if (!employeesResult || !employeesResult.employees || !employeesResult.employees.length) {
        throw new Error('No employees found with the given email');
      }

      const matchingEmployee = employeesResult.employees.find(
        (emp: any) => emp.emailAddress === email
      );

      if (matchingEmployee.employeeId === null) {
        setIsSubmitting(false);
        return;
      }

      const employeeId = Number(matchingEmployee.employeeId);

      if (isNaN(employeeId)) {
        setIsSubmitting(false);
        return;
      }

      const response = await shareApplication(
        applicationId,
        applicationGuid,
        employeeId,
        token,
        email,
        fullName
      );

      if (response && response.status === 'SUCCESS') {
        reset();
        setSelectedEmployee(null);
      }
    } catch (error) {
      console.error('Error sharing application:', error);
    } finally {
      setIsSubmitting(false);
      router.refresh();
    }
  };

  const handleRemoveParticipant = async (participantId: string) => {
    try {
      await removeParticipant(applicationId, applicationGuid, participantId, token);
    } catch (error) {
      console.error('Error removing participant:', error);
    } finally {
      router.refresh();
    }
  };
  return (
    <FormProvider {...formMethods}>
      <div className={containerStyle} data-testid={`${baseDataTestId}-container`}>
        {titleSection}

        {maxSharedParticipants && (
          <div className={'my-4'} data-testid={`${baseDataTestId}-notification-limit-5-users`}>
            <Notification
              type="warning"
              icon={formatIBAssetsUrl(icons?.['icon.notification.alert'])}
              message={
                <SanitizedContent>{t('payapp.applicationSave.maxLimit.message')}</SanitizedContent>
              }
            />
          </div>
        )}

        <div className={formStyle} data-testid={`${baseDataTestId}-form-shared`}>
          {showResumeLink && (
            <div
              className={fieldStyle}
              data-testid={`${baseDataTestId}-resume-application-link-container`}
            >
              <Link
                className={linkStyle}
                data-testid={`${baseDataTestId}-resume-application-link`}
                href={getPathForLocale(
                  locale || LOCALES.EN,
                  `business-pay/pay-application-resume?applicationId=${applicationId}&applicationGuid=${applicationGuid}`
                )}
              >
                {t('payapp.resume.subheading')}
              </Link>
            </div>
          )}

          <AppDetailsCard
            companyName={companyName}
            applicationReference={String(applicationReference)}
            startedBy={startedBy}
            baseDataTestId={baseDataTestId}
          />

          {isSharedApp && (
            <>
              <div
                className={`${fieldStyle} pt-6`}
                data-testid={`${baseDataTestId}-shared-title-container`}
              >
                <span className="font-bold" data-testid={`${baseDataTestId}-shared-title-text`}>
                  {t('payapp.resume.applicationShared')}
                </span>
              </div>
              <div
                data-testid={`${baseDataTestId}-shared-entire-data-container`}
                className="space-y-2"
              >
                {participants
                  .filter((participant: ApplicationParticipant) => !participant.initiator)
                  .map((participant: ApplicationParticipant) => {
                    const formattedDate = participant.shared
                      ? format(new Date(participant.shared), DATE_TYPE.DD_MM_YYYY)
                      : null;

                    return (
                      <div
                        key={participant.participantId}
                        data-testid={`${baseDataTestId}-participant-${participant.participantId}`}
                      >
                        <span
                          data-testid={`${baseDataTestId}-participant-${participant.participantId}-mail`}
                          className="mt-4"
                        >
                          {participant.email}
                        </span>
                        <div
                          className="flex flex:col justify-between mt-1"
                          data-testid={`${baseDataTestId}-sharedLabel-${participant.participantId}-sharedTime`}
                        >
                          <span>
                            <span
                              className="font-bold"
                              data-testid={`${baseDataTestId}-sharedLabel-text-${participant.participantId}-${participant.shared}`}
                            >
                              {t('payapp.resume.sharedLabel')}
                            </span>
                            {formattedDate}
                          </span>
                          <button
                            className={linkStyle}
                            data-testid={`${baseDataTestId}-delete-shared-link-${participant.participantId}`}
                            onClick={() =>
                              participant.participantId !== undefined &&
                              handleRemoveParticipant(String(participant.participantId))
                            }
                          >
                            {t('payapp.resume.removeLabel')}
                          </button>
                        </div>
                      </div>
                    );
                  })}
              </div>
            </>
          )}
        </div>

        {showShareAppWithColleague && !maxSharedParticipants && (
          <div className={formStyle} data-testid={`${baseDataTestId}-form-share-with-colleague`}>
            <h2
              data-testid={`${baseDataTestId}-share-colleague-title`}
              className="text-lg font-bold leading-8"
            >
              {t('payapp.resume.shareColleague.title')}
            </h2>
            <div className={'my-4'} data-testid={`${baseDataTestId}-notification`}>
              <Notification
                type="warning"
                icon={formatIBAssetsUrl(icons?.['icon.notification.alert'])}
                message={
                  <SanitizedContent
                    replacements={{
                      '{manageEmployeesLink}': getPathForLocale(
                        locale || LOCALES.EN,
                        'manage/employees'
                      ),
                    }}
                  >
                    {t('payapp.searchError')}
                  </SanitizedContent>
                }
              />
            </div>
            <div data-testid={`${baseDataTestId}-people-picker-container`} className={'mt-4'}>
              <Controller
                name="employeeId"
                control={control}
                render={({ field }) => (
                  <FormPeoplePicker
                    {...field}
                    id="PeoplePicker"
                    companyId={companyId}
                    errors={errors}
                    errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                    placeholder={t('payapp.resume.placeholder.text.search')}
                    setError={setError}
                    onBlur={() => {
                      trigger('employeeId');
                    }}
                    handleEmployeeChange={handleEmployeeChange}
                  />
                )}
              />
              <div className={buttonContainerStyle}>
                <Button
                  data-testid={`${baseDataTestId}-share-app`}
                  variant="dialogDefault"
                  className={buttonStyle}
                  onClick={handleShareApplication}
                  disabled={!selectedEmployee || isSubmitting}
                >
                  <Image
                    alt={'Share application icon'}
                    src={formatIBAssetsUrl(icons['icon.share.application'])}
                    width={26}
                    height={26}
                    className={buttonIconStyle}
                    data-testid={`${baseDataTestId}-share-app-icon`}
                  />
                  {t('payapp.resume.ctaApplication')}
                </Button>
              </div>
            </div>
          </div>
        )}

        <div className={'pt-12 pb-2'}>
          <SanitizedContent
            replacements={{
              '{contactUsLink}': getPathForLocale(locale || LOCALES.EN, 'contact-us'),
            }}
          >
            {t('payapp.resume.contactUs')}
          </SanitizedContent>
        </div>

        <div className={buttonContainerStyle} data-testid={`${baseDataTestId}-bottom-button`}>
          {bottomButton}
        </div>
      </div>
    </FormProvider>
  );
}

const containerStyle = 'relative w-[420px] flex flex-col mobile:w-full mx-auto';
const formStyle = 'mt-6 p-6 border border-lightGrey3 bg-white rounded-lg';
const fieldStyle = 'flex flex-col pb-4';

const linkStyle = 'underline text-secondaryColor underline-offset-2 text-right';
const buttonStyle = 'flex w-full';
const buttonContainerStyle = 'mt-4 flex flex-col gap-4';
const buttonIconStyle = 'mr-2';
