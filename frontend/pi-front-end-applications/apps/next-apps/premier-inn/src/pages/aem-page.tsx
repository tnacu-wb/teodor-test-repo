import { dehydrate, QueryClient, useQueryClient } from '@tanstack/react-query';
import { PISearchContainer as Search } from '@whitbread-eos/organisms';
import {
  getI18nLabels,
  getServerSideCustomLocale,
  getUnleashToggles,
  useFeatureToggle,
  GLOBALS,
} from '@whitbread-eos/utils';
import { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';

import Transclude, { fetchMicroFrontend } from '~utils/Transclude/Transclude';
import { PAGE } from '~utils/pi-all-pages-constants';

interface Props {
  pageLayout: string | false;
  featureToggles: DynamicObject;
}

interface DynamicObject {
  [key: string]: boolean;
}

export default function AEMPage({ pageLayout, featureToggles }: Readonly<Props>) {
  const router = useRouter();
  const client = useQueryClient();
  const { page } = router.query;
  useFeatureToggle(featureToggles);

  return (
    <>
      <Search queryClient={client} />
      <Transclude src={(page as string) || ''} initialMarkup={pageLayout || undefined} />
    </>
  );
}

export async function getServerSideProps({ locale = 'gb', ...props }: GetServerSidePropsContext) {
  const { logger } = await import('@whitbread-eos/utils');

  const queryClient = new QueryClient();

  const { language, country } = getServerSideCustomLocale(locale);
  const labels = await getI18nLabels({
    language: language,
    queryClient,
  });

  const featureToggles: DynamicObject = await getUnleashToggles(
    props,
    PAGE.AEM_PAGE.featureToogles.appPage,
    PAGE.AEM_PAGE.featureToogles.flagsWithFallback,
    { country: country || GLOBALS.locale.GB }
  );
  let pageLayout: string | boolean = false;
  try {
    if (!featureToggles.release_pi_aem_page_csr_only && props.query?.page)
      pageLayout = await fetchMicroFrontend(props.query.page as string);
  } catch (error) {
    logger.info({
      label: 'PI:AEMPage',
      msg: error,
      error,
    });
    pageLayout = false;
  }

  logger.info({
    label: 'PI:MainPage',
    message: 'PageLoad',
  });

  return {
    props: {
      ...labels,
      dehydratedState: dehydrate(queryClient),
      pageLayout,
      featureToggles,
    },
  };
}
