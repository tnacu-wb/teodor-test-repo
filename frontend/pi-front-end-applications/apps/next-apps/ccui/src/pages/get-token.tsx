import { Text } from '@chakra-ui/react';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import { GetServerSidePropsContext } from 'next';
import { ReactElement } from 'react';

import { DefaultLayout } from '~components';
import { auth0 } from '~lib/auth0';

interface Props {
  accessToken?: string;
  idToken?: string;
}

export default function GetTokenPage({ accessToken, idToken }: Readonly<Props>) {
  return (
    <>
      <Text fontWeight="bold">Access Token</Text>
      <p>{accessToken}</p>
      <hr />
      <Text fontWeight="bold">Id Token</Text>
      <p>{idToken}</p>
    </>
  );
}

GetTokenPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <DefaultLayout showFooter={false}>
      <ErrorBoundary>{page}</ErrorBoundary>
    </DefaultLayout>
  );
};

export async function getServerSideProps(context: GetServerSidePropsContext) {
  const { req } = context;
  const session = await auth0.getSession(req);

  if (!session) {
    return {
      redirect: {
        destination: `/auth/login?returnTo=${encodeURIComponent(context.resolvedUrl)}`,
        permanent: false,
      },
    };
  }

  if (
    !(
      process?.env?.AUTH0_BASE_URL?.includes('ccui.qa') ||
      process?.env?.AUTH0_BASE_URL?.includes('ccui.dev') ||
      process?.env?.AUTH0_BASE_URL?.includes('ccui.dit') ||
      process?.env?.AUTH0_BASE_URL?.includes('ccui.uat') ||
      process?.env?.AUTH0_BASE_URL?.includes('ccui.demo') ||
      process?.env?.AUTH0_BASE_URL?.includes('ccui.preprod') ||
      process?.env?.AUTH0_BASE_URL?.includes('ccui.perf') ||
      process?.env?.AUTH0_BASE_URL?.includes('localhost')
    )
  ) {
    return {
      redirect: {
        permanent: false,
        destination: '/',
      },
      props: {
        accessToken: undefined,
        idToken: undefined,
      },
    };
  }
  return {
    props: {
      accessToken: session.tokenSet.accessToken,
      idToken: session.tokenSet.idToken,
    },
  };
}
