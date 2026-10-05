import { QueryClient } from '@tanstack/react-query';
import type { HotelBrandType } from '@whitbread-eos/api';
import { Channel } from '@whitbread-eos/api';
import { useCustomLocale, useHotelBrands } from '@whitbread-eos/utils';
import { useRouter } from 'next/router';

import HeaderVariantStep from './HeaderVariantStep.component';

interface Props {
  headerInfoData: any;
  bb?: boolean;
  queryClient: QueryClient;
}

export default function HeaderVariantStepContainer({
  headerInfoData,
  bb = false,
  queryClient,
}: Readonly<Props>) {
  const { query } = useRouter() || { query: {}, route: '' };
  const { language, country } = useCustomLocale();
  const basketReference = query?.reservationId ? String(query.reservationId) : '';

  const { brand, stepProgress } = useHotelBrands({
    basketReference,
    channel: bb ? Channel.Bb : Channel.Pi,
    queryClient,
  });

  return (
    <HeaderVariantStep
      currentLang={language}
      currentCountry={country}
      headerInfoData={headerInfoData}
      propsStepProgress={stepProgress}
      hotelBrand={brand as HotelBrandType}
      bb={bb}
    />
  );
}
