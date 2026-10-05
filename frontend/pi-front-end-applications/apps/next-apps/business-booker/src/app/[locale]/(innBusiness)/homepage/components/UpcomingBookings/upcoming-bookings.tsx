import {
  LOCALES,
  BUSINESS_BOOKER_USER_ROLES,
  UpcomingBookingsResponse,
  Channel,
} from '@whitbread-eos/api';
import { Skeleton } from '@whitbread-eos/atoms/ui';
import {
  getCountryLanguageByLocale,
  getTranslations,
  formatIBAssetsUrl,
  getAccessLevel,
  getUpcomingBookings,
  ID_TOKEN_COOKIE,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import Image from 'next/image';
import Link from 'next/link';

import Analytics from '../Analytics/analytics';
import { BookingCard, BookingCardSkeleton } from './booking-card';
import { NoBookingCard } from './no-booking-card';

type Props = {
  locale: LOCALES;
};

export async function UpcomingBookings({ locale }: Props) {
  const baseDataTestId = `UpcomingBookings`;
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { language, country } = getCountryLanguageByLocale(locale);
  const [{ t }, { accessLevel }, response] = await Promise.all([
    getTranslations(language, ['homepage', 'icons']),
    getAccessLevel(),
    getUpcomingBookings(token, {
      country,
      language,
      channel: Channel.Bb,
      subchannel: 'web',
    }),
  ]);

  const upcomingBooking: UpcomingBookingsResponse = response?.data?.getUpcomingBookings ?? null;
  const shouldDisplayBookings = [
    BUSINESS_BOOKER_USER_ROLES.SUPER,
    BUSINESS_BOOKER_USER_ROLES.BOOKER,
  ].includes(accessLevel);

  return (
    <section className={containerStyle} data-testid={`${baseDataTestId}-Container`}>
      <div className="flex flex-col md:flex-row md:justify-between md:items-center mb-4">
        <h2 className={h2Style} data-testid={`${baseDataTestId}-Heading`}>
          {t('homepage.home.innbusinessPay.upcomingBookings.heading')}
        </h2>
        <Link
          className={linkStyle}
          href={`/${country}/${language}/business-booker/account/dashboard.html`}
          data-testid={`${baseDataTestId}-ViewAllBookings`}
          prefetch={false}
        >
          {t('homepage.home.innbusinessPay.upcomingBookings.viewAllBookings.link')}
        </Link>
      </div>
      <div className={bookingsWrapper} data-testid={`${baseDataTestId}-BookingCardContainer`}>
        {upcomingBooking?.hotelName ? (
          <BookingCard
            booking={upcomingBooking}
            baseDataTestId={baseDataTestId}
            t={t}
            locale={locale}
            hubBadge={t('icons.content.global.brand.hubBadge')}
          />
        ) : (
          <NoBookingCard
            searchIcon={t('icons.icon.input.search') ?? ''}
            baseDataTestId={baseDataTestId}
            t={t}
          />
        )}
        <div className={countersStyle} data-testid={`${baseDataTestId}-CountersContainer`}>
          <div
            className={`${counterStyle} ${
              shouldDisplayBookings ? 'justify-between' : singleCounterStyle
            }`}
            data-testid={`${baseDataTestId}-StaysContainer`}
          >
            <h3 className={counterTitleStyle} data-testid={`${baseDataTestId}-StaysLabel`}>
              {t('homepage.home.innbusinessPay.upcomingBookings.myStays')}
            </h3>
            <div
              className={iconWrapperStyle}
              data-testid={`${baseDataTestId}-StaysCounterContainer`}
            >
              <Image
                alt={'Stays icon'}
                src={formatIBAssetsUrl(t('icons.icon.checkin-early-icon'))}
                width={32}
                height={32}
                className={iconStyle}
                data-testid={`${baseDataTestId}-Stays-icon`}
              />
              <span className={counterNoStyle} data-testid={`${baseDataTestId}-StaysCounter`}>
                {upcomingBooking?.stays ?? 0}
              </span>
            </div>
          </div>
          {shouldDisplayBookings && (
            <div
              className={`${counterStyle} justify-between`}
              data-testid={`${baseDataTestId}-BookingsContainer`}
            >
              <h3 className={counterTitleStyle} data-testid={`${baseDataTestId}-BookingsLabel`}>
                {t('homepage.home.innbusinessPay.upcomingBookings.bookings')}
              </h3>
              <div
                className={iconWrapperStyle}
                data-testid={`${baseDataTestId}-BookingsCounterContainer`}
              >
                <Image
                  alt={'Bookings icon'}
                  src={formatIBAssetsUrl(t('icons.icon.bookings-icon'))}
                  width={32}
                  height={32}
                  className={iconStyle}
                  data-testid={`${baseDataTestId}-Bookings-icon`}
                />
                <span className={counterNoStyle} data-testid={`${baseDataTestId}-BookingsCounter`}>
                  {upcomingBooking?.bookings ?? 0}
                </span>
              </div>
            </div>
          )}
        </div>
      </div>
      <Analytics stays={upcomingBooking?.stays ?? 0} bookings={upcomingBooking?.bookings ?? 0} />
    </section>
  );
}

type SkeletonProps = {
  t: (key: string) => string;
  country: string;
  language: string;
};

export function UpcomingBookingsSkeleton({ t, country, language }: SkeletonProps) {
  return (
    <section className={containerStyle} data-testid={'UpcomingBookings-Skeleton'}>
      <div className="flex flex-col md:flex-row md:justify-between md:items-center mb-4">
        <h2 className={h2Style}>{t('homepage.home.innbusinessPay.upcomingBookings.heading')}</h2>
        <Link
          className={linkStyle}
          href={`/${country}/${language}/business-booker/account/dashboard.html`}
          prefetch={false}
        >
          {t('homepage.home.innbusinessPay.upcomingBookings.viewAllBookings.link')}
        </Link>
      </div>
      <div className={bookingsWrapper}>
        <BookingCardSkeleton />
        <div className={countersStyle}>
          <div className={counterStyle}>
            <h3 className={counterTitleStyle}>
              {t('homepage.home.innbusinessPay.upcomingBookings.myStays')}
            </h3>
            <div className={iconWrapperStyle}>
              <Skeleton className="h-[32px] w-[32px]" />
              <Skeleton className="h-[40px] w-[40px]" />
            </div>
          </div>
          <div className={counterStyle}>
            <h3 className={counterTitleStyle}>
              {t('homepage.home.innbusinessPay.upcomingBookings.bookings')}
            </h3>
            <div className={iconWrapperStyle}>
              <Skeleton className="h-[32px] w-[32px]" />
              <Skeleton className="h-[40px] w-[40px]" />
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}

const containerStyle = 'mt-12';
const h2Style = 'text-xl leading-[1.5rem] font-bold';
const bookingsWrapper = 'flex flex-col md:flex-row gap-4';
const countersStyle = 'flex flex-row md:flex-col w-full gap-4 md:w-1/3';
const counterStyle = 'flex-1 border-[1px] border-lightGrey3 rounded-lg p-4 flex flex-col';
const linkStyle = 'text-secondaryColor text-md underline';
const counterTitleStyle = 'font-semibold text-secondaryColor';
const iconStyle = '';
const iconWrapperStyle = 'flex flex-row gap-4';
const counterNoStyle = 'text-4xl font-semibold text-secondaryColor';
const singleCounterStyle = 'items-left justify-center md:items-center';
