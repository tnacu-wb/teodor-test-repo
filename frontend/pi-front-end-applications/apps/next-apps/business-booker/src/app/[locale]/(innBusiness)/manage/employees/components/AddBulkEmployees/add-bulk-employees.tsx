'use client';

import { LOCALES } from '@whitbread-eos/api';
import {
  FormPage,
  Notification,
  SanitizedContent,
  Button,
  useToast,
} from '@whitbread-eos/atoms/ui';
import {
  useTranslation,
  getPathForLocale,
  getLocaleByPathname,
  formatIBAssetsUrl,
  cn,
} from '@whitbread-eos/utils';
import { bulkUploadEmployees } from '@whitbread-eos/utils/server';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import React, { useRef, useState } from 'react';

import {
  Analytics,
  EmployeeAnalyticsRef,
  PageNames,
} from '~components/innBusiness/ManageEmployeesAnalytics';

type Props = {
  icons: Record<string, string>;
  token: string;
  companyId: string;
};

type BulkUploadError = {
  errorCode: string;
  errorDescription: string;
};

const validErrorCodes = new Set(['282', '283', '284', '285', '286', '287', '288', '289', '293']);

export const groupErrorsByCode = (errors: BulkUploadError[]): Record<string, string[]> =>
  errors.reduce<Record<string, string[]>>((grouped, { errorCode, errorDescription }) => {
    if (!validErrorCodes.has(errorCode)) {
      return grouped;
    }
    if (!grouped[errorCode]) {
      grouped[errorCode] = [];
    }
    grouped[errorCode].push(errorDescription);
    return grouped;
  }, {});

export const getBulkUploadFailureMessages = (
  errors: Record<string, string[]>,
  t: (key: string) => string
): string[] => {
  const messages: Record<string, string> = {
    '282': t('userMgmt.employee.alreadyExists'),
    '283': t('userMgmt.employee.emailAddress.empty'),
    '284': t('userMgmt.employee.emailAddress.invalid'),
    '285': t('userMgmt.employee.title.empty'),
    '286': t('userMgmt.employee.firstName.empty'),
    '287': t('userMgmt.employee.lastName.empty'),
    '288': t('userMgmt.employee.textConfirmation.empty'),
    '289': t('userMgmt.employee.accessLevel.empty'),
  };
  return Object.entries(errors).map(([code, rows]) => {
    const baseMessage = messages[code];
    return rows.length ? `${baseMessage} ${rows.join(', ')}` : baseMessage;
  });
};

export function AddBulkEmployees({ icons, token, companyId }: Props) {
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const { t } = useTranslation('users');
  const inputRef = useRef<HTMLInputElement | null>(null);
  const [file, setFile] = useState<File | null>(null);
  const { toast } = useToast();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isPartiallyProcessed, setIsPartiallyProcessed] = useState(false);
  const [isError, setShowError] = useState(false);
  const [errorMessages, setErrorMessages] = useState<string[]>([]);
  const router = useRouter();

  const analyticsRef = useRef<EmployeeAnalyticsRef | null>(null);
  const bulkUploadFailureCaption = isPartiallyProcessed
    ? t('userMgmt.employee.bulkUpload.uploadFile.partiallyProcessed')
    : t('userMgmt.employee.bulkUpload.failure');

  const setShowErrorTrackAnalytics = (isError: boolean) => {
    analyticsRef?.current?.setValidation(isError ? bulkUploadFailureCaption : null);
    setShowError(isError);
  };

  const handleUploadSubmit = async () => {
    setIsSubmitting(true);
    setIsPartiallyProcessed(false);
    setErrorMessages([]);
    setShowErrorTrackAnalytics(false);
    const res = await bulkUploadEmployees(token, companyId, file, locale);
    if (res?.ok) {
      analyticsRef.current?.setAddEmployeeAnalyticsData({
        addEmployee: true,
        employee: 'bulk upload',
      });
      analyticsRef.current?.setPageName(PageNames.MANAGE_EMPLOYEES_SUCCESSFUL_ACCOUNT_CREATION);

      router.push(getPathForLocale(locale, 'manage/employees'));
      toast({
        content: t('userMgmt.employee.bulkUpload.success'),
      });
      return;
    }
    if (res) {
      const body = await res
        .clone()
        .json()
        .catch(() => res.text().catch(() => null));
      const hasEmployeeProcessed =
        Array.isArray(body) &&
        (body as BulkUploadError[]).some(({ errorCode }) => errorCode === '293');
      setIsPartiallyProcessed(hasEmployeeProcessed);
      const errors = Array.isArray(body)
        ? (body as BulkUploadError[]).filter(({ errorCode }) => errorCode !== '293')
        : [];
      const groupedErrors = errors.length > 0 ? groupErrorsByCode(errors) : {};
      if (Object.keys(groupedErrors).length > 0) {
        const failureMessages = getBulkUploadFailureMessages(groupedErrors, t);
        setErrorMessages(failureMessages);
      } else {
        setErrorMessages([]);
      }
    }
    if (inputRef.current) {
      inputRef.current.value = '';
    }
    setIsSubmitting(false);
    setShowErrorTrackAnalytics(true);
  };

  return (
    <FormPage
      iconClassName="px-2 py-3 max-w-[3rem]"
      baseDataTestId={'Add-Employees-Bulk'}
      backIcon={formatIBAssetsUrl(icons['icon.arrow.left.purple'])}
      backHref={getPathForLocale(locale as LOCALES, `manage/employees/add`)}
      title={t('userMgmt.employee.bulkUpload.heading')}
    >
      <div data-testid="Bulk-Add-Container" className={containerStyle}>
        <span className={cn(listItemStyle, 'mt-12 mb-0')}>
          <span className={listMarkerStyle}>1.</span>
          {t('userMgmt.employee.bulkUpload.step1')}
        </span>
        <Link
          className={linkStyle}
          href={formatIBAssetsUrl(t('userMgmt.employee.bulkUpload.newExcelFile.linkUrl'))}
          data-testid="Download-New-Excel"
        >
          {t('userMgmt.employee.bulkUpload.newExcelFile.linkText')}
        </Link>
        <Link
          className={linkStyle}
          href={formatIBAssetsUrl(t('userMgmt.employee.bulkUpload.oldExcelFile.linkUrl'))}
          data-testid="Download-Old-Excel"
        >
          {t('userMgmt.employee.bulkUpload.oldExcelFile.linkText')}
        </Link>

        <span className={cn(listItemStyle, 'my-12')}>
          <span className={listMarkerStyle}>2.</span>
          {t('userMgmt.employee.bulkUpload.step2')}
        </span>

        <span className={listItemStyle}>
          <span className={listMarkerStyle}>3.</span>
          {t('userMgmt.employee.bulkUpload.step3')}
        </span>

        <Button
          data-testid="Bulk-Upload-File"
          variant="default"
          className={buttonStyle}
          onClick={() => inputRef.current?.click()}
          disabled={isSubmitting}
        >
          {t('userMgmt.employee.bulkUpload.uploadFile')}
        </Button>

        <input
          data-testid="FileInput"
          type="file"
          ref={inputRef}
          accept=".xlsx, .xls"
          className={inputStyle}
          onChange={(e) => {
            const files = e.target.files;
            if (files && files.length > 0) {
              setFile(files[0]);
              setShowError(false);
              setIsPartiallyProcessed(false);
            }
          }}
        />

        {file && (
          <>
            <span className={cn(listItemStyle, 'mt-12')}>
              <span className={listMarkerStyle}>4.</span>
              <SanitizedContent replacements={{ '{filename}': file.name }}>
                {t('userMgmt.employee.bulkUpload.uploadFile.selected')}
              </SanitizedContent>
            </span>
            {isError && (
              <Notification
                className={notificationErrorStyle}
                type="error"
                icon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                message={
                  errorMessages.length > 0 ? (
                    <>
                      <SanitizedContent>{bulkUploadFailureCaption}</SanitizedContent>
                      <ul className="list-disc pl-4">
                        {errorMessages.map((message) => (
                          <li key={message}>{message}</li>
                        ))}
                      </ul>
                    </>
                  ) : (
                    <SanitizedContent>{t('userMgmt.employee.bulkUpload.failure')}</SanitizedContent>
                  )
                }
              />
            )}
            <Button
              data-testid="Bulk-Upload-Submit"
              variant="dialogDefault"
              className={buttonStyle}
              onClick={handleUploadSubmit}
              disabled={isSubmitting || isError}
            >
              {t('userMgmt.employee.bulkUpload.submit')}
            </Button>
            <Notification
              className={notificationStyle}
              type="info"
              icon={formatIBAssetsUrl(icons['icon.notification.info'])}
              title={t('userMgmt.employee.add.infobox.heading')}
              message={
                <SanitizedContent>
                  {t('userMgmt.employee.bulkUpload.infobox.description')}
                </SanitizedContent>
              }
            />
          </>
        )}
      </div>
      <Analytics ref={analyticsRef} />
    </FormPage>
  );
}

const containerStyle = 'flex flex-col';
const linkStyle = 'flex font-medium text-sm underline text-secondaryColor mt-4';
const notificationStyle = 'mt-12';
const notificationErrorStyle = 'mb-4';
const buttonStyle = 'flex w-full';
const listMarkerStyle = 'font-bold mr-[4px]';
const listItemStyle = 'mb-4';
const inputStyle = 'hidden';
