import { Box, BoxProps, Flex, FlexProps, Image, Link, Text } from '@chakra-ui/react';
import { HotelInformationOptional } from '@whitbread-eos/api';
import { Card, Button, ChevronRight, Icon } from '@whitbread-eos/atoms';
import {
  SRHotelBadges,
  SRHotelDistance,
  SRHotelFacilities,
  SRHotelThumbnail,
  SRHotelTitle,
} from '@whitbread-eos/molecules';
import { formatDataTestId, useCustomLocale, useSemanticTypography } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

export interface Props {
  data: HotelInformationOptional;
  hideHotelDistance: boolean;
}

export default function HotelCard({ data, hideHotelDistance }: Readonly<Props>) {
  const baseDataTestId = 'DLP';
  const getTypographyProps = useSemanticTypography();

  const { language, country } = useCustomLocale();
  const { t } = useTranslation();
  const hotelSlug = data?.links?.detailsPage ?? '';
  const hotelBrand = data?.brand ?? '';
  const hotelName = data?.name ?? '';
  const imageURLHotelExterior = `${
    data?.topSectionImages?.[0]?.imageSrc ?? t('dlp.hotelCard.img.placeholder')
  }`;

  const imageAltText = `${hotelName} ${data?.topSectionImages?.[0]?.tags?.[0] ?? 'exterior'}`;

  const hotelBrandLogos = {
    hubLogo: t('dlp.hotelCard.hubBadge'),
    zipLogo: t('dlp.hotelCard.zipBadge'),
  };
  const hotelDistanceLabels = {
    distanceUnitPlural: t('dlp.hotelCard.distance.unitPlural'),
    fromLocation: t('dlp.hotelCard.distance.fromLocation'),
  };

  const URLToRedirect = `/${country}/${language}/hotels${hotelSlug}.html`;

  return (
    <Box {...wrapperStyles}>
      <Link href={URLToRedirect} {...linkStyles}>
        <Card {...cardContainerStyles} data-testid={formatDataTestId(baseDataTestId, 'hotel-card')}>
          <Flex direction="column" w="full">
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
            <Flex {...hotelDetailsBoxStyles}>
              <SRHotelTitle
                title={hotelName}
                testId={formatDataTestId(baseDataTestId, 'hotel-title')}
                styles={{
                  ...titleStylesLayout,
                  ...getTypographyProps(titleLegacyTypography, titleSemanticTypography),
                }}
              />
              {!hideHotelDistance && data?.distanceFromReference && (
                <SRHotelDistance
                  distance={parseFloat(data?.distanceFromReference.toFixed(2))}
                  unit="miles"
                  labels={hotelDistanceLabels}
                  testId={formatDataTestId(baseDataTestId, 'hotel-distance')}
                  styles={{
                    ...hotelDistanceLayoutStyles,
                    ...getTypographyProps(
                      hotelDistanceLegacyTypography,
                      hotelDistanceSemanticTypography
                    ),
                  }}
                />
              )}
              <SRHotelBadges
                testId={formatDataTestId(baseDataTestId, 'hotel-badges')}
                messagingFlag={data?.messagingFlag}
                hotelFacilities={data?.hotelFacilities}
                labels={{
                  premierPlus: t('hoteldetails.rates.premierplus'),
                }}
                styles={hotelBadgesStyles}
              />{' '}
              {data?.tripAdvisorReviews?.reviews && data?.tripAdvisorReviews?.rating && (
                <Flex mb="sm">
                  <Image
                    {...taImageStyles}
                    src={`https://static.tacdn.com/img2/ratings/traveler/${(
                      Math.round(data.tripAdvisorReviews.rating * 2) / 2
                    ).toFixed(1)}.svg`}
                    alt="ta-ratings-img"
                  />
                  <Link
                    href={`${URLToRedirect}#tripadvisor-reivew-section-bottom`}
                    target="_blank"
                    {...taReviewsStyles}
                  >{`${data.tripAdvisorReviews.numberOfReviews} ${t(
                    'hoteldetails.tripadvisorreviews'
                  )}`}</Link>
                </Flex>
              )}
              {data?.hotelFacilities && (
                <SRHotelFacilities facilities={data?.hotelFacilities} roomTypes={[]} isDLPPage />
              )}
            </Flex>
            <Flex
              justifyContent="space-between"
              gap="md"
              {...hotelButtonBoxStyles}
              data-testid={formatDataTestId(baseDataTestId, 'hotel-button')}
            >
              <Text fontWeight="bold" fontSize="sm" lineHeight="3">
                {t('dlp.hotelCard.viewHotel')}
              </Text>
              <Button
                aria-label={t('dlp.hotelCard.viewHotel')}
                size="xxs"
                variant="circle"
                {...buttonStyles}
              >
                <Icon svg={<ChevronRight color="var(--chakra-colors-baseWhite)" />} />
              </Button>
            </Flex>
          </Flex>
        </Card>
      </Link>
    </Box>
  );
}

const wrapperStyles = {
  w: {
    mobile: 'full',
    xs: '22.375rem',
    sm: '16.25rem',
    md: '14.313rem',
    lg: '18.375rem',
    xl: '19.312rem',
  },
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
  minH: { mobile: 'auto', sm: '27.25rem', xl: '25.875rem' },
  border: '1px solid var(--chakra-colors-lightGrey4)',
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

const titleStylesLayout = {
  color: 'darkGrey1',
};

const titleLegacyTypography = {
  fontSize: 'md',
  lineHeight: 'var(--chakra-lineHeights-3)',
  fontWeight: 'semibold',
};

const titleSemanticTypography = {
  textStyle: 'body-m-emphasis',
};

const hotelThumbnailWrapperStyles = {
  minH: '12.875rem',
};

const hotelDetailsBoxStyles = {
  px: {
    mobile: 'sm',
    sm: 'md',
  },
  py: {
    mobile: 'sm',
    sm: 'md',
  },
  flexDirection: 'column',
  w: 'full',
  minH: { mobile: 'auto', sm: '11.5rem', xl: '10.4rem' },
} as FlexProps;

const hotelDistanceLayoutStyles = {
  paddingTop: {
    mobile: 'sm',
    sm: 'xs',
  },
  paddingBottom: 'sm',
  flex: 0,
};

const hotelDistanceLegacyTypography = {
  fontSize: 'sm',
  fontWeight: 'normal',
  lineHeight: '2',
};

const hotelDistanceSemanticTypography = {
  textStyle: 'body-s-regular',
};

const hotelBadgesStyles = {
  mb: 'xmd',
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
  m: 'auto var(--chakra-space-md) var(--chakra-space-md)',
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
  h: '18px',
  ml: '-7px',
};

const taReviewsStyles = {
  fontSize: 'xxs',
};
