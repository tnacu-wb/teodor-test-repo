import type {
  QueryHotelInformationArgs,
  PackagesCriteria,
  PromotionQueryInput,
  StaticContentQueryInput,
} from '@whitbread-eos/api';
import { FT_PI_REDIS_RQ_CACHE, PageName, UnleashChannel } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import {
  getI18nLabels,
  getServerSideCustomLocale,
  getUnleashToggles,
  useFeatureToggle,
  GLOBALS,
  validateBasketIdFromServer,
  BASKET_IDS_COOKIE,
  PAGE_UNAVAILABLE_URL_EN,
  PAGE_UNAVAILABLE_URL_DE,
} from '@whitbread-eos/utils';
import Cookies from 'cookies';
import type { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import { ReactElement } from 'react';

import { ConfirmationLayout } from '~components';
import ConfirmationPagePi, { createConfirmationPiDataLoaderFn } from '~page-helper/confirmation';
import { PAGE } from '~utils/pi-all-pages-constants';
import { clearServerQueryClient, createServerQueryClient } from '~utils/serverQueryClient';

interface Props {
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  staticContentQueryInput: StaticContentQueryInput;
  basketReference: string | null;
  promotionQueryInput: PromotionQueryInput;
  featureToggles: DynamicObject;
}
interface DynamicObject {
  [key: string]: boolean;
}
export default function ConfirmationPage({
  pcksQueryInput,
  hiQueryInput,
  staticContentQueryInput,
  basketReference,
  promotionQueryInput,
  featureToggles,
}: Readonly<Props>) {
  const router = useRouter();
  useFeatureToggle(featureToggles);

  return (
    <ConfirmationPagePi
      router={router}
      pcksQueryInput={pcksQueryInput}
      hiQueryInput={hiQueryInput}
      staticContentQueryInput={staticContentQueryInput}
      basketReference={basketReference}
      promotionQueryInput={promotionQueryInput}
    />
  );
}

ConfirmationPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <ConfirmationLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </ConfirmationLayout>
  );
};

export async function getServerSideProps({ locale = 'gb', ...props }: GetServerSidePropsContext) {
  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles: DynamicObject = await getUnleashToggles(
    props,
    PAGE.CONFIRMATION.featureToggles.appPage,
    PAGE.CONFIRMATION.featureToggles.flagsWithFallback,
    {
      country: country || GLOBALS.locale.GB,
      channel: UnleashChannel.PI,
      pageName: PageName.CONFIRMATION.toUpperCase(),
    }
  );

  const queryClient = createServerQueryClient({
    page: PAGE.CONFIRMATION.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });
  try {
    // Validate basket ID authorization (server-side)
    const basketReference = props.query?.reservationId ? String(props.query.reservationId) : '';
    const cookies = new Cookies(props.req, props.res);
    const basketIdsCookie = cookies.get(BASKET_IDS_COOKIE);

    // Only perform authorization check if feature toggle is enabled
    if (featureToggles.release_basket_ids_cookie_validation) {
      const isAuthorized = validateBasketIdFromServer(basketReference, basketIdsCookie);

      if (!isAuthorized) {
        const pageNotAvailableUrl =
          language === GLOBALS.language.DE ? PAGE_UNAVAILABLE_URL_DE : PAGE_UNAVAILABLE_URL_EN;
        return {
          redirect: {
            destination: `/${country}/${language}/${pageNotAvailableUrl}`,
            permanent: false,
          },
        };
      }
    }

    const labels = await getI18nLabels({
      language,
      queryClient,
    });

    const loadedData = await createConfirmationPiDataLoaderFn({
      queryClient: queryClient,
      language: language,
      country: country,
      ...props,
    });

    return {
      props: {
        featureToggles,
        ...loadedData,
        ...labels,
      },
    };
  } finally {
    clearServerQueryClient(queryClient);
  }
}
