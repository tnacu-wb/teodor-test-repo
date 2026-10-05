import { BoxProps, FlexProps, GridItemProps, GridProps } from '@chakra-ui/react';

export const mainPaymentGridStyle = {
  w: 'full',
  maxW: 'var(--chakra-space-breakpoint-xl)',
  px: { mobile: '0px', lg: '7', xl: '5xl' },
  pb: { mobile: '0px', sm: '0px', md: '0px', lg: '7', xl: '5xl' },
  pt: { mobile: '0px', lg: '5xl' },
  m: '0px',
  templateColumns: { mobile: '1fr', lg: '1fr auto' },
  columnGap: { mobile: '0px', lg: '32', xl: '8.5rem' },
} as GridProps;

export const pageContentStyle = {
  minW: 0,
  w: { lg: '500px' },
  mx: { lg: 'auto' },
  px: { mobile: 'md', sm: '5', md: 'lg', lg: '0px' },
  pt: { mobile: 'lg', sm: 'xl', md: '2xl', lg: '0px' },
  // On mobile: stretch to at least the full viewport height so PaymentConfirmSection
  // is pushed to the bottom when page content is shorter than the screen.
  display: { mobile: 'flex', lg: 'block' },
  flexDirection: { mobile: 'column', lg: 'unset' },
  minH: { mobile: '100dvh', lg: 'unset' },
} as GridItemProps;

export const bookingSummaryMobileContainerStyle = {
  display: { mobile: 'block', lg: 'none' },
  backgroundColor: 'lightGrey5',
} as GridItemProps;

export const bookingSummaryMobileTriggerStyle = {
  justifyContent: 'center',
  fontWeight: 'bold',
  flexDirection: 'column',
} as FlexProps;

export const bookingSummaryDesktopStyle = {
  display: { mobile: 'none', lg: 'block' },
  w: { lg: '72', xl: '19.3125rem' },
} as GridItemProps;

export const loadingStyle = {
  height: '100%',
  width: '100%',
  left: 0,
  top: 0,
  position: 'fixed',
  zIndex: 1,
  backgroundColor: 'baseWhite',
  textAlign: 'center',
  lineHeight: 3,
  justifyContent: 'center',
  color: 'btnSecondaryEnabled',
  fontSize: 'xl',
  flex: 'none',
  flexGrow: 0,
  order: 1,
  alignSelf: 'stretch',
} as BoxProps;

export const partialLoadingStyle = {
  height: '100%',
  width: '100%',
  backgroundColor: 'baseWhite',
  textAlign: 'center',
  lineHeight: 3,
  justifyContent: 'center',
  color: 'btnSecondaryEnabled',
  fontSize: 'xl',
  flex: 'none',
  flexGrow: 0,
  order: 1,
  alignSelf: 'stretch',
} as BoxProps;

export const securityContainerStyle = {
  w: { mobile: 'full', md: '45rem', lg: '50.5rem', xl: '54rem' },
};

export const termsAndConditionsStyle = {
  mt: 'lg',
  p: { fontSize: 'md', fontWeight: 'normal', lineHeight: '3' },
  a: { textDecoration: 'underline', color: 'zipSecondary' },
};
