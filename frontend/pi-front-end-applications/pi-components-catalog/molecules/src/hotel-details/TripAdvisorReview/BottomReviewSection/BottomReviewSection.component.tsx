import {
  Box,
  BoxProps,
  Flex,
  FlexProps,
  Text,
  TextProps,
  Image,
  ImageProps,
} from '@chakra-ui/react';
import type { TripAdvisorReviews, Reviews, SubRatings } from '@whitbread-eos/api';
import { Section, Accordion } from '@whitbread-eos/atoms';
import {
  formatDate,
  useCustomLocale,
  getPIBrandText,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useState } from 'react';

import {
  TRIP_ADVISOR_RATING_IMAGE_URL_PART1,
  TRIP_ADVISOR_RATING_IMAGE_URL_PART2,
} from '../../../utils/constants';

interface Props {
  readonly tripAdvisorData: TripAdvisorReviews | Record<string, never> | null | undefined;
  brand: string;
  title: string | undefined;
}

export default function BottomReviewSection({ tripAdvisorData, brand, title }: Props) {
  const { t } = useTranslation(['common']);
  const { language } = useCustomLocale();
  const getTypographyProps = useSemanticTypography();
  const seeReviewsLabel = t('tripadvisor.show.reviews');
  const [accordionTitle, setAccordionTitle] = useState<string>(seeReviewsLabel);

  const onRedirectToTripAdvisor = (url: string | null) => {
    if (url) window.open(url, '_blank', 'noopener,noreferrer');
  };

  if (!tripAdvisorData || !Object.keys(tripAdvisorData).length) {
    return null;
  }

  const renderUserReviewSection = (reviews: Reviews[] | undefined) => {
    return reviews?.map((reviewData: Reviews) => (
      <Box marginY={3} key={`reviews-${reviewData?.publishedDate}`}>
        <Box>
          <meta content="Trip Advisor" />
        </Box>
        <Flex alignItems={'center'}>
          <span>
            <Image
              src={`${TRIP_ADVISOR_RATING_IMAGE_URL_PART1}${(
                Math.round((reviewData?.rating ?? 0) * 2) / 2
              ).toFixed(1)}${TRIP_ADVISOR_RATING_IMAGE_URL_PART2}`}
              alt={`${t('booking.hotel.summary.tripAdvisorRating')} ${(
                Math.round((reviewData?.rating ?? 0) * 2) / 2
              ).toFixed(1)}`}
            ></Image>
          </span>
          <Text marginLeft={'1'}>
            {formatDate(
              new Date(reviewData?.publishedDate ?? '').toDateString(),
              'dd MMM yyyy',
              language
            )}
          </Text>
        </Flex>
        <Box>
          <Text fontWeight={'semibold'} marginY={1}>
            {reviewData?.user?.username}
          </Text>
          <Text marginY={1}>{reviewData?.user?.location}</Text>
          <Flex marginY={1}>
            <Text fontWeight={'bold'}>{`${t('hoteldetails.tripadvisorreviewstriptype')}: `}</Text>
            <Text marginLeft={1}>{reviewData?.tripType}</Text>
          </Flex>
        </Box>
        <Box fontStyle={'italic'} marginY={1}>
          {`"${reviewData?.title}"`}
        </Box>
        <Box marginY={1}>{reviewData?.text}</Box>
      </Box>
    ));
  };

  return (
    <Section dataTestId="tripadvisor-bottom-review-section-hdp">
      <Box
        {...containerStyles}
        data-testid="tripadvisor-user-review-section"
        id="tripadvisor-user-review-section"
      >
        <Text
          {...headingLayoutStyles}
          {...getTypographyProps(headingLegacyTypography, headingSemanticTypography)}
          as="h3"
        >
          {t('hoteldetails.tripadvisorreviews.title')}
          {getPIBrandText(t, brand)}
          {title}
        </Text>

        <Flex {...reviewContainerFlexStyles}>
          <Accordion
            accordionOverwriteStyles={{ ...accordionOverwriteStyles }}
            bgColor="lightGrey5"
            accordionItems={[
              {
                title: accordionTitle,
                onToggleSection: () =>
                  setAccordionTitle(
                    accordionTitle === seeReviewsLabel
                      ? t('tripadvisor.hide.reviews')
                      : seeReviewsLabel
                  ),
                content: (
                  <Box>
                    {renderUserReviewSection(tripAdvisorData?.reviews)}
                    <Flex marginY={4} alignItems={'center'}>
                      <Text
                        {...redirectLinkStyles}
                        data-testid="tripadvisor-see-reviews-link"
                        onClick={() => onRedirectToTripAdvisor(tripAdvisorData?.webUrl ?? '')}
                      >
                        {t('tripadvisor.see.reviews')}
                      </Text>
                      <Text>{t('tripadvisor.see.reviews.onTripadvisor')}</Text>
                    </Flex>
                  </Box>
                ),
              },
            ]}
          />

          <Box>
            <Text
              {...getTypographyProps(
                tripAdvisorBroughtByLegacyTypography,
                tripAdvisorBroughtBySemanticTypography
              )}
            >
              {t('hoteldetails.tripadvisorreviewsbroughtby')}
            </Text>
            <Image {...logoStyle} src={t('tripadvisor.primarylogo')}></Image>
            <Box data-testid="subrating-section">
              {tripAdvisorData?.subRatings?.map((subRatingData: SubRatings) => (
                <Flex key={`sunbrating-${subRatingData?.localisedName}`}>
                  <Box minWidth={'60%'} maxWidth={'60%'}>
                    <Text {...getTypographyProps({}, subratingLabelSemanticTypography)}>
                      {subRatingData?.localisedName}
                    </Text>
                  </Box>
                  <Image src={subRatingData?.ratingImageUrl} marginRight={2}></Image>
                </Flex>
              ))}
            </Box>
            <Box>
              <Text
                {...writeReviewLinkLayoutStyles}
                {...getTypographyProps(
                  writeReviewLinkLegacyTypography,
                  writeReviewLinkSemanticTypography
                )}
                marginTop={4}
                data-testid="tripadvisor-write-review-link"
                onClick={() => onRedirectToTripAdvisor(tripAdvisorData?.writeReview ?? '')}
              >
                {t('tripadvisor.write.review')}
              </Text>
            </Box>
          </Box>
        </Flex>
      </Box>
    </Section>
  );
}

const headingLayoutStyles = {
  mb: 'md',
} as TextProps;

const headingLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: { base: 'xl', sm: '2xl' },
  lineHeight: { base: '3', sm: '4' },
} as TextProps;

const headingSemanticTypography = {
  textStyle: 'heading-m',
} as const;

const tripAdvisorBroughtByLegacyTypography = {
  fontWeight: 'bold',
  fontSize: 'sm',
} as TextProps;

const tripAdvisorBroughtBySemanticTypography = {
  textStyle: 'body-s-emphasis',
} as const;

const subratingLabelSemanticTypography = {
  textStyle: 'body-m-regular',
} as const;

const writeReviewLinkLayoutStyles = {
  display: 'inline-block',
  marginRight: '1',
  color: 'btnSecondaryEnabled',
  textDecoration: 'underline',
  _hover: {
    cursor: 'pointer',
  },
} as TextProps;

const writeReviewLinkLegacyTypography = {
  fontSize: 'md',
  lineHeight: '2',
} as TextProps;

const writeReviewLinkSemanticTypography = {
  textStyle: 'link-m-regular',
} as const;

const containerStyles = {
  mt: { base: 'lg', sm: '3xl' },
  mb: { base: 'lg', sm: '2xl' },
} as BoxProps;

const reviewContainerFlexStyles = {
  flexDirection: { base: 'column-reverse', sm: 'row' },
  justifyContent: 'space-between',
  gap: '2.5rem',
  marginRight: '2',
} as FlexProps;

const redirectLinkStyles = {
  display: 'inline-block',
  fontSize: 'md',
  lineHeight: '2',
  marginRight: '1',
  color: 'btnSecondaryEnabled',
  textDecoration: 'underline',
  _hover: {
    cursor: 'pointer',
  },
} as TextProps;

const logoStyle = {
  height: '38px',
  marginY: '3',
  objectFit: 'cover',
} as ImageProps;

const accordionOverwriteStyles = {
  container: { w: { base: 'full', sm: '50%', md: '70%' } },
  button: {
    borderWidth: 0,
    borderBottom: 0,
    borderTop: 0,
    backgroundColor: 'lightGrey5',
    _hover: {
      backgroundColor: 'lightGrey5',
    },
    _expanded: {
      borderBottomRadius: 'var(--chakra-radii-none)',
    },
    borderRadius: 'var(--chakra-radii-xl)',
    px: 'var(--chakra-space-lg)',
  },
  item: { border: 0, w: { base: 'full', lg: '53.125rem' } },
  text: {
    color: 'btnSecondaryEnabled',
    fontSize: 'lg',
    fontWeight: 'semibold',
  },
  panel: {
    padding: 'var(--chakra-space-sm) var(--chakra-space-lg)',
    borderBottomRadius: 'var(--chakra-radii-xl)',
  },
};
