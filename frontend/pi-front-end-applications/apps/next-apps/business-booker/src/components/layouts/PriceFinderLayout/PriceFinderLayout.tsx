import { VStack } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import { SITE_BB } from '@whitbread-eos/api';
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
        <Header variant="business-step" queryClient={queryClient} />
      </ErrorBoundary>
      {children}
      <ErrorBoundary isFooterBoundary={true}>
        <Footer isPremierInn={true} site={SITE_BB} />
      </ErrorBoundary>
    </VStack>
  );
}
