import { VisuallyHidden } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import type { HotelBrandType, Language } from '@whitbread-eos/api';
import { GET_STATIC_CONTENT, SITE_LEISURE } from '@whitbread-eos/api';
import { LanguageOptions } from '@whitbread-eos/atoms';
import { useCustomLocale, useQueryRequest } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useEffect, useState } from 'react';

import HeaderVariantDefault from './HeaderVariantDefault';
import HeaderVariantLogo from './HeaderVariantLogo';
import HeaderVariantStepContainer from './HeaderVariantStep/HeaderVariantStep.container';

export interface Props {
  variant?: 'default' | 'step' | 'logo';
  isIcon?: boolean;
  bb?: boolean;
  queryClient: QueryClient;
  useNextImage?: boolean;
}

export default function HeaderLeisure({
  variant,
  isIcon,
  queryClient,
  bb = false,
  useNextImage,
}: Readonly<Props>) {
  const { language: currentLanguage, country: currentCountry } = useCustomLocale();
  const { t } = useTranslation();
  const { data, isLoading } = useQueryRequest(
    ['GetStaticContent', currentLanguage, currentCountry],
    GET_STATIC_CONTENT,
    {
      language: currentLanguage as Language,
      country: currentCountry,
      site: SITE_LEISURE,
      businessBooker: false,
    }
  );

  const [headerInfoData, setHeaderInfoData] = useState<LanguageOptions[]>([]);

  useEffect(() => {
    if (data?.headerInformation?.content?.countries?.length) {
      setHeaderInfoData(data?.headerInformation);
    }
  }, [data?.headerInformation]);

  if (isLoading) {
    return <VisuallyHidden>{t('searchresults.list.hotel.loading')}</VisuallyHidden>;
  }

  if (currentLanguage === 'de') {
    return renderHeader(
      headerInfoData,
      variant,
      bb,
      queryClient,
      isIcon,
      'pi-simple',
      useNextImage
    );
  }

  return renderHeader(headerInfoData, variant, bb, queryClient, isIcon, undefined, useNextImage);
}

function renderHeader(
  headerInfoData: any,
  variant: string | undefined,
  bb: boolean,
  queryClient: QueryClient,
  isIcon = false,
  hotelBrand = 'pi' as HotelBrandType,
  useNextImage?: boolean
) {
  switch (variant) {
    case 'default': {
      return (
        <HeaderVariantDefault
          headerInfoData={headerInfoData}
          hotelBrand={hotelBrand}
          useNextImage={useNextImage}
        />
      );
    }
    case 'step': {
      return (
        <HeaderVariantStepContainer
          headerInfoData={headerInfoData}
          bb={bb}
          queryClient={queryClient}
        />
      );
    }
    case 'logo': {
      return (
        <HeaderVariantLogo
          headerInfoData={headerInfoData}
          hotelBrand={hotelBrand}
          isIcon={isIcon}
          bb={bb}
        />
      );
    }
    default: {
      return <HeaderVariantDefault headerInfoData={headerInfoData} hotelBrand={hotelBrand} />;
    }
  }
}
