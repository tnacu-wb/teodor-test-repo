import type { FlexProps, TextProps } from '@chakra-ui/react';
import { Flex, Text } from '@chakra-ui/react';
import { Icon, Location24 } from '@whitbread-eos/atoms';
import { formatAssetsUrl, useCustomLocale } from '@whitbread-eos/utils';

import HotelParking from '../../../account/dashboard/BookingInfoCard/HotelParking';
import HotelThumbnail from '../../../account/dashboard/BookingInfoCard/HotelThumbnail';

export interface HotelDetails {
  hotelInformation: {
    brand: string;
    links: {
      detailsPage: string;
    };
    address: string;
    parkingDescription: string;
    galleryImages: {
      alt: string;
      thumbnailSrc: string;
    }[];
  };
}

export interface HotelDetailsProps {
  data: HotelDetails;
  isLoading: boolean;
  isError: boolean;
  error: any;
  t: (id: string) => string;
}

export default function BartHotelDetailsComponent({
  data,
  isLoading,
  isError,
  error,
  t,
}: Readonly<HotelDetailsProps>) {
  const parkingInfo = data?.hotelInformation?.parkingDescription;
  const hdpUrl = data?.hotelInformation?.links?.detailsPage;
  const galleryImages = data?.hotelInformation?.galleryImages;
  const hotelImage = galleryImages && formatAssetsUrl(galleryImages[0]?.thumbnailSrc);
  const brand = data?.hotelInformation?.brand;
  const altText = galleryImages?.[0]?.alt;
  const address = data?.hotelInformation?.address;
  const { country, language } = useCustomLocale();

  if (isLoading) {
    return <Text data-testid="loading-message">{t('searchresults.list.hotel.loading')}</Text>;
  }

  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }

  return (
    <Flex {...hotelDetailsWrapperStyle}>
      {hotelImage && (
        <HotelThumbnail
          image={`${hotelImage}`}
          alt={altText}
          url={`/${country}/${language}/hotels${hdpUrl}.html`}
          brand={brand}
        />
      )}

      <Flex mt="xmd">
        <Icon svg={<Location24 />} />
        <Text {...addressStyle}>{address}</Text>
      </Flex>
      <HotelParking content={parkingInfo} />
    </Flex>
  );
}

const addressStyle = {
  fontSize: 'sm',
  fontWeight: 'medium',
  lineHeight: '2',
  color: 'darkGrey2',
  ml: 'sm',
} as TextProps;

const hotelDetailsWrapperStyle = {
  flexDirection: 'column',
  ml: { mobile: 0, lg: '5xl' },
  w: { mobile: 'full', lg: '21rem' },
  whiteSpace: 'pre-wrap',
} as FlexProps;
