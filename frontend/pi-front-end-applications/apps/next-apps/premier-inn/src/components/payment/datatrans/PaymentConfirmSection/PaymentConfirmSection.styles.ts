import { BoxProps, ButtonProps, FlexProps, TextProps } from '@chakra-ui/react';

export const containerStyle = {
  flexDirection: 'column',
  gap: 'md',
  // Full-bleed grey band on mobile — escape parent column padding with negative margins,
  // then re-add the same values as px so content stays aligned.
  bg: { mobile: 'lightGrey5', lg: 'transparent' },
  mx: { mobile: '-md', sm: '-5', md: '-lg', lg: '0px' },
  px: { mobile: 'md', sm: '5', md: 'lg', lg: '0px' },
  mt: 'md',
  w: { mobile: 'auto', lg: 'full' },
} as FlexProps;

export const termsStyle = {
  fontSize: 'xs',
  fontWeight: 'normal',
  lineHeight: '1.4',
  color: 'darkGrey1',
} as TextProps;

export const termsSx = {
  a: {
    textDecoration: 'underline',
    color: 'darkGrey1',
  },
};

export const amountRowStyle = {
  flexDirection: 'row',
  justify: 'space-between',
  align: 'center',
  gap: 'md',
  w: 'full',
} as FlexProps;

export const amountGroupStyle = {
  flexDirection: 'column',
  gap: '4px',
  flexShrink: 0,
  pr: 'lg',
} as FlexProps;

export const pencePriceOverrideStyle = {
  '[data-testid="pence-price"]': {
    fontSize: '23px',
    fontWeight: 600,
  },
  '[data-testid="pence-price-leading-symbol"], [data-testid="pence-price-integer"], [data-testid="pence-price-decimal"], [data-testid="pence-price-trailing-symbol"]':
    {
      fontSize: '23px',
      fontWeight: 600,
      lineHeight: '120%',
    },
  '[data-testid="pence-price-separator"]': {
    visibility: 'visible',
    width: 'auto',
    fontSize: '23px',
    fontWeight: 600,
    lineHeight: '120%',
  },
};

export const amountSubtextStyle = {
  fontSize: 'sm',
  fontWeight: 'normal',
  lineHeight: '1.4',
  color: 'darkGrey2',
} as TextProps;

// "Confirm booking" — Primary button: purple fill, white text, pill shape, 56px height
export const confirmButtonStyle = {
  flex: 1,
  h: '56px',
  borderRadius: '999px',
  bg: '#642587',
  color: 'baseWhite',
  fontFamily: 'SunsetSans, helvetica, arial, sans-serif',
  fontWeight: 'semibold',
  fontSize: 'lg',
  lineHeight: '120%',
  px: '2xl',
  _hover: { bg: 'btnSecondaryHoverBg' },
  _active: { bg: '#511E62' },
  _focus: { boxShadow: '0 0 0 3px rgba(100, 37, 135, 0.4)' },
} as ButtonProps;

// "Back" — Tertiary button: no fill, purple text, pill shape, 56px height
export const backButtonStyle = {
  w: 'full',
  h: '56px',
  borderRadius: '999px',
  bg: 'transparent',
  color: '#642587',
  fontFamily: 'SunsetSans, helvetica, arial, sans-serif',
  fontWeight: 'semibold',
  fontSize: 'lg',
  lineHeight: '120%',
  px: '2xl',
  _hover: { bg: 'rgba(100, 37, 135, 0.06)' },
  _active: { bg: 'rgba(100, 37, 135, 0.12)' },
  _focus: { boxShadow: '0 0 0 3px rgba(100, 37, 135, 0.4)' },
} as ButtonProps;

export const backButtonInnerStyle = {
  align: 'center',
  justify: 'center',
  gap: 'xs',
} as FlexProps;

export const chevronContainerStyle = {
  flexShrink: 0,
  w: '24px',
  h: '24px',
} as BoxProps;
