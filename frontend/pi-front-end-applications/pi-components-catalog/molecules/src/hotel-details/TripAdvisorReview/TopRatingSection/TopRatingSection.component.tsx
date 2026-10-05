import { Box, Image, Text, BoxProps, Center, Flex } from '@chakra-ui/react';
import { Awards, TripAdvisorReviews, tripadvisorAwardtype } from '@whitbread-eos/api';
import { ModalVariants } from '@whitbread-eos/atoms';
import {
  formatInnerHTMLAssetUrls,
  renderSanitizedHtml,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useState } from 'react';

interface Props {
  readonly tripAdvisorData: TripAdvisorReviews | Record<string, never> | null | undefined;
}

interface HIAwardInfo {
  title: string;
  description: string;
  image: string;
}

export default function TopRatingSection({ tripAdvisorData }: Props) {
  const { t } = useTranslation();
  const getTypographyProps = useSemanticTypography();
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const defaultAwardModalInfo: HIAwardInfo = {
    description: '',
    title: '',
    image: '',
  };
  const [awardModalData, setAwardModalData] = useState<HIAwardInfo>(defaultAwardModalInfo);

  if (!tripAdvisorData || !Object.keys(tripAdvisorData).length) {
    return null;
  }

  function toggleModal() {
    setIsModalOpen(!isModalOpen);
  }

  function awardModalClickHandler(award: Awards) {
    const selectedAwardType =
      award.awardType?.toLowerCase() === 'travelers choice'
        ? tripadvisorAwardtype.CHOICE
        : tripadvisorAwardtype.BEST;

    setAwardModalData({
      description: formatInnerHTMLAssetUrls(t(`tripadvisor.award.${selectedAwardType}.text`)),
      title: t('tripadvisor.award.title')
        .replace('{award_name}', award.awardType ?? '')
        .replace('{award_year}', award.year ?? ''),
      image: award.image ?? '',
    });

    toggleModal();
  }

  const scrollToUserReviewSection = () => {
    const element = document.getElementById('tripadvisor-user-review-section');
    if (element) {
      element.scrollIntoView({
        block: 'start',
        behavior: 'smooth',
      });
    }
  };

  return (
    <Box data-testid="tripadvisor-top-rating-section-hdp" display="flex">
      {tripAdvisorData?.rating && tripAdvisorData?.numberOfReviews ? (
        <Flex mb="sm">
          <Image
            {...taImageStyles}
            src={`https://static.tacdn.com/img2/ratings/traveler/${(
              Math.round(tripAdvisorData.rating * 2) / 2
            ).toFixed(1)}.svg`}
            alt="ta-ratings-img"
          />
          <Text
            as="u"
            {...reviewsLayoutStyles}
            {...getTypographyProps(reviewsLegacyTypography, reviewsSemanticTypography)}
            onClick={scrollToUserReviewSection}
          >{`(${tripAdvisorData.numberOfReviews} ${t('hoteldetails.tripadvisorreviews')})`}</Text>
        </Flex>
      ) : (
        <></>
      )}
      {tripAdvisorData?.awards?.slice(0, 1).map((award: Awards) => {
        return (
          <Image
            src={award?.image}
            alt={award?.awardType}
            onClick={() => awardModalClickHandler(award)}
            maxWidth="32px"
            mt="-3px"
            {...cursorStyle}
            ml={3}
            key={award?.awardType?.toLowerCase()}
            data-testid="tripadvisor-top-rating-award-button"
          />
        );
      })}

      <ModalVariants onClose={toggleModal} isOpen={isModalOpen} variant="info">
        <Box {...modalStyles}>
          <Box maxW="full" {...containerStyles}>
            <Center>
              <Image
                src={awardModalData.image}
                {...modalAwardIcon}
                data-testid="tripadvisor-modal-award-icon"
              />
            </Center>
            <Text mt="15px" mb="15px">
              {awardModalData.title}
            </Text>
            <Box mt={3}>{renderSanitizedHtml(awardModalData.description)}</Box>
          </Box>
        </Box>
      </ModalVariants>
    </Box>
  );
}

const modalStyles = {
  w: { md: '35.125rem' },
  px: 'md',
  pb: 'lg',
} as BoxProps;

const containerStyles = {
  mb: { base: 'lg', sm: '2xl' },
} as BoxProps;

const cursorStyle = {
  cursor: 'pointer',
};

const reviewsLayoutStyles = {
  ...cursorStyle,
};

const reviewsLegacyTypography = {
  fontSize: 'sm',
  lineHeight: 'var(--chakra-sizes-6)',
};

const reviewsSemanticTypography = {
  textStyle: 'link-s-regular',
};

const modalAwardIcon = {
  w: '200px',
  h: '230px',
};

const taImageStyles = {
  h: '20px',
  ml: '-7px',
  mt: '3px',
};
