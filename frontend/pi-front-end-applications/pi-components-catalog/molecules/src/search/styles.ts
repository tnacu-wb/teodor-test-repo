import type { FlexProps } from '@chakra-ui/react';
import { ternaryCondition } from '@whitbread-eos/utils';

export function getSearchStyles(
  isPortrait: boolean | undefined,
  isLessThanSm: boolean | undefined,
  isError?: boolean | undefined,
  locale?: string | undefined
) {
  const containerResponsiveStyles: FlexProps = {
    w: {
      mobile: '18rem',
      xs: '21.4375rem',
      sm: '33.75rem',
      md: ternaryCondition(isPortrait, '45rem', '76.5rem'),
      lg: '76.5rem',
      xl: '81.75rem',
    },
    h: {
      mobile: 'var(--chakra-space-4xl)',
      xs: 'var(--chakra-space-4xl)',
      sm: 'var(--chakra-space-4xl)',
      md: ternaryCondition(isPortrait, 'var(--chakra-space-4xl)', 'var(--chakra-space-6xl)'),
      lg: 'var(--chakra-space-6xl)',
    },
    flexWrap: {
      mobile: 'wrap',
      xs: 'wrap',
      sm: 'nowrap',
    },
    borderRadius: 'var(--chakra-space-xs)',
    border: {
      xs: 'none',
      sm: '1px solid var(--chakra-colors-lightGrey1)',
    },
    boxShadow: {
      xs: 'none',
      sm: '0px 0.125rem 0.75rem var(--chakra-colors-lightGrey2)',
    },
  };
  const autocompleteInputWrapperStyles = {
    w: {
      mobile: '18rem',
      xs: '21.4375rem',
      sm: '9.8125rem',
      md: ternaryCondition(isPortrait, '11.625rem', '24.625rem'),
      lg: '25.25rem',
      xl: '26.25rem',
    },
    marginBottom: {
      mobile: 'md',
      xs: 'md',
      sm: 0,
    },
  };
  const inputGroupStyles = {
    w: 'auto',
    h: {
      mobile: 'var(--chakra-space-4xl)',
      xs: 'var(--chakra-space-4xl)',
      sm: 'var(--chakra-space-4xl)',
      md: ternaryCondition(isPortrait, 'var(--chakra-space-4xl)', 'var(--chakra-space-6xl)'),
      lg: 'var(--chakra-space-6xl)',
    },
    border: {
      mobile: '1px solid var(--chakra-colors-lightGrey1)',
      xs: '1px solid var(--chakra-colors-lightGrey1)',
      sm: 0,
      md: 0,
    },
    borderRadius: {
      mobile: 'var(--chakra-space-xs)',
      xs: 'var(--chakra-space-xs)',
      sm: 0,
      md: 0,
    },
  };
  const inputElementStyles = {
    borderRight: '1px solid var(--chakra-colors-lightGrey4)',
    borderTopRightRadius: ternaryCondition(isLessThanSm, 'var(--chakra-space-xs)', 0),
    borderBottomRightRadius: ternaryCondition(isLessThanSm, 'var(--chakra-space-xs)', 0),
    color: 'darkGrey1',
    sx: {
      '::placeholder': {
        color: 'darkGrey2',
      },
    },
  };
  const datepickerInputElementStyles = {
    ...inputElementStyles,
    w: {
      mobile: '8.75rem',
      xs: '10.25rem',
      sm: '9.6875rem',
      md: ternaryCondition(isPortrait, '11.5rem', '19.375rem'),
      lg: '19.375rem',
      xl: '19.78125rem',
    },
    h: '100%',
  };
  const iconStyles = {
    top: {
      mobile: 'var(--chakra-space-sm)',
      xs: 'var(--chakra-space-sm)',
      sm: 'var(--chakra-space-sm)',
      md: ternaryCondition(isPortrait, 'var(--chakra-space-sm)', 'var(--chakra-space-md)'),
      lg: 'var(--chakra-space-md)',
    },
  };
  const noOfNightsInputElementStyles = {
    ...inputElementStyles,
    padding:
      'var(--chakra-space-lg) var(--chakra-space-lg) var(--chakra-space-lg) var(--chakra-space-2xl)',
    w: {
      xs: '10.25rem',
      md: '9.688rem',
    },
    h: '100%',
    _hover: {
      border: '0.0625rem solid var(--chakra-colors-darkGrey1)',
      borderRadius: 'var(--chakra-space-xs)',
      color: 'darkGrey2',
    },
    _active: {
      borderColor: 'primary',
      borderWidth: '0.125rem',
      borderRadius: 'var(--chakra-space-xs)',
      color: 'darkGrey1',
    },
    _focus: {
      borderColor: 'primary',
      borderWidth: '0.125rem',
      color: 'darkGrey1',
      borderRadius: 'var(--chakra-space-xs)',
    },
    ...(isError
      ? {
          border: 'none',
          _hover: {
            border: 'none',
          },
          _focus: {
            border: 'none',
          },
        }
      : {}),
  };
  const autocompleteListStyles = {
    border: '1px solid var(--chakra-colors-lightGrey3)',
    borderRadius: 'radiusSmall',
    boxShadow: '0 0.125rem var(--chakra-space-xmd) var(--chakra-colors-lightGrey2)',
  };
  const autocompleteListProps = {
    marginTop: 0,
    w: {
      mobile: '18rem',
      xs: '21.4375rem',
      sm: '23.375rem',
      lg: '25.25rem',
      xl: '26.25rem',
    },
    maxHeight: 'auto',
    py: 'sm',
  };
  const autocompleteListItemStyles = {
    h: 'var(--chakra-space-2xl)',
    fontSize: 'sm',
    lineHeight: '1',
    fontWeight: 'normal',
    color: 'darkGrey1',
    mx: 0,
    p: 'var(--chakra-space-xmd) var(--chakra-space-md)',
    borderRadius: 0,
    bg: 'var(--chakra-colors-baseWhite) !important',

    _hover: {
      bg: 'var(--chakra-colors-lightGrey5) !important',
    },
  };
  const autocompleteItemValueStyles = {
    display: 'block',
    textOverflow: 'ellipsis',
    overflow: 'hidden',
  };

  const autocompleteGroupTitleStyles = {
    fontSize: 'sm',
    lineHeight: '2',
    fontWeight: 'normal',
    marginLeft: 'md',
    color: 'darkGrey1',
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
    sm: 'var(--chakra-space-2xl)',
    xs: 'var(--chakra-space-2xl)',
    mobile: 'auto',
  };

  const alertStyles = {
    py: 'sm',
    paddingRight: '0',
  };

  const marginBottomMobileError = locale === 'de' ? '5rem' : '5xl';

  const errorMarginBottom = {
    sm: '0',
    xs: ternaryCondition(isError, '5xl', 'md'),
    mobile: ternaryCondition(isError, marginBottomMobileError, 'md'),
  };

  const errorInputGroupStyles = {
    ...inputGroupStyles,
    ...(isError
      ? {
          borderRadius: 'var(--chakra-space-radiusSmall)',
          border: '2px solid var(--chakra-colors-error)',
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
        }
      : {}),
  };

  const buttonStyles = {
    w: {
      mobile: '18rem',
      xs: '21.4375rem',
      sm: 'var(--chakra-space-3xl)',
      md: ternaryCondition(isPortrait, '8.875rem', '11.75rem'),
      lg: '11.75rem',
    },
    h: {
      xs: 'var(--chakra-space-4xl)',
      sm: 'var(--chakra-space-2xl)',
      md: ternaryCondition(isPortrait, 'var(--chakra-space-2xl)', 'var(--chakra-space-4xl)'),
      lg: 'var(--chakra-space-4xl)',
    },
    ml: {
      mobile: 0,
      xs: 0,
      sm: 'sm',
      xl: 'lg',
    },
    mt: {
      mobile: 'var(--chakra-space-md)',
      xs: 'var(--chakra-space-md)',
      sm: 0,
    },
  };
  const roomPickerWrapperStyles = {
    mt: 0,
    w: {
      mobile: '8.75rem',
      xs: '10.1875rem',
      sm: '16rem',
      md: ternaryCondition(isPortrait, '11.625rem', '19.5rem'),
      lg: '18.875rem',
      xl: '21.90625rem',
    },
    h: {
      mobile: 'var(--chakra-space-4xl)',
      xs: 'var(--chakra-space-4xl)',
      sm: 'var(--chakra-space-4xl)',
      md: ternaryCondition(isPortrait, 'var(--chakra-space-4xl)', 'var(--chakra-space-6xl)'),
      lg: 'var(--chakra-space-6xl)',
    },
  };
  const roomPickerInputElementStyles = {
    w: '100%',
    h: '100%',
    ml: {
      mobile: 'var(--chakra-space-sm)',
      xs: 'var(--chakra-space-md)',
      sm: 0,
    },
    border: {
      mobile: '1px solid var(--chakra-colors-lightGrey1)',
      xs: '1px solid var(--chakra-colors-lightGrey1)',
      sm: 'none',
    },
  };

  return {
    containerResponsiveStyles,
    autocompleteInputWrapperStyles,
    inputGroupStyles,
    inputElementStyles,
    datepickerInputElementStyles,
    iconStyles,
    noOfNightsInputElementStyles,
    autocompleteListStyles,
    autocompleteListProps,
    autocompleteListItemStyles,
    autocompleteItemValueStyles,
    autocompleteGroupTitleStyles,
    errorInputGroupStyles,
    errorInputElementStyles,
    errorMarginBottom,
    alertStyles,
    errorResponsiveWidth,
    errorResponsiveHeight,
    buttonStyles,
    roomPickerWrapperStyles,
    roomPickerInputElementStyles,
  };
}
