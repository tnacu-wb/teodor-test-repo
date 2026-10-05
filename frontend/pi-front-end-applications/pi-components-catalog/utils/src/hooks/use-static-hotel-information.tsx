'use client';

import { STATIC_HOTEL_INFORMATION_QUERY, HotelInformationBySlug } from '@whitbread-eos/api';
import { useRouter } from 'next/router';

import { getStaticHotelInformationQueryDateDataFromUrl } from '../getters';
import useCustomLocale from './use-custom-locale';
import { useQueryRequest } from './use-request';

export default function useStaticHotelInformation() {
  const router = useRouter();

  const slug = ['/hotels', ...(router.query.slug as string[])].join('/');
  const { country, language } = useCustomLocale();
  const queryString = router.asPath?.split('?')[1] ?? '';
  const { staticHotelInformationQueryKeyDates, staticHotelInformationQueryPayloadDates } =
    getStaticHotelInformationQueryDateDataFromUrl(queryString);

  const { isLoading, isError, data, error } = useQueryRequest(
    ['staticHotelInformation', language, country, slug, ...staticHotelInformationQueryKeyDates],
    STATIC_HOTEL_INFORMATION_QUERY,
    {
      slug,
      language,
      country,
      ...staticHotelInformationQueryPayloadDates,
    }
  );

  const hotelInformationData: HotelInformationBySlug = data?.hotelInformationBySlug;

  return { isLoading, isError, ...hotelInformationData, error };
}
