import { BoxProps, Container } from '@chakra-ui/react';
import { PageName } from '@whitbread-eos/api';
import { ReactNode } from 'react';

interface Props {
  children: ReactNode;
  containerStyles?: BoxProps;
  pageName?: string;
}

export default function ContainerComponent({
  children,
  containerStyles,
  pageName,
}: Readonly<Props>) {
  const styles = {
    minW: '18.75rem', // 300px
    maxW: {
      base: 'none',
      mobile: '100%',
      sm: '100%',
      md: '100%',
      lg: 'var(--chakra-space-breakpoint-lg)',
      xl: 'var(--chakra-space-breakpoint-xl)',
    },
    px: { base: 0, mobile: '1rem', sm: '1.125rem', md: '1.5rem', lg: '1.75rem', xl: '4.125rem' },
  };

  const priceFinderStyles = {
    ...styles,
    minW: '100%', // 300px
    px: { base: 0 },
  };
  return (
    <Container
      {...styles}
      {...(pageName === PageName.PRICE_FINDER ? { ...priceFinderStyles } : { ...containerStyles })}
    >
      {children}
    </Container>
  );
}
