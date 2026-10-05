import type { FlexProps } from '@chakra-ui/react';
import { ternaryCondition } from '@whitbread-eos/utils';

export function getSearchStyles(
  isLessThanSm: boolean | undefined,
  isError?: boolean | undefined,
  locale?: string | undefined,
  items?: any,
  validationField?: any
) {
  const searchWrapper: FlexProps = {
    minW: {
      base: 'full',
      md: '45rem',
      lg: '76.5rem',
      xl: '81.75rem',
    },
    h: {
      base: 'var(--chakra-space-4xl)',
      lg: 'var(--chakra-space-6xl)',
    },
    flexWrap: {
      base: 'wrap',
      sm: 'nowrap',
    },
    borderRadius: 'var(--chakra-space-xs)',
    border: {
      base: 'none',
      sm: '1px solid var(--chakra-colors-lightGrey2)',
    },
    boxShadow: {
      base: 'none',
      sm: '0px 0.125rem 0.75rem var(--chakra-colors-lightGrey2)',
    },
    position: 'relative',
  };

  const locationPickerStyles = {
    w: {
      base: 'full',
      md: '8.188rem',
      lg: '19.563rem',
    },
    flex: { base: '1 1 auto', lg: 'none' },
    marginBottom: {
      sm: 0,
      xs: 'md',
      mobile: 'md',
    },
  };

  const inputGroupStyles = {
    w: 'full',
    h: {
      base: 'var(--chakra-space-4xl)',
      lg: 'var(--chakra-space-6xl)',
    },
    border: {
      base: '1px solid var(--chakra-colors-lightGrey1)',
      sm: 0,
    },
    borderRadius: {
      base: 'var(--chakra-space-xs)',
      sm: 0,
    },
  };

  const inputElementStyles = {
    borderRight: '1px solid var(--chakra-colors-lightGrey4)',
    borderBottomRightRadius: isLessThanSm ? 'var(--chakra-space-xs)' : 0,
    borderTopRightRadius: isLessThanSm ? 'var(--chakra-space-xs)' : 0,
    color: 'darkGrey1',
    sx: {
      '::placeholder': {
        color: 'darkGrey2',
      },
    },
  };

  const bookingDatepickerSize = {
    w: {
      base: '49%',
      sm: 'full',
      md: '7.125rem',
      lg: '13.5rem',
    },
  };

  const datepickerInputElementStyles = {
    h: '100%',
    flex: { base: '1 1 50%', sm: '1 1 auto' },
    mr: { base: 'xs', xs: 'sm', sm: '0' },
    _placeholder: {
      color: 'var(--chakra-colors-darkGrey1)',
    },
  };

  const iconStyles = {
    top: {
      base: 'var(--chakra-space-sm)',
      lg: 'var(--chakra-space-md)',
    },
  };

  const errorResponsiveWidth = {
    xl: '27rem',
    lg: '25rem',
    md: '25.3125rem',
    sm: '25.3125rem',
    xs: '21.4375rem',
    mobile: '18rem',
  };

  const errorResponsiveHeight = {
    mobile: 'auto',
    xs: 'var(--chakra-space-2xl)',
  };

  const alertStyles = {
    py: 'sm',
    paddingRight: '0',
  };

  const errorMarginBottom = {
    sm: '0',
    xs: ternaryCondition(isError, '5xl', 'md'),
    mobile: ternaryCondition(isError, locale === 'de' ? '5rem' : '5xl', 'md'),
  };

  const errorInputGroupStyles = {
    ...inputGroupStyles,
    ...(isError
      ? {
          border:
            items?.length === 0 || validationField !== 'location'
              ? '2px solid var(--chakra-colors-error)'
              : '',
          borderRadius: 'var(--chakra-space-radiusSmall)',
          _hover: {
            border: '2px solid var(--chakra-colors-primary)',
            borderRadius: 'var(--chakra-space-radiusSmall)',
          },
          _focus: {
            border: '2px solid var(--chakra-colors-primary)',
            borderRadius: 'var(--chakra-space-radiusSmall)',
          },
        }
      : {}),
  };

  const errorInputElementStyles = {
    ...inputElementStyles,
    ...(isError
      ? {
          border: 'none',
          _hover: {
            border: 'none',
          },
          _focus: {
            border: 'none',
          },
          _active: {
            border: 'none',
          },
        }
      : {}),
  };

  const buttonStyles = {
    w: {
      base: 'full',
      sm: '3xl',
      md: '8.875rem',
      lg: '8.063rem',
      xl: '11.75rem',
    },
    h: {
      xs: 'var(--chakra-space-4xl)',
      sm: 'var(--chakra-space-2xl)',
      lg: 'var(--chakra-space-4xl)',
    },
    ml: {
      sm: 'sm',
      lg: '0.375rem',
      xl: '0.813rem',
    },
    mr: {
      sm: 'sm',
    },
    mt: {
      base: 'var(--chakra-space-md)',
      sm: 0,
    },
    flex: { sm: '1 1 20%', md: '1 1 10%', lg: 'none' },
  };

  const roomPickerSize = { w: { base: '49%', sm: 'full', md: '6.563rem', lg: '12.688rem' } };

  const roomPickerWrapperStyles = {
    mt: 0,
    h: {
      base: 'var(--chakra-space-4xl)',
      lg: 'var(--chakra-space-6xl)',
    },
  };

  const roomPickerInputElementStyles = {
    w: '100%',
    h: '100%',
    flex: { base: '1 1 50%', sm: '1 1 auto' },
    ml: {
      base: 'xs',
      xs: 'sm',
      sm: '0',
    },
    border: {
      base: '1px solid var(--chakra-colors-lightGrey1)',
      sm: 'none',
    },
    _placeholder: {
      color: 'var(--chakra-colors-darkGrey1)',
    },
  };

  return {
    searchWrapper,
    locationPickerStyles,
    inputGroupStyles,
    inputElementStyles,
    iconStyles,
    bookingDatepickerSize,
    datepickerInputElementStyles,
    errorInputGroupStyles,
    errorInputElementStyles,
    errorMarginBottom,
    alertStyles,
    errorResponsiveWidth,
    errorResponsiveHeight,
    buttonStyles,
    roomPickerSize,
    roomPickerWrapperStyles,
    roomPickerInputElementStyles,
  };
}
