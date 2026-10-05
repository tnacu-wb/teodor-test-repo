import { Box, SimpleGrid } from '@chakra-ui/react';
import { dehydrate, useQueryClient } from '@tanstack/react-query';
import { FT_PI_REDIS_RQ_CACHE, GET_STATIC_CONTENT, SITE_BB } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import { AuthContentManagerBBVariant, NewPassword } from '@whitbread-eos/organisms';
import {
  getServerSideCustomLocale,
  graphQLRequest,
  getI18nLabels,
  getGQLClient,
  getUnleashToggles,
  WB_SESSION_ID,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import { ReactElement, useState } from 'react';

import { DefaultLayout } from '~components';
import { PAGE } from '~utils/bb-all-pages-constants';

export default function ResetPasswordPage() {
  const queryClient = useQueryClient();
  const [isLoginModalOpen, setIsLoginModalOpen] = useState(false);
  const router = useRouter();
  const { token } = router.query;

  const defaultValues = {
    email: '',
    password: '',
    confirmPassword: '',
  };

  const toggleLoginModal = () => {
    setIsLoginModalOpen(!isLoginModalOpen);
  };

  return (
    <SimpleGrid columns={2} spacing={135} {...gridStyle}>
      <Box {...pageContentStyle}>
        <NewPassword
          defaultValues={defaultValues}
          toggleLoginModal={toggleLoginModal}
          token={token as string}
          queryClient={queryClient}
          isBusinessBooker={true}
        />
      </Box>
      <AuthContentManagerBBVariant
        isLoginModalOpen={isLoginModalOpen}
        toggleLoginModal={toggleLoginModal}
      />
    </SimpleGrid>
  );
}

ResetPasswordPage.getLayout = function getLayout(page: ReactElement<any>) {
  return (
    <DefaultLayout showFooter={false}>
      <ErrorBoundary>{page}</ErrorBoundary>
    </DefaultLayout>
  );
};

export async function getServerSideProps({ locale = 'gb', ...props }: GetServerSidePropsContext) {
  const { logger } = await import('@whitbread-eos/utils');

  const featureToggles: { [key: string]: boolean } = await getUnleashToggles(
    props,
    PAGE.RESET_PASSWORD.featureToggles.appPage,
    PAGE.RESET_PASSWORD.featureToggles.flagsWithFallback
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.RESET_PASSWORD.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const cookies = new Cookies(props.req, props.res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);

  const { language, country } = getServerSideCustomLocale(locale);
  await queryClient.prefetchQuery({
    queryKey: ['GetStaticContent', language, country],
    queryFn: () =>
      graphQLRequest(
        GET_STATIC_CONTENT,
        {
          language,
          country,
          site: SITE_BB,
          businessBooker: true,
        },
        undefined,
        undefined,
        client
      ),
  });

  logger?.info({
    label: 'BB:ResetPasswordPage',
    message: 'PageLoad',
  });

  const labels = await getI18nLabels({
    language,
    queryClient,
  });

  return {
    props: {
      ...labels,
      dehydratedState: dehydrate(queryClient),
    },
  };
}

const gridStyle = {
  w: 'full',
  maxW: 'var(--chakra-space-breakpoint-xl)',
  pb: {
    mobile: 'md',
    sm: '5',
    md: 'lg',
    lg: '7',
    xl: '5xl',
  },
  pt: {
    mobile: '0px',
    lg: '2xl',
  },
  m: '0px',
  templateColumns: {
    mobile: '1fr',
    lg: '1fr auto',
  },
  columnGap: {
    mobile: '0px',
    lg: '32',
    xl: '8.5rem',
  },
};

const pageContentStyle = {
  pt: {
    mobile: 'lg',
    sm: 'xl',
    md: '2xl',
    lg: '0px',
  },
};
