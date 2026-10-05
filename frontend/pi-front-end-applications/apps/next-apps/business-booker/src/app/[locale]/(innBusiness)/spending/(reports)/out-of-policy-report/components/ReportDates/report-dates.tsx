'use client';

import { FormInnB } from '@whitbread-eos/api';
import {
  ErrorTooltip,
  Button,
  SingleDatePickerUi,
  Notification,
  Skeleton,
} from '@whitbread-eos/atoms/ui';
import {
  getLocaleByPathname,
  useTranslation,
  formatIBAssetsUrl,
  getOutOfPolicyReport,
} from '@whitbread-eos/utils/server';
import { differenceInDays } from 'date-fns';
import { Download } from 'lucide-react';
import { usePathname } from 'next/navigation';
import { useCallback, useEffect, useState } from 'react';

import { FormRadioGroup } from '~components/innBusiness/FormRadioGroup/FormRadioGroup';

type Props = {
  icons: Record<string, string>;
  baseDataTestId: string;
  calendarLabels: FormInnB | Record<string, never>;
  token: string;
  companyId: string;
};

enum Interval {
  Yesterday = 'yesterday',
  LastWeek = 'lastWeek',
  LastMonth = 'lastMonth',
  LastYear = 'lastYear',
}

const ReportDates = ({ icons, baseDataTestId, calendarLabels = {}, token, companyId }: Props) => {
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const { t } = useTranslation('spending');
  const [interval, setInterval] = useState<Interval | null>(null);
  const [showCalendarError, setShowCalendarError] = useState<string | undefined>(undefined);
  const [showReportError, setShowReportError] = useState<string | undefined>(undefined);
  const [startDate, setStartDate] = useState<Date | undefined>(undefined);
  const [endDate, setEndDate] = useState<Date | undefined>(undefined);
  const [currentMonthStart, setCurrentMonthStart] = useState<Date>(new Date());
  const [currentMonthEnd, setCurrentMonthEnd] = useState<Date>(new Date());
  const today = new Date();
  const oneYearAfter = new Date(today);
  oneYearAfter.setFullYear(today.getFullYear() + 1);

  const yesterday = new Date(today);
  yesterday.setDate(today.getDate() - 1);

  const intervals = [
    {
      value: Interval.Yesterday,
      label: `${t('out.of.policy.report.yesterday')} - (${yesterday.toLocaleDateString(locale, {
        day: '2-digit',
        month: 'short',
        year: '2-digit',
      })})`,
    },
    {
      value: Interval.LastWeek,
      label: `${t('out.of.policy.report.last.week')} - (${new Date(
        new Date(yesterday).setDate(yesterday.getDate() - 7)
      ).toLocaleDateString(locale, {
        day: '2-digit',
        month: 'short',
        year: '2-digit',
      })} - ${yesterday.toLocaleDateString(locale, {
        day: '2-digit',
        month: 'short',
        year: '2-digit',
      })})`,
    },
    {
      value: Interval.LastMonth,
      label: `${t('out.of.policy.report.last.month')} - (${new Date(
        new Date(yesterday).setMonth(yesterday.getMonth() - 1)
      ).toLocaleDateString(locale, {
        day: '2-digit',
        month: 'short',
        year: '2-digit',
      })} - ${yesterday.toLocaleDateString(locale, {
        day: '2-digit',
        month: 'short',
        year: '2-digit',
      })})`,
    },
    {
      value: Interval.LastYear,
      label: `${t('out.of.policy.report.last.year')} - (${new Date(
        new Date(yesterday).setFullYear(yesterday.getFullYear() - 1)
      ).toLocaleDateString(locale, {
        day: '2-digit',
        month: 'short',
        year: '2-digit',
      })} - ${yesterday.toLocaleDateString(locale, {
        day: '2-digit',
        month: 'short',
        year: '2-digit',
      })})`,
    },
  ];

  useEffect(() => {
    if (interval) {
      setShowCalendarError(undefined);
      setShowReportError(undefined);
      const today = new Date();
      const yesterday = new Date(today);
      yesterday.setDate(today.getDate() - 1);
      const startDate = new Date(yesterday);
      const endDate = new Date(yesterday);

      switch (interval) {
        case Interval.Yesterday:
          setStartDate(startDate);
          setEndDate(endDate);
          break;
        case Interval.LastWeek:
          startDate.setDate(startDate.getDate() - 7);
          setStartDate(startDate);
          setEndDate(endDate);
          break;
        case Interval.LastMonth:
          startDate.setMonth(startDate.getMonth() - 1);
          setStartDate(startDate);
          setEndDate(endDate);
          break;
        case Interval.LastYear:
          startDate.setFullYear(startDate.getFullYear() - 1);
          setStartDate(startDate);
          setEndDate(endDate);
          break;
        default:
          break;
      }
    }
  }, [interval]);

  const handleDateChange = useCallback(
    (newDate: Date | undefined, type: string) => {
      setShowCalendarError(undefined);
      setShowReportError(undefined);
      setInterval(null);
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
      setShowCalendarError(undefined);
      try {
        const response = await getOutOfPolicyReport(
          token,
          companyId,
          startDate?.toISOString().split('T')[0],
          endDate?.toISOString().split('T')[0]
        );
        if (response === null) {
          setShowReportError(t('report.error.generic'));
        }
      } catch (error) {
        setShowReportError(t('report.error.generic'));
      }
    }
  }, [startDate, endDate, t, token, companyId]);

  const fromMonth = (type: string) => {
    if (type === 'end' && startDate) {
      return startDate;
    }
    if (type === 'start') {
      const oneYearAgo = new Date(today);
      oneYearAgo.setFullYear(today.getFullYear() - 1);
      return oneYearAgo;
    }
    return undefined;
  };

  const disabledDays = (type: string) => {
    return [{ before: fromMonth(type), after: oneYearAfter }];
  };

  const renderCalendarButtons = () => {
    return (
      <div
        className={datePickerContainerStyle}
        data-testid={`${baseDataTestId}-Single-Date-Picker`}
      >
        <SingleDatePickerUi
          fromMonth={fromMonth('start')}
          toMonth={oneYearAfter}
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
          toMonth={oneYearAfter}
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
      <p className={headingStyle}>{t('out.of.policy.report.choose.dates')}</p>
      <div className="mb-12">
        {showCalendarError ? (
          <ErrorTooltip
            icon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
            className={' mobile:flex'}
            content={showCalendarError}
            open={!!showCalendarError}
            data-testid={`${baseDataTestId}-ErrorTooltip`}
          >
            {renderCalendarButtons()}
          </ErrorTooltip>
        ) : (
          renderCalendarButtons()
        )}
      </div>
      <p className={headingStyle}>{t('out.of.policy.report.date.range.option')}</p>
      <FormRadioGroup
        key={interval === null ? 'reset' : interval}
        variant="address"
        items={intervals}
        errorIcon={icons['icon.notification.error']}
        selectedValue={interval}
        data-testid={`${baseDataTestId}-RadioGroup`}
        className={'mb-12 mt-4'}
        onChange={(value: string) => setInterval(value as Interval)}
      />
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
        data-testid={`${baseDataTestId}-Generate-Report`}
        variant="default"
        className="min-w-[288px] mobile:w-full"
      >
        <Download className={'mr-[0.5rem]'} color="white" width={24} height={24} />
        {t('out.of.policy.report.download')}
      </Button>
    </div>
  );
};

export default ReportDates;

export const ReportDatesSkeleton = () => {
  return (
    <div data-testid="ReportDatesSkeleton-container">
      <Skeleton className="h-8 w-full sm:w-1/4 mb-6" />
      <div className="mb-12 w-full sm:w-1/3">
        <div className="flex gap-4">
          <Skeleton className="h-12 w-1/2" />
          <Skeleton className="h-12 w-1/2" />
        </div>
      </div>
      <Skeleton className="h-8 w-full sm:w-1/4 mb-6" />
      <div className="mb-12 w-full sm:w-1/3">
        <Skeleton className="h-12 w-full mb-1" />
        <Skeleton className="h-12 w-full mb-1" />
        <Skeleton className="h-12 w-full mb-1" />
        <Skeleton className="h-12 w-full" />
      </div>
      <Skeleton className="h-12 w-full sm:w-1/4" />
    </div>
  );
};

const headingStyle = 'text-[1.25rem] font-bold';
const datePickerStyle = 'max-h-[3.5rem]';
const datePickerContainerStyle = 'relative inline-flex mobile:flex-1 mobile:w-full pt-6 gap-4';
