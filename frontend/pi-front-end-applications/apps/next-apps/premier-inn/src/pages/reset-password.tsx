import { Box, SimpleGrid } from '@chakra-ui/react';
import { dehydrate, QueryClient, useQueryClient } from '@tanstack/react-query';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import { AuthContentManagerPIVariant, NewPassword } from '@whitbread-eos/organisms';
import { getI18nLabels, getServerSideCustomLocale, getUnleashToggles } from '@whitbread-eos/utils';
import { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import { ReactElement, useState } from 'react';

import { DefaultLayout } from '~components';
import { PAGE } from '~utils/pi-all-pages-constants';

export default function ResetPasswordPage() {
  const queryClient = useQueryClient();
  const [isLoginModalOpen, setIsLoginModalOpen] = useState(false);
  const toggleLoginModal = () => {
    setIsLoginModalOpen(!isLoginModalOpen);
  };
  const router = useRouter();
  const { token } = router.query;

  const defaultValues = {
    email: '',
    password: '',
    confirmPassword: '',
  };

  return (
    <SimpleGrid columns={2} spacing={135} {...gridStyle}>
      <Box {...pageContentStyle}>
        <NewPassword
          defaultValues={defaultValues}
          toggleLoginModal={toggleLoginModal}
          token={token as string}
          queryClient={queryClient}
          isBusinessBooker={false}
        />
      </Box>
      <AuthContentManagerPIVariant
        isLoginModalOpen={isLoginModalOpen}
        toggleLoginModal={toggleLoginModal}
      />
    </SimpleGrid>
  );
}

ResetPasswordPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <DefaultLayout showFooter={false}>
      <ErrorBoundary>{page}</ErrorBoundary>
    </DefaultLayout>
  );
};

export async function getServerSideProps({ locale = 'gb', ...props }: GetServerSidePropsContext) {
  const { logger } = await import('@whitbread-eos/utils');

  const queryClient = new QueryClient();

  logger.info({
    label: 'PI:ResetPasswordPage',
    message: 'PageLoad',
  });
  const { language } = getServerSideCustomLocale(locale);

  const [labels, featureToggles] = await Promise.all([
    getI18nLabels({ language, queryClient }),
    getUnleashToggles(
      props,
      PAGE.RESET_PASSWORD.featureToggles.appPage,
      PAGE.RESET_PASSWORD.featureToggles.flagsWithFallback
    ),
  ]);

  return {
    props: {
      ...labels,
      dehydratedState: dehydrate(queryClient),
      featureToggles,
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
