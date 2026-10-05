import { Text } from '@chakra-ui/react';
import { FT_PI_BB_CCUI_TRIP_ADVISOR } from '@whitbread-eos/api';
import { useFeatureToggle, useStaticHotelInformation } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

import BottomReviewSection from './BottomReviewSection';
import TopRatingSection from './TopRatingSection';

interface Props {
  readonly isTopSection?: boolean;
}

export const TripAdvisorReview = ({ isTopSection = true }: Readonly<Props>) => {
  const { tripAdvisorReviews, isLoading, isError, error, brand, title } =
    useStaticHotelInformation();
  const { t } = useTranslation(['common']);
  const { [FT_PI_BB_CCUI_TRIP_ADVISOR]: isTripAdvisorReviewEnabled } = useFeatureToggle();

  if (isLoading) {
    return <Text>{t('searchresults.list.hotel.loading')}</Text>;
  }

  if (isError) {
    return <Text>{(error as Error)?.message}</Text>;
  }

  return isTripAdvisorReviewEnabled ? (
    <>
      {isTopSection ? (
        <TopRatingSection tripAdvisorData={tripAdvisorReviews} />
      ) : (
        <BottomReviewSection tripAdvisorData={tripAdvisorReviews} brand={brand} title={title} />
      )}
    </>
  ) : null;
};
