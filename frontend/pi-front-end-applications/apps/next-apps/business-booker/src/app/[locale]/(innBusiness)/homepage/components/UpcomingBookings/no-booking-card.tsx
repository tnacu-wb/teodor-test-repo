import { formatIBAssetsUrl } from '@whitbread-eos/utils/server';
import Image from 'next/image';

type Props = {
  baseDataTestId: string;
  t: (key: string) => string;
  searchIcon: string;
};

export function NoBookingCard({ searchIcon, t, baseDataTestId }: Props) {
  return (
    <div className={noBookingStyle} data-testid={`${baseDataTestId}-NoBookingCard`}>
      <Image
        alt={'Search icon'}
        src={formatIBAssetsUrl(searchIcon)}
        width={27}
        height={27}
        data-testid={`${baseDataTestId}-Search-icon`}
      />
      <h2 className={noBookingsTitle} data-testid={`${baseDataTestId}-NoBookingTitle`}>
        {t('homepage.home.innbusinessPay.upcomingBookings.noBookings')}
      </h2>
      <p className={noBookingsSubTitle} data-testid={`${baseDataTestId}-NoBookingSubTitle`}>
        {t('homepage.home.innbusinessPay.upcomingBookings.searchBooking')}
      </p>
    </div>
  );
}

const noBookingStyle =
  'flex flex-col w-full md:w-2/3 border-[1px] border-dashed border-lightGrey3 rounded-lg overflow-hidden p-6 md:py-12 md:px-8 justify-between gap-2 md:gap-0';
const noBookingsTitle = 'text-3xl text-secondaryColor font-black';
const noBookingsSubTitle = 'text-sm max-w-[27.125rem]';
