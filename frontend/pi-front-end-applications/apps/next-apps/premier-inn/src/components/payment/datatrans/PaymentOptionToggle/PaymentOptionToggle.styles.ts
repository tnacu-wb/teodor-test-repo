import { BoxProps, FlexProps, TextProps } from '@chakra-ui/react';

// Figma: "When would you like to pay?" — Proxima Nova heading, Semibold 18px, 120% line-height
export const titleStyle = {
  fontFamily: 'heading',
  fontSize: 'lg',
  fontWeight: 'semibold',
  lineHeight: '120%',
  letterSpacing: '0',
  color: 'darkGrey1',
  mb: 'sm',
} as TextProps;

// Toggle bar: full-width pill with a 1px grey border, 44px tall, 2px padding
export const toggleBarStyle = {
  w: 'full',
  border: '1px solid #BFBFBF',
  borderRadius: '4px',
  h: '44px',
  p: '2px',
  bg: 'baseWhite',
  overflow: 'hidden',
} as FlexProps;

// Option label — sits above the sliding pill; colour toggled in the component
export const optionLabelStyle = {
  as: 'button' as const,
  role: 'radio',
  flex: 1,
  position: 'relative' as const,
  zIndex: 1,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  fontFamily: 'heading',
  fontSize: 'md',
  fontWeight: 'semibold',
  lineHeight: '120%',
  letterSpacing: '0',
  textAlign: 'center' as const,
  cursor: 'pointer',
  bg: 'transparent',
  _focusVisible: {
    outline: '2px solid',
    outlineColor: '#642587',
    outlineOffset: '2px',
    zIndex: 2,
  },
} as BoxProps;

// Figma: description text — Proxima Nova Regular 16px, darkGrey1, 150% line-height
export const descriptionStyle = {
  fontFamily: 'heading',
  fontSize: 'md',
  fontWeight: 'normal',
  lineHeight: '150%',
  letterSpacing: '0',
  color: 'darkGrey1',
  mt: 'sm',
} as TextProps;
