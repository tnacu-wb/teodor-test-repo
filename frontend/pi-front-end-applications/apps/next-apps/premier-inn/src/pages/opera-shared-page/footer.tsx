import { dehydrate, QueryClient } from '@tanstack/react-query';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import { getI18nLabels, getServerSideCustomLocale } from '@whitbread-eos/utils';
import { GetServerSidePropsContext } from 'next';
import { ReactElement } from 'react';

import { DefaultLayout } from '~components';

export default function Footer() {
  return <></>;
}

Footer.getLayout = function getLayout(page: ReactElement) {
  const containerStyles = {
    mx: 0,
    px: 0,
    maxWidth: '100% !important',
    height: '100%',
  };

  const mainStyles = {
    mb: '-0.5rem !important',
  };

  return (
    <>
      <DefaultLayout
        showPromoCode={true}
        showFooter={true}
        showHeader={false}
        showChildren={false}
        containerStyles={containerStyles}
        mainStyles={mainStyles}
      >
        <ErrorBoundary>{page}</ErrorBoundary>
      </DefaultLayout>
    </>
  );
};

export async function getServerSideProps({ locale = 'gb' }: GetServerSidePropsContext) {
  const { logger } = await import('@whitbread-eos/utils');

  const queryClient = new QueryClient();

  const { language } = getServerSideCustomLocale(locale);
  const labels = await getI18nLabels({
    language: language,
    queryClient,
  });

  logger.info({
    label: 'PI:MainPage',
    message: 'PageLoad',
  });

  return {
    props: {
      ...labels,
      dehydratedState: dehydrate(queryClient),
    },
  };
}
