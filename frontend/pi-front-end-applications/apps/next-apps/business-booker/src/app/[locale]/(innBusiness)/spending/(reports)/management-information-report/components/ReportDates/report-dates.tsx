'use client';

import { FormInnB, Language } from '@whitbread-eos/api';
import {
  SanitizedContent,
  ErrorTooltip,
  Checkbox,
  Button,
  SingleDatePickerUi,
  Notification,
} from '@whitbread-eos/atoms/ui';
import {
  getPathForLocale,
  getLocaleByPathname,
  useTranslation,
  formatIBAssetsUrl,
  downloadFromS3PreSignedUrl,
} from '@whitbread-eos/utils';
import { getManagementInformationReport } from '@whitbread-eos/utils/server';
import { differenceInDays } from 'date-fns';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useCallback, useState } from 'react';

type Props = {
  icons: Record<string, string>;
  baseDataTestId: string;
  calendarLabels: FormInnB | Record<string, never>;
  language: Language;
  token: string;
};

const ReportDates = ({ icons, baseDataTestId, calendarLabels = {}, language, token }: Props) => {
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const { t } = useTranslation('spending');
  const [includeQuestions, setIncludeQuestions] = useState(false);
  const [showCalendarError, setShowCalendarError] = useState<string | undefined>(undefined);
  const [showReportError, setShowReportError] = useState<string | undefined>(undefined);
  const [startDate, setStartDate] = useState<Date | undefined>(undefined);
  const [endDate, setEndDate] = useState<Date | undefined>(undefined);
  const [currentMonthStart, setCurrentMonthStart] = useState<Date>(new Date());
  const [currentMonthEnd, setCurrentMonthEnd] = useState<Date>(new Date());
  const today = new Date();

  const handleDateChange = useCallback(
    (newDate: Date | undefined, type: string) => {
      setShowCalendarError(undefined);
      setShowReportError(undefined);
      const resetEndDate =
        (endDate && newDate && newDate > endDate) ||
        (endDate && newDate && differenceInDays(endDate, newDate) > 365);
      if (type === 'start') {
        setStartDate(newDate);
        if (resetEndDate) {
          setEndDate(undefined);
        }
      } else {
        setEndDate(newDate);
      }
    },
    [endDate]
  );

  const handleGenerateReport = useCallback(async () => {
    if (!(startDate && endDate)) {
      const errorMessage = t('report.error.missing.dates');
      return setShowCalendarError(errorMessage);
    } else {
      let response;
      setShowCalendarError(undefined);
      const startDateCopy = new Date(startDate);
      const endDateCopy = new Date(endDate);
      endDateCopy.setHours(23, 59, 59, 999);
      startDateCopy.setHours(23, 59, 59, 999);

      const formattedFromDate = startDate ? startDateCopy.toISOString().split('T')[0] : '';
      const formattedToDate = endDate ? endDateCopy.toISOString().split('T')[0] : '';
      try {
        response = await getManagementInformationReport(
          token,
          formattedFromDate,
          formattedToDate,
          includeQuestions,
          language
        );
        if (response?.data?.managementInformationReport && !response?.data?.errors) {
          const { downloadUrl, fileName } = response?.data?.managementInformationReport ?? {};
          downloadFromS3PreSignedUrl(downloadUrl, fileName);
          setShowReportError(undefined);
          if (includeQuestions) {
            setIncludeQuestions(false);
          }
        } else if (response?.errors) {
          const errorType = response.errors[0]?.extensions?.errorType;
          setShowReportError(
            errorType === 404 ? t('report.error.nobookings') : t('report.error.generic')
          );
        }
      } catch (error) {
        setShowReportError(t('report.error.generic'));
      }
    }
  }, [startDate, endDate, t, token, includeQuestions, language]);

  const onCheck = () => {
    setIncludeQuestions(!includeQuestions);
  };

  const fromMonth = (type: string) => {
    if (type === 'end' && startDate) {
      return startDate;
    }
    if (type === 'start') {
      const threeYearsAgo = new Date(today);
      threeYearsAgo.setFullYear(today.getFullYear() - 3);
      return threeYearsAgo;
    }
    return undefined;
  };

  const toMonth = (type: string) => {
    const yesterday = new Date(today);
    yesterday.setDate(today.getDate() - 1);
    if (type === 'end' && startDate) {
      const oneYearFromStartDate = new Date(startDate || today);
      oneYearFromStartDate.setDate(oneYearFromStartDate.getDate() + 365);
      const rangeOrYesterday = oneYearFromStartDate > yesterday ? yesterday : oneYearFromStartDate;
      return rangeOrYesterday;
    }
    return yesterday;
  };

  const disabledDays = (type: string) => {
    return [{ before: fromMonth(type), after: toMonth(type) }];
  };

  const renderCalendarButtons = () => {
    return (
      <div
        className={datePickerContainerStyle}
        data-testid={'IB-Desktop-Calendar-Single-Date-Picker'}
      >
        <SingleDatePickerUi
          fromMonth={fromMonth('start')}
          toMonth={today}
          selectedDay={startDate}
          currentMonth={currentMonthStart}
          setCurrentMonth={setCurrentMonthStart}
          className={datePickerStyle}
          locale={locale}
          formLabels={calendarLabels}
          icons={icons}
          onDateChange={(value: Date | undefined) => {
            handleDateChange(value, 'start');
          }}
          showError={showCalendarError}
          setShowError={(value: string | undefined) => setShowCalendarError(value)}
          placeholder={t('spending.reporting.start.date')}
          disabled={disabledDays('start')}
        />
        <SingleDatePickerUi
          fromMonth={fromMonth('end')}
          toMonth={today}
          selectedDay={endDate}
          currentMonth={currentMonthEnd}
          setCurrentMonth={setCurrentMonthEnd}
          className={datePickerStyle}
          locale={locale}
          formLabels={calendarLabels}
          icons={icons}
          onDateChange={(value: Date | undefined) => {
            handleDateChange(value, 'end');
          }}
          showError={showCalendarError}
          setShowError={(value: string | undefined) => setShowCalendarError(value)}
          placeholder={t('spending.reporting.end.date')}
          disabled={disabledDays('end')}
        />
      </div>
    );
  };

  return (
    <div data-testid={`${baseDataTestId}-container`}>
      <p className={headingStyle}>{t('management.info.report.choose.dates')}</p>
      {showCalendarError ? (
        <ErrorTooltip
          icon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
          className={' mobile:flex'}
          content={showCalendarError}
          open={!!showCalendarError}
          testId="IB-Date-Picker-ErrorTooltip"
        >
          {renderCalendarButtons()}
        </ErrorTooltip>
      ) : (
        renderCalendarButtons()
      )}
      <div className={checkboxWrapperStyle}>
        <div className="inline-flex items-center">
          <Checkbox
            id="includeEmployeeQuestions"
            checked={includeQuestions}
            onCheckedChange={onCheck}
            className={checkboxStyle}
          />
          <label htmlFor="includeEmployeeQuestions" className={labelStyle}>
            <SanitizedContent>{t('management.info.report.include')}</SanitizedContent>
          </label>
        </div>
        <Link className={ManageQuestionsLink} href={getPathForLocale(locale, 'manage/questions')}>
          {t('management.info.report.manage.questions.link')}
        </Link>
      </div>
      {showReportError && (
        <Notification
          className={'mb-12 max-w-[39.063rem] mobile:max-w-[100%]'}
          type="error"
          icon={formatIBAssetsUrl(icons['icon.notification.error'])}
          title={''}
          message={showReportError}
        />
      )}
      <Button
        onClick={() => handleGenerateReport()}
        data-testid="IB-Generate-Report"
        variant="default"
      >
        {t('management.info.report.generate.button')}
      </Button>
    </div>
  );
};

export default ReportDates;

const headingStyle = 'text-[1.25rem] font-bold';
const checkboxWrapperStyle = 'pt-6 pb-12 flex items-center mobile:flex-col mobile:items-start';
const checkboxStyle = 'mr-2 border-neutral-600';
const labelStyle = 'cursor-pointer text-neutral-900';
const datePickerStyle = 'max-h-[3.5rem]';
const datePickerContainerStyle =
  'relative inline-flex mobile:flex-1 mobile:min-w-[7.25rem] pt-6 gap-4';
const ManageQuestionsLink =
  'inline-block text-secondaryColor underline hover:no-underline ml-2 mobile:ml-[1.75rem]';
