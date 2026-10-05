import { Box, BoxProps, Flex, FlexProps, Image, Link, Text } from '@chakra-ui/react';
import { HotelInformationOptional } from '@whitbread-eos/api';
import { Button, Card, ChevronRight, Icon } from '@whitbread-eos/atoms';
import { SRHotelDistance, SRHotelThumbnail, SRHotelTitle } from '@whitbread-eos/molecules';
import { formatAssetsUrl, formatDataTestId, useCustomLocale } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

export interface Props {
  data: HotelInformationOptional;
  hideHotelDistance: boolean;
}

export default function MapHotelCard({ data, hideHotelDistance }: Readonly<Props>) {
  const {
    links,
    brand,
    name,
    topSectionImages,
    hotelFacilities,
    tripAdvisorReviews,
    distanceFromReference,
  } = data;

  const hotelSlug = links?.detailsPage ?? '';
  const hotelBrand = brand ?? '';
  const hotelName = name ?? '';

  const imageAltText = `${hotelName} ${topSectionImages?.[0]?.tags?.[0] ?? 'exterior'}`;
  const { t } = useTranslation();
  const imageURLHotelExterior = `${
    topSectionImages?.[0]?.imageSrc ?? t('dlp.hotelCard.img.placeholder')
  }`;

  const hotelBrandLogos = {
    hubLogo: t('dlp.hotelCard.hubBadge'),
    zipLogo: t('dlp.hotelCard.zipBadge'),
  };
  const hotelDistanceLabels = {
    distanceUnitPlural: t('dlp.hotelCard.distance.unitPlural'),
    fromLocation: t('dlp.hotelCard.distance.fromLocation'),
  };

  const { language, country } = useCustomLocale();
  const baseDataTestId = 'DLP';

  const URLToRedirect = `/${country}/${language}/hotels${hotelSlug}.html`;
  const firstFacilityDisplayed = hotelFacilities?.[0];

  return (
    <Box {...wrapperStyles}>
      <Link href={URLToRedirect} {...linkStyles}>
        <Card {...cardContainerStyles} data-testid={formatDataTestId(baseDataTestId, 'hotel-card')}>
          <Flex direction="row" w="full">
            <Flex direction="column">
              <SRHotelThumbnail
                imageData={{
                  imageSrc: imageURLHotelExterior,
                  imageAlt: imageAltText,
                }}
                brand={hotelBrand}
                brandLogos={hotelBrandLogos}
                testId={formatDataTestId(baseDataTestId, 'hotel-thumbnail')}
                styles={hotelThumbnailWrapperStyles}
              />
              {tripAdvisorReviews?.reviews && tripAdvisorReviews?.rating && (
                <Flex {...taContainerStyles}>
                  <Link href={`${URLToRedirect}#tripadvisor-reivew-section-bottom`} target="_blank">
                    <Image
                      {...taImageStyles}
                      src={`https://static.tacdn.com/img2/ratings/traveler/${(
                        Math.round(tripAdvisorReviews.rating * 2) / 2
                      ).toFixed(1)}.svg`}
                      alt="ta-ratings-img"
                    />
                  </Link>
                </Flex>
              )}
            </Flex>
            <Flex {...hotelDetailsBoxStyles}>
              <SRHotelTitle
                title={hotelName}
                testId={formatDataTestId(baseDataTestId, 'hotel-title')}
                styles={titleStyles}
              />
              {distanceFromReference && !hideHotelDistance && (
                <SRHotelDistance
                  distance={parseFloat(distanceFromReference.toFixed(2))}
                  unit="miles"
                  labels={hotelDistanceLabels}
                  testId={formatDataTestId(baseDataTestId, 'hotel-distance')}
                  styles={hotelDistanceTextStyles}
                />
              )}
              {firstFacilityDisplayed && (
                <Flex direction="row" gap="1">
                  <Box
                    as="span"
                    width="16px"
                    data-testid={formatDataTestId(baseDataTestId, 'hotel-facility')}
                  >
                    <Icon
                      data-testid={formatDataTestId(baseDataTestId, 'hotel-facility-icon')}
                      src={formatAssetsUrl(firstFacilityDisplayed?.icon ?? '')}
                    />
                  </Box>
                  <Text {...hotelFacilityTextStyles}>{firstFacilityDisplayed?.name}</Text>
                </Flex>
              )}
              <Flex
                {...hotelButtonBoxStyles}
                data-testid={formatDataTestId(baseDataTestId, 'hotel-button')}
              >
                <Button size="xxs" variant="circle" {...buttonStyles}>
                  <Icon svg={<ChevronRight color="var(--chakra-colors-baseWhite)" />} />
                </Button>
              </Flex>
            </Flex>
          </Flex>
        </Card>
      </Link>
    </Box>
  );
}

const wrapperStyles = {
  w: { mobile: '17.9rem', sm: '18.75rem' },
  h: '10.625rem',
};

const linkStyles = {
  display: 'flex',
  h: '100%',
  cursor: 'default',
  _hover: {
    textDecoration: 'none',
  },
  _focus: {
    textDecoration: 'none',
    boxShadow: 'none',
  },
  _visited: {
    textDecoration: 'none',
  },
  _link: {
    textDecoration: 'none',
  },
  _active: {
    textDecoration: 'none',
  },
};

const cardContainerStyles: BoxProps = {
  bgColor: 'baseWhite',
  w: 'full',
  border: '1px solid var(--chakra-colors-primary)',
  borderRadius: 'var(--chakra-space-radiusSmall)',
  boxShadow: 'none',
  padding: '0',
  _hover: {
    cursor: 'pointer',
    boxShadow: '0 0 var(--chakra-space-sm) 0 var(--chakra-colors-lightGrey2)',
    transitionProperty: 'border, box-shadow',
    transitionDuration: '0s, 0s',
    transitionTimingFunction: 'ease, ease',
    transitionDelay: '0s, 0s',
  },
};

const titleStyles = {
  fontSize: 'sm',
  fontWeight: 'bold',
  color: 'darkGrey1',
};

const hotelThumbnailWrapperStyles = {
  h: '120px',
  w: '125px',
};

const hotelDetailsBoxStyles = {
  p: 'xmd',
  flexDirection: 'column',
  // h: '10.625rem',
  h: '11.438rem',
} as FlexProps;

const hotelDistanceTextStyles = {
  fontSize: 'var(--chakra-space-xmd)',
  fontWeight: 'normal',
  paddingTop: 'xs',
  paddingBottom: 'sm',
  flex: 0,
};

const hotelFacilityTextStyles = {
  alignSelf: 'center',
  fontSize: 'var(--chakra-space-xmd)',
  fontWeight: 'normal',
};

const hotelButtonBoxStyles = {
  pl: {
    mobile: 'sm',
    md: '0',
  },
  pr: {
    mobile: '0',
  },
  alignSelf: 'end',
  m: 'auto 0 var(--chakra-space-xmd) auto',
};

const buttonStyles = {
  bgColor: 'primary',
  _hover: {
    bg: 'var(--chakra-colors-btnPrimaryHoverBg) radial-gradient(circle, transparent 1%, var(--chakra-colors-btnPrimaryHoverBg) 1%) center/15000%',
    boxShadow: '0 4px 8px var(--chakra-colors-lightGrey1)',
    _disabled: {
      bg: 'lightGrey3',
    },
  },
};

const taImageStyles = {
  h: '20px',
};

const taContainerStyles = {
  py: 'md',
};
