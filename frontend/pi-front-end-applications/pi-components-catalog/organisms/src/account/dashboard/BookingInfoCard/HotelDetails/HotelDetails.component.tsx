import type { FlexProps, TextProps } from '@chakra-ui/react';
import { Flex, Text } from '@chakra-ui/react';
import { Icon, Location24 } from '@whitbread-eos/atoms';
import { HotelParking, HotelThumbnail } from '@whitbread-eos/molecules';
import { formatAssetsUrl, formatDataTestId, useCustomLocale } from '@whitbread-eos/utils';

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
}

export default function HotelDetailsComponent({ data }: Readonly<HotelDetailsProps>) {
  const parkingInfo = data?.hotelInformation?.parkingDescription;
  const hdpUrl = data?.hotelInformation?.links?.detailsPage;
  const galleryImages = data?.hotelInformation?.galleryImages;
  const hotelImage = galleryImages && formatAssetsUrl(galleryImages[0]?.thumbnailSrc);
  const brand = data?.hotelInformation?.brand;
  const altText = galleryImages?.[0]?.alt;
  const address = data?.hotelInformation?.address;
  const baseDataTestId = 'HotelDetails';
  const { country, language } = useCustomLocale();
  return (
    <Flex {...hotelDetailsWrapperStyle} data-testid={formatDataTestId(baseDataTestId, 'Container')}>
      {hotelImage && (
        <HotelThumbnail
          image={`${hotelImage}`}
          alt={altText}
          url={`/${country}/${language}/hotels${hdpUrl}.html`}
          brand={brand}
          imageWrapperStyles={{
            height: 'auto',
            aspectRatio: '16 / 9',
          }}
        />
      )}
      <Flex mt="xmd">
        <Icon svg={<Location24 />} />
        <Text {...addressStyle} data-testid={formatDataTestId(baseDataTestId, 'Address')}>
          {address}
        </Text>
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
  w: { mobile: 'full', lg: '21rem' },
  whiteSpace: 'pre-wrap',
} as FlexProps;
