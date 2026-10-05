import { VisuallyHidden } from '@chakra-ui/react';
import { GET_STATIC_CONTENT, LEISURE_BRANDS, SITE, SITE_LEISURE } from '@whitbread-eos/api';
import { Alert, Notification } from '@whitbread-eos/atoms';
import { useCustomLocale, useQueryRequest } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import dynamic from 'next/dynamic';
import { useRouter } from 'next/router';
import { useMemo } from 'react';

interface Props {
  isPremierInn?: boolean;
  site?: SITE;
}

const FooterComponent = dynamic(() => import('./Footer.component'), {
  ssr: false,
});

export default function Footer({ isPremierInn, site = SITE_LEISURE }: Readonly<Props>) {
  const { query } = useRouter() || { query: {} };
  const { language, country } = useCustomLocale();
  const { t } = useTranslation();
  const baseDataTestId = 'Footer';
  const brand = query?.BRAND ? String(query.BRAND).toLowerCase() : 'pi';

  const isLeisureBrand = useMemo(() => {
    return LEISURE_BRANDS.includes(brand);
  }, [brand]);

  const { isLoading, isError, data, error } = useQueryRequest(
    ['GetStaticContent', language, country],
    GET_STATIC_CONTENT,
    {
      country,
      language,
      site: site,
      businessBooker: false,
    }
  );

  if (isLoading) {
    return (
      <VisuallyHidden data-testid="loading-message">
        {t('searchresults.list.hotel.loading')}
      </VisuallyHidden>
    );
  }

  if (isError) {
    return (
      <Notification
        status="error"
        description={String(error)}
        variant="alert"
        maxW="full"
        svg={<Alert />}
      />
    );
  }
  let footerData = [data?.footer?.tabs[0]];
  if (isPremierInn && isLeisureBrand) {
    footerData = data?.footer?.tabs;
  }

  return (
    <FooterComponent
      baseDataTestId={baseDataTestId}
      language={language || 'en'}
      copyrightData={data?.footer?.copyrightInfo}
      socialIcons={data?.footer?.socialMediaIcons}
      tabsInfo={footerData}
      isPI={isPremierInn ? isLeisureBrand : false}
    />
  );
}
