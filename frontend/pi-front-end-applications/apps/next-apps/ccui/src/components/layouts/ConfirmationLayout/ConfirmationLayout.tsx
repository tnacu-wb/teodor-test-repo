import { VStack } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import { PI_FAVICON } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import { formatAssetsUrl, getUserAndRoles } from '@whitbread-eos/utils';
import dynamic from 'next/dynamic';
import Head from 'next/head';

import useSetScreenSize from '~hooks/use-screensize';

interface Props {
  children: React.ReactElement;
}
const Header = dynamic(
  async () => {
    const { Header } = await import('@whitbread-eos/organisms');
    return { default: Header };
  },
  {
    ssr: false,
  }
);

export default function ConfirmationLayout({ children }: Readonly<Props>) {
  useSetScreenSize();
  const queryClient = useQueryClient();

  return (
    <VStack minH="100vh">
      <Head>
        <title>Booking Confirmation </title>
        <link rel="icon" type="image/x-icon" href={formatAssetsUrl(PI_FAVICON)} />
      </Head>
      <ErrorBoundary isHeaderBoundary={true}>
        <Header variant="agent" queryClient={queryClient} {...getUserAndRoles(children)} />
      </ErrorBoundary>
      {children}
    </VStack>
  );
}
