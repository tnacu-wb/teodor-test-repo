import { BoxProps, TextProps } from '@chakra-ui/react';
import type { CSSProperties } from 'react';

// Brand purple used across Datatrans payment components (#642587).
// Exported so the component can reference these in Framer Motion `animate` values
// (Framer Motion does not resolve Chakra token names at runtime).
export const brandPurple = '#642587';
export const borderUnchecked = '#BFBFBF';

// Visually-hidden style for the native <input type="checkbox">.
// Keeps the element in the accessibility tree and focusable while
// removing it from visual flow — avoids the Chakra VisuallyHidden `as` caveat.
export const hiddenInputStyle: CSSProperties = {
  position: 'absolute',
  width: '1px',
  height: '1px',
  padding: 0,
  margin: '-1px',
  overflow: 'hidden',
  clip: 'rect(0, 0, 0, 0)',
  whiteSpace: 'nowrap',
  border: 0,
};

// 20×20 outer square — border colour is animated by Framer Motion in the component
export const checkboxControlStyle = {
  w: '20px',
  h: '20px',
  minW: '20px',
  borderWidth: '1.5px',
  borderStyle: 'solid',
  borderColor: borderUnchecked,
  borderRadius: '0',
  bg: 'white',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  mt: '2px',
} as BoxProps;

// 10×10 inner purple square — rendered only when checked
export const checkboxInnerStyle = {
  w: '10px',
  h: '10px',
  bg: brandPurple,
  flexShrink: 0,
} as BoxProps;

export const labelStyle = {
  fontFamily: 'Proxima Nova Sans, helvetica, arial, sans-serif',
  fontWeight: '400',
  fontSize: '16px',
  lineHeight: '24px',
  letterSpacing: '0',
  color: 'darkGrey1',
} as TextProps;
