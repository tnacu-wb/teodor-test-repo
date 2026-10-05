import {
  Area,
  FT_PI_AUTH0_LOGIN,
  FT_PI_REDIS_RQ_CACHE,
  PageName,
  ScreenSizeValues,
} from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import { SEO as Seo } from '@whitbread-eos/molecules';
import { BookingInfoCardWrapper } from '@whitbread-eos/organisms';
import {
  getI18nLabels,
  getServerSideCustomLocale,
  getUnleashToggles,
  useFeatureToggle,
  GLOBALS,
  getFindBookingToken,
} from '@whitbread-eos/utils';
import { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import { ReactElement, useEffect, useState } from 'react';

import { DefaultLayout } from '~components';
import { useScreenSize } from '~hooks/use-screensize';
import { createDashboardPiDataLoaderFn } from '~page-helper/dashboard';
import MyDashboardPagePi from '~page-helper/dashboard/page.pi';
import { PAGE } from '~utils/pi-all-pages-constants';
import { clearServerQueryClient, createServerQueryClient } from '~utils/serverQueryClient';

import { getAuth0TokenAndEmail } from '../../lib/getAuth0Token';

interface DynamicObject {
  [key: string]: boolean;
}
interface Props {
  featureToggles: DynamicObject;
}

export default function Dashboard({ featureToggles }: Readonly<Props>) {
  const screenSize: ScreenSizeValues = useScreenSize();
  useFeatureToggle(featureToggles);

  // Check for single booking view (bookingReference query param)
  const router = useRouter();
  const { bookingReference } = router.query;
  const ref = bookingReference ? String(bookingReference) : '';

  const [token, setToken] = useState<string | null>(null);
  const [basketReference, setBasketReference] = useState<string | null>(null);
  const [operaConfNumber, setOperaConfNumber] = useState('');

  useEffect(() => {
    if (ref) {
      const { token, basketReference, operaConfNumber } = getFindBookingToken();
      setToken(token);
      setBasketReference(basketReference);
      setOperaConfNumber(operaConfNumber);
    }
  }, [ref]);

  // Render single booking view when bookingReference query param present
  if (basketReference && ref) {
    if (!token) {
      return <div />;
    }
    return (
      <>
        <Seo page={PageName.DASHBOARD} />
        <BookingInfoCardWrapper
          area={Area.PI}
          bookingReference={ref}
          basketReference={basketReference}
          operaConfNumber={operaConfNumber}
          inputValues={{}}
        />
      </>
    );
  }

  // Normal dashboard view
  return <MyDashboardPagePi screenSize={screenSize} />;
}
Dashboard.getLayout = function getLayout(page: ReactElement) {
  return (
    <DefaultLayout showFooter={true}>
      <ErrorBoundary>{page}</ErrorBoundary>
    </DefaultLayout>
  );
};

export async function getServerSideProps({ locale = 'gb', ...props }: GetServerSidePropsContext) {
  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles: DynamicObject = await getUnleashToggles(
    props,
    PAGE.DASHBOARD.featureToogles.appPage,
    PAGE.DASHBOARD.featureToogles.flagsWithFallback,
    { country: country ?? GLOBALS.locale.GB }
  );

  const queryClient = createServerQueryClient({
    page: PAGE.DASHBOARD.featureToogles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });
  try {
    const labels = await getI18nLabels({
      language,
      queryClient,
    });

    const isAuth0Enabled = featureToggles[FT_PI_AUTH0_LOGIN] ?? false;
    let authToken: string | undefined;

    if (isAuth0Enabled) {
      const { accessToken } = await getAuth0TokenAndEmail(props.req);
      authToken = accessToken ?? undefined;
    }

    const loadedData = await createDashboardPiDataLoaderFn({
      queryClient: queryClient,
      language: language,
      country: country,
      authToken,
      ...props,
    });

    return {
      props: {
        ...loadedData,
        ...labels,
        featureToggles,
      },
    };
  } finally {
    clearServerQueryClient(queryClient);
  }
}
