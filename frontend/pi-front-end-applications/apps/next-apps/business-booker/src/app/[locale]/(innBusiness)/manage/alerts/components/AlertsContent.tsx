'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { GlobalInnB, Currency } from '@whitbread-eos/api';
import { Button, useToast } from '@whitbread-eos/atoms/ui';
import { useTranslation } from '@whitbread-eos/utils';
import { ID_TOKEN_COOKIE, updateBookingAlerts } from '@whitbread-eos/utils/server';
import { getCookie } from 'cookies-next';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { useForm, FormProvider } from 'react-hook-form';
import { z } from 'zod';

import { ReviewChanges } from '~components/innBusiness/ReviewChanges';
import { AlertRecipients } from '~components/innBusiness/forms/AlertsForms/AlertRecipients';
import { AlertToggles } from '~components/innBusiness/forms/AlertsForms/AlertToggles';
import { FrequencyRadios } from '~components/innBusiness/forms/AlertsForms/FrequencyRadios';
import { HotelAlertsSelector } from '~components/innBusiness/forms/AlertsForms/HotelAlertsSelector';
import { PriceAlertsFormWrapper } from '~components/innBusiness/forms/AlertsForms/PriceAlertsFormWrapper';

type BookingAlerts = {
  dayOfArrival?: boolean;
  weekendArrival?: boolean;
  passThroughWeekend?: boolean;
  rateCaps?: {
    uKWide?: { amount: number; currency: string };
    greaterLondon?: { amount: number; currency: string };
    ireland?: { amount: number; currency: string };
  };
  frequency?: string;
  recipientEmailAddresses?: string[];
  bookingAlertHotels?: string[];
};

type Props = {
  icons: Record<string, string>;
  globalLabels: GlobalInnB;
  locationIcon: string;
  companyId: string;
  bookingAlerts?: BookingAlerts;
  language: string;
};

const frequencyMapping: Record<string, string> = {
  N: 'none',
  A: 'immediately',
  D: 'daily',
  W: 'weekly',
  M: 'monthly',
};

const apiFrequencyMapping: Record<string, string> = {
  none: 'N',
  immediately: 'A',
  daily: 'D',
  weekly: 'W',
  monthly: 'M',
};

export function AlertsContent({
  icons,
  globalLabels,
  locationIcon,
  companyId,
  bookingAlerts,
  language,
}: Props) {
  const baseDataTestId = 'AlertsPage';
  const { t } = useTranslation(['company', 'global', 'users']);
  const [isReviewOpen, setIsReviewOpen] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);
  const { toast } = useToast();
  const router = useRouter();

  const alertsFormSchema = z.object({
    alertToggles: z.object({
      sameDayBooking: z.boolean().default(true),
      weekendArrival: z.boolean().default(true),
      partWeekendBooking: z.boolean().default(true),
    }),
    priceAlerts: z.object({
      unitedKingdom: z
        .string()
        .regex(/^\d*$/, { message: t('company.coMngt.alerts.price.input.error') })
        .default('0'),
      greaterLondon: z
        .string()
        .regex(/^\d*$/, { message: t('company.coMngt.alerts.price.input.error') })
        .default('0'),
      germanyIreland: z
        .string()
        .regex(/^\d*$/, { message: t('company.coMngt.alerts.price.input.error') })
        .default('0'),
    }),
    frequency: z.string().default('daily'),
    recipients: z.array(
      z.object({
        id: z.string(),
        name: z.string(),
        email: z.string().email(),
      })
    ),
    selectedHotels: z.array(
      z.object({
        id: z.string(),
        code: z.string(),
        suggestion: z.string(),
        brand: z.string().optional(),
      })
    ),
    employee: z.string().optional(),
  });

  type AlertsFormData = z.infer<typeof alertsFormSchema>;

  const formMethods = useForm<AlertsFormData>({
    resolver: zodResolver(alertsFormSchema),
    defaultValues: {
      alertToggles: {
        sameDayBooking: bookingAlerts?.dayOfArrival ?? true,
        weekendArrival: bookingAlerts?.weekendArrival ?? true,
        partWeekendBooking: bookingAlerts?.passThroughWeekend ?? true,
      },
      priceAlerts: {
        unitedKingdom: bookingAlerts?.rateCaps?.uKWide?.amount?.toString() || '0',
        greaterLondon: bookingAlerts?.rateCaps?.greaterLondon?.amount?.toString() || '0',
        germanyIreland: bookingAlerts?.rateCaps?.ireland?.amount?.toString() || '0',
      },
      frequency: bookingAlerts?.frequency
        ? frequencyMapping[bookingAlerts.frequency] || 'daily'
        : 'daily',
      recipients: (bookingAlerts?.recipientEmailAddresses || [])
        .filter((email: string) => email && email.trim() !== '')
        .map((email: string, index: number) => ({
          id: `recipient-${index}`,
          name: email.split('@')[0],
          email,
        })),
      selectedHotels: [],
      employee: '',
    },
  });

  const { handleSubmit } = formMethods;

  const onSubmit = async (formData: AlertsFormData) => {
    setIsUpdating(true);

    try {
      const token = getCookie(ID_TOKEN_COOKIE) || '';

      if (!token) {
        return;
      }

      const transformedData = {
        dayOfArrival: formData.alertToggles.sameDayBooking,
        weekendArrival: formData.alertToggles.weekendArrival,
        passThroughWeekend: formData.alertToggles.partWeekendBooking,
        rateCaps: {
          uKWide: {
            amount: parseInt(formData.priceAlerts.unitedKingdom) || 0,
            currency: Currency.GBP_NAME,
          },
          greaterLondon: {
            amount: parseInt(formData.priceAlerts.greaterLondon) || 0,
            currency: Currency.GBP_NAME,
          },
          ireland: {
            amount: parseInt(formData.priceAlerts.germanyIreland) || 0,
            currency: Currency.GBP_NAME,
          },
        },
        frequency: apiFrequencyMapping[formData.frequency],
        bookingAlertHotels: formData.selectedHotels
          .map((hotel) => hotel.code)
          .filter((code) => code && code.trim() !== ''),
        recipientEmailAddresses: formData.recipients
          .map((r) => r.email)
          .filter((email) => email && email.trim() !== ''),
      };

      const response = await updateBookingAlerts(companyId, transformedData, token);

      if (response?.status === 'success') {
        toast({
          content: t('company.notification.message.save'),
        });
        router.refresh();
        window.scrollTo({ top: 0, behavior: 'smooth' });
      } else {
        toast({
          content: t('company.notification.message.error'),
          variant: 'error',
        });
      }
    } catch (error) {
      toast({
        content: t('company.notification.message.error'),
        variant: 'error',
      });
    } finally {
      setIsUpdating(false);
    }
  };

  const handleDiscardChanges = () => {
    setIsReviewOpen(true);
  };

  const handleConfirmDiscard = () => {
    setIsReviewOpen(false);
    router.refresh();
  };

  const handleContinueEditing = () => {
    setIsReviewOpen(false);
  };

  return (
    <FormProvider {...formMethods}>
      <form onSubmit={handleSubmit(onSubmit)}>
        <div data-testid={`${baseDataTestId}-container`} className={pageStyle}>
          <h1 data-testid={`${baseDataTestId}-container-title`} className={h1Style}>
            {t('company.coMngt.alerts.title')}
          </h1>
          <div data-testid={`${baseDataTestId}-container-subtitle`} className={subtitleStyle}>
            {t('company.coMngt.alerts.subtitle')}
          </div>

          <div className={alertsSectionStyle}>
            <div className={sectionHeaderStyle}>
              <h2 data-testid={`${baseDataTestId}-alerts-title`} className={sectionTitleStyle}>
                {t('company.coMngt.alerts.booking.title')}
              </h2>
              <div
                data-testid={`${baseDataTestId}-alerts-subtitle`}
                className={sectionSubtitleStyle}
              >
                {t('company.coMngt.alerts.booking.subtitle')}
              </div>
            </div>
            <AlertToggles />
          </div>

          <div className={hotelAlertsSectionStyle}>
            <div className={sectionHeaderStyle}>
              <h2 data-testid={`${baseDataTestId}-hotelAlerts-title`} className={sectionTitleStyle}>
                {t('company.coMngt.alerts.hotel.title')}
              </h2>
              <div
                data-testid={`${baseDataTestId}-hotelAlerts-subtitle`}
                className={sectionSubtitleStyle}
              >
                {t('company.coMngt.alerts.hotel.subtitle')}
              </div>
            </div>
            <HotelAlertsSelector
              globalLabels={globalLabels}
              locationIcon={locationIcon}
              initialHotelIds={bookingAlerts?.bookingAlertHotels || []}
              language={language}
            />
          </div>

          <div className={priceAlertsSectionStyle}>
            <div className={sectionHeaderStyle}>
              <h2 data-testid={`${baseDataTestId}-priceAlerts-title`} className={sectionTitleStyle}>
                {t('company.coMngt.alerts.price.title')}
              </h2>
              <div
                data-testid={`${baseDataTestId}-priceAlerts-subtitle`}
                className={sectionSubtitleStyle}
              >
                {t('company.coMngt.alerts.price.subtitle')}
              </div>
            </div>
            <PriceAlertsFormWrapper icons={icons} />
          </div>

          <div className={recipientsSectionStyle}>
            <div className={sectionHeaderStyle}>
              <h2 data-testid={`${baseDataTestId}-recipients-title`} className={sectionTitleStyle}>
                {t('company.coMngt.alerts.recipients.title')}
              </h2>
              <div
                data-testid={`${baseDataTestId}-recipients-subtitle`}
                className={sectionSubtitleStyle}
              >
                {t('company.coMngt.alerts.recipients.subtitle')}
              </div>
            </div>
            <AlertRecipients icons={icons} companyId={companyId} />
          </div>

          <div className={frequencySectionStyle}>
            <div className={sectionHeaderStyle}>
              <h2 data-testid={`${baseDataTestId}-frequency-title`} className={sectionTitleStyle}>
                {t('company.coMngt.alerts.frequency.title')}
              </h2>
              <div
                data-testid={`${baseDataTestId}-frequency-subtitle`}
                className={sectionSubtitleStyle}
              >
                {t('company.coMngt.alerts.frequency.subtitle')}
              </div>
            </div>
            <FrequencyRadios />
          </div>

          <div className="w-full md:w-[620px] mt-12 mb-6 flex flex-col md:inline-flex md:flex-row justify-start items-center gap-4">
            <Button
              data-testid={`${baseDataTestId}-save-button`}
              variant="dialogDefault"
              type="submit"
              disabled={isUpdating}
              className="self-stretch md:self-auto w-full md:w-72 max-h-14 min-h-14 px-8 py-4 text-lg font-semibold order-1 md:order-2"
            >
              {t('company.coMngt.saveUpdatesButton')}
            </Button>

            <div className="md:flex-1 rounded inline-flex justify-center md:justify-start items-center gap-2 order-2 md:order-1">
              <Button
                data-testid={`${baseDataTestId}-discard-button`}
                variant="link"
                onClick={handleDiscardChanges}
                disabled={isUpdating}
                className="text-base font-medium underline text-secondaryColor pl-0"
              >
                {t('company.coMngt.discardChangesLink')}
              </Button>
            </div>
          </div>
        </div>
      </form>

      {!isUpdating && (
        <ReviewChanges
          isOpen={isReviewOpen}
          onDiscard={handleConfirmDiscard}
          onContinue={handleContinueEditing}
        />
      )}
    </FormProvider>
  );
}

const pageStyle =
  'px-12 pt-12 mobile:pt-6 min-w-[700px] mobile:min-w-full mobile:px-4 pb-12 w-full [@media(min-width:120rem)]:max-w-[81.75rem] [@media(min-width:120rem)]:mx-auto';
const h1Style =
  'text-[2.5rem] font-black leading-[2.75rem] mobile:leading-9 text-secondaryColor mobile:text-[1.75rem] max-w-[620px] mobile:max-w-full break-words';
const subtitleStyle = 'mt-4 text-darkGrey1 font-normal max-w-[620px] mb-6';
const alertsSectionStyle = 'mt-8 max-w-[620px] flex flex-col gap-10';
const hotelAlertsSectionStyle = 'mt-8 max-w-[620px] flex flex-col gap-6';
const priceAlertsSectionStyle = 'mt-8 max-w-[620px] flex flex-col gap-6';
const recipientsSectionStyle = 'mt-8 max-w-[620px] flex flex-col gap-6';
const frequencySectionStyle = 'mt-8 max-w-[620px] flex flex-col gap-6';
const sectionHeaderStyle = 'self-stretch flex flex-col justify-start items-start gap-2';
const sectionTitleStyle = 'text-neutral-900 text-xl font-bold leading-normal';
const sectionSubtitleStyle = 'self-stretch text-neutral-900 text-base font-normal leading-normal';
