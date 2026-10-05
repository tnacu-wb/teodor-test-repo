import { Box, VStack } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import { Container, ErrorBoundary } from '@whitbread-eos/atoms';
import { getUserAndRoles } from '@whitbread-eos/utils';
import dynamic from 'next/dynamic';
import Head from 'next/head';

import useSetOrientation from '~hooks/use-orientation';
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

export default function PaymentLayout({ children }: Readonly<Props>) {
  useSetScreenSize();
  useSetOrientation();
  const queryClient = useQueryClient();

  return (
    <VStack minH="100vh">
      <Head>
        <title>Premier Inn</title>
      </Head>
      <ErrorBoundary isHeaderBoundary={true}>
        <Header variant="agent" queryClient={queryClient} {...getUserAndRoles(children)} />
      </ErrorBoundary>
      <Box as="main" w="full" flex="1">
        <ErrorBoundary>
          <Container>{children}</Container>
        </ErrorBoundary>
      </Box>
      <Box w="full" position="sticky" bottom="0" id="hotel-details-mobile-basket" />
    </VStack>
  );
}
