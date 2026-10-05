import { VStack } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import { SITE_LEISURE, PI_FAVICON } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import { formatAssetsUrl, getUserAndRoles } from '@whitbread-eos/utils';
import dynamic from 'next/dynamic';
import Head from 'next/head';

import useSetScreenSize from '~hooks/use-screensize';

const Header = dynamic(
  async () => {
    const { Header } = await import('@whitbread-eos/organisms');
    return { default: Header };
  },
  {
    ssr: false,
  }
);
const Footer = dynamic(
  async () => {
    const { FooterWrapper } = await import('@whitbread-eos/organisms');
    return { default: FooterWrapper };
  },
  {
    ssr: false,
  }
);
interface Props {
  children: React.ReactNode;
}

export default function BookingsLayout({ children }: Readonly<Props>) {
  useSetScreenSize();
  const queryClient = useQueryClient();

  return (
    <VStack minH="100vh">
      <Head>
        <title>Search booking results</title>
        <link rel="icon" type="image/x-icon" href={formatAssetsUrl(PI_FAVICON)} />
      </Head>
      <ErrorBoundary isHeaderBoundary={true}>
        <Header
          variant="agent"
          queryClient={queryClient}
          {...getUserAndRoles(children as React.ReactElement)}
        />
      </ErrorBoundary>
      {children}
      <ErrorBoundary isFooterBoundary={true}>
        <Footer site={SITE_LEISURE} />
      </ErrorBoundary>
    </VStack>
  );
}
