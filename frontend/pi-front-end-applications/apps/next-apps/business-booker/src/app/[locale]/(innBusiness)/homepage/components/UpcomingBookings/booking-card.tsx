import { LOCALES, UpcomingBookingsResponse } from '@whitbread-eos/api';
import { Skeleton } from '@whitbread-eos/atoms/ui';
import { getCountryLanguageByLocale, formatIBAssetsUrl } from '@whitbread-eos/utils/server';
import { format } from 'date-fns';
import { de, enGB } from 'date-fns/locale';
import Image from 'next/image';
import Link from 'next/link';

type Props = {
  booking: UpcomingBookingsResponse;
  baseDataTestId: string;
  t: (key: string) => string;
  locale: LOCALES;
  hubBadge: string;
};

export function BookingCard({ locale, booking, t, baseDataTestId, hubBadge }: Props) {
  const { language, country } = getCountryLanguageByLocale(locale);
  const isHub = booking?.brand?.toLowerCase() === 'hub';
  const dateLocale = locale === LOCALES.DE ? de : enGB;

  return (
    <div className={bookingStyle} data-testid={`${baseDataTestId}-BookingCard`}>
      <div
        className={bookingImageStyle}
        data-testid={`${baseDataTestId}-BookingCard-ImageContainer`}
      >
        {booking?.galleryImages?.[0] && (
          <Image
            alt={booking?.galleryImages?.[0]?.alt ?? ''}
            src={formatIBAssetsUrl(booking?.galleryImages?.[0]?.imageSrc ?? '')}
            layout="fill"
            objectFit="cover"
            className={'w-full'}
            priority={true}
            data-testid={`${baseDataTestId}-Booking-image`}
          />
        )}
        {isHub && (
          <Image
            alt={booking?.hotelName ?? ''}
            src={formatIBAssetsUrl(hubBadge)}
            width={48}
            height={28}
            className={hotelBadgeStyle}
            priority={true}
            data-testid={`${baseDataTestId}-Booking-hub-badge`}
          />
        )}
      </div>
      <div
        className={bookingDetailsStyle}
        data-testid={`${baseDataTestId}-BookingCard-InfoContainer`}
      >
        <h2 className={'text-lg font-bold'} data-testid={`${baseDataTestId}-BookingCard-HotelName`}>
          {booking?.hotelName ?? ''}
        </h2>
        <div className={colsStyle} data-testid={`${baseDataTestId}-BookingCard-Dates`}>
          <div
            className={`${colStyle} border-r-[1px] border-lightGrey3`}
            data-testid={`${baseDataTestId}-BookingCard-ArrivingContainer`}
          >
            <span
              className={colTitleStyle}
              data-testid={`${baseDataTestId}-BookingCard-ArrivingLabel`}
            >
              {t('homepage.home.innbusinessPay.upcomingBookings.arriving')}
            </span>
            <span
              className={colTextStyle}
              data-testid={`${baseDataTestId}-BookingCard-ArrivingDate`}
            >
              {booking?.arrivalDate
                ? format(new Date(booking.arrivalDate), 'EEE dd MMM', { locale: dateLocale })
                : ''}
            </span>
            <span
              className={colTextStyle}
              data-testid={`${baseDataTestId}-BookingCard-ArrivingCheckIn`}
            >
              {t('homepage.home.innbusinessPay.upcomingBookings.from')}{' '}
              {booking?.arrivalTime
                ? format(new Date(`1970-01-01T${booking.arrivalTime}`), 'ha').toLowerCase()
                : ''}
            </span>
          </div>
          <div className={colStyle} data-testid={`${baseDataTestId}-BookingCard-LeavingContainer`}>
            <span
              className={colTitleStyle}
              data-testid={`${baseDataTestId}-BookingCard-LeavingLabel`}
            >
              {t('homepage.home.innbusinessPay.upcomingBookings.leaving')}
            </span>
            <span
              className={colTextStyle}
              data-testid={`${baseDataTestId}-BookingCard-LeavingDate`}
            >
              {booking?.departureDate
                ? format(new Date(booking.departureDate), 'EEE dd MMM', { locale: dateLocale })
                : ''}
            </span>
            <span
              className={colTextStyle}
              data-testid={`${baseDataTestId}-BookingCard-LeavingCheckOut`}
            >
              {t('homepage.home.innbusinessPay.upcomingBookings.before')}{' '}
              {booking?.departureTime
                ? format(new Date(`1970-01-01T${booking.departureTime}`), 'ha').toLowerCase()
                : ''}
            </span>
          </div>
        </div>
        <Link
          className={linkStyle}
          href={`/${country}/${language}/business-booker/account/dashboard.html?reference=${booking?.bookingReference}`}
          data-testid={`${baseDataTestId}-BookingCard-ViewDetails`}
          prefetch={false}
        >
          {t('homepage.home.innbusinessPay.upcomingBookings.viewBookingDetails.link')}
        </Link>
      </div>
    </div>
  );
}

export function BookingCardSkeleton() {
  return (
    <div className={bookingStyle}>
      <div className={bookingImageStyle}>
        <Skeleton className="w-full h-full" />
      </div>
      <div className={bookingDetailsStyle}>
        <Skeleton className="h-6 w-1/2 mb-2" />
        <div className={colsStyle}>
          <div className={`${colStyle} border-r-[1px] border-lightGrey3`}>
            <Skeleton className="h-6 w-3/4 mb-1" />
            <Skeleton className="h-6 w-1/2 mb-1" />
            <Skeleton className="h-6 w-1/3" />
          </div>
          <div className={colStyle}>
            <Skeleton className="h-6 w-3/4 mb-1" />
            <Skeleton className="h-6 w-1/2 mb-1" />
            <Skeleton className="h-6 w-1/3" />
          </div>
        </div>
        <Skeleton className="h-6 w-1/2 mt-4" />
      </div>
    </div>
  );
}

const bookingStyle =
  'flex flex-col md:flex-row w-full md:w-2/3 border-[1px] border-lightGrey3 rounded-lg overflow-hidden';
const bookingImageStyle =
  'md:flex-1 min-h-[216px] relative bg-lightGrey4 md:w-1/2 overflow-hidden relative';
const hotelBadgeStyle = 'absolute top-2 left-0';
const bookingDetailsStyle =
  'md:flex-1 min-h-[216px] flex flex-col p-4 justify-between md:w-1/2 overflow-hidden';
const linkStyle = 'text-secondaryColor text-md underline';
const colTitleStyle = 'text-md font-bold';
const colStyle = 'flex flex-col flex-1 w-1/2 overflow-hidden';
const colsStyle = 'flex flex-row gap-4 md:gap-2';
const colTextStyle = 'text-md font-normal';
