import { VStack, Box } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import { SITE_LEISURE } from '@whitbread-eos/api';
import { Container, ErrorBoundary } from '@whitbread-eos/atoms';
import { getUserAndRoles } from '@whitbread-eos/utils';
import dynamic from 'next/dynamic';

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

const Footer = dynamic(
  () => import('@whitbread-eos/organisms').then((mod) => ({ default: mod.FooterWrapper })),
  { ssr: false }
);
export default function PriceFinderLayout({ children }: Readonly<Props>) {
  useSetScreenSize();
  const queryClient = useQueryClient();

  return (
    <VStack minH="100vh" gap={0}>
      <ErrorBoundary isHeaderBoundary={true}>
        <Header variant="agent" queryClient={queryClient} {...getUserAndRoles(children)} />
      </ErrorBoundary>
      {children}
      <ErrorBoundary isFooterBoundary={true}>
        <Box w="full">
          <Container>
            <Footer site={SITE_LEISURE} />
          </Container>
        </Box>
      </ErrorBoundary>
    </VStack>
  );
}
