import { Box, VStack } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import { Container, ErrorBoundary } from '@whitbread-eos/atoms';
import { Header } from '@whitbread-eos/organisms';

import useSetOrientation from '~hooks/use-orientation';
import useSetScreenSize, { useScreenSize } from '~hooks/use-screensize';

interface Props {
  children: React.ReactNode;
}

export default function SecondaryHDPLayout({ children }: Readonly<Props>) {
  useSetScreenSize();
  useSetOrientation();
  const queryClient = useQueryClient();
  const { isLessThanSm } = useScreenSize();

  return (
    <VStack minH="100vh">
      <Box as="header" w="full">
        <ErrorBoundary isHeaderBoundary={true}>
          <Header variant="logo" isIcon={isLessThanSm} queryClient={queryClient} />
        </ErrorBoundary>
      </Box>
      <Box as="main" w="full" flex="1">
        <Container>{children}</Container>
      </Box>
      <Box w="full" zIndex={2} position="sticky" bottom="0" id="hotel-details-mobile-basket" />
    </VStack>
  );
}
