import { Text } from '@chakra-ui/react';
import { useCustomLocale, useLocalStorage } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useEffect, useState } from 'react';

import {
  DISTANCE_FROM_SEARCH_INITIAL_VALUE,
  SEARCH_REFERRER_INITIAL_VALUE,
} from '../../search/constants';

export default function HotelLocation() {
  const [distanceFromSRPSearch] = useLocalStorage(
    'DistanceFromSearch',
    DISTANCE_FROM_SEARCH_INITIAL_VALUE
  );
  const [searchReferrer] = useLocalStorage('SearchReferrer', SEARCH_REFERRER_INITIAL_VALUE);

  const { language } = useCustomLocale();
  const distanceFromSearch = distanceFromSRPSearch?.data?.distance;
  const referrer = searchReferrer?.data?.referrer;

  const { t } = useTranslation(['common']);
  const [isMounted, setIsMounted] = useState(false);

  useEffect(() => {
    setIsMounted(true);
  }, []);

  if (
    !isMounted ||
    !distanceFromSearch ||
    Number.isNaN(Number(distanceFromSearch)) ||
    !referrer?.includes('search.html')
  ) {
    return null;
  }

  return (
    <Text data-testid="hdp_hotelDistanceFromSearch">
      <strong>
        {Math.round(Number(distanceFromSearch) * 10) / 10} {language === 'en' ? 'miles' : 'km'}
      </strong>
      {` ${t('searchresults.list.hotel.fromSearch')}`}
    </Text>
  );
}
