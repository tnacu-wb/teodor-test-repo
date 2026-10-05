import { VStack } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import dynamic from 'next/dynamic';

import useSetScreenSize from '~hooks/use-screensize';

interface Props {
  children: React.ReactNode;
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

export default function AddToWalletLayout({ children }: Readonly<Props>) {
  useSetScreenSize();
  const queryClient = useQueryClient();

  return (
    <VStack minH="100vh" gap={0}>
      <ErrorBoundary isHeaderBoundary={true}>
        <Header variant="default" queryClient={queryClient} />
      </ErrorBoundary>
      {children}
    </VStack>
  );
}
