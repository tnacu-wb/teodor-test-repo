import type { ComponentStyleConfig } from '@chakra-ui/theme';

const Datepicker: ComponentStyleConfig = {
  baseStyle: {
    '.react-datepicker-popper': {
      zIndex: 1801,
    },
    '.react-datepicker': {
      minH: '23rem',
      border: '0.0625rem solid var(--chakra-colors-lightGrey3)',
      borderRadius: 'var(--chakra-space-xs)',
      boxShadow: '0 0.125rem 0.75rem var(--chakra-colors-lightGrey2)',
      fontFamily: 'var(--chakra-fonts-body)',

      '.react-datepicker__triangle': {
        display: 'none',
      },
    },
    '.react-datepicker__navigation': {
      top: {
        mobile: 'calc(var(--chakra-space-lg) - var(--chakra-space-xs))',
        xs: 'calc(var(--chakra-space-xl) - var(--chakra-space-xs))',
      },

      '.react-datepicker__navigation-icon:before': {
        left: '0',
        right: '0',
        borderWidth: '0.09375rem 0.09375rem 0 0',
        borderColor: 'var(--chakra-colors-darkGrey2)',
        w: 'var(--chakra-space-sm)',
        h: 'var(--chakra-space-sm)',
      },

      '&.react-datepicker__navigation--previous': {
        left: 'var(--chakra-space-xl)',
      },

      '&.react-datepicker__navigation--next': {
        right: 'var(--chakra-space-xl)',
      },
    },
    '.react-datepicker__header--custom': {
      position: 'relative',
      '.navigation-btn-left, .navigation-btn-right': {
        position: 'absolute',
        top: '2xl',
        '.react-datepicker__navigation-icon:before': {
          left: '0',
          right: '0',
          borderWidth: '0.09375rem 0.09375rem 0 0',
          borderColor: 'var(--chakra-colors-darkGrey2)',
          w: 'var(--chakra-space-sm)',
          h: 'var(--chakra-space-sm)',
        },
      },
      '.navigation-btn-left': {
        left: 'xl',
      },
      '.navigation-btn-right': {
        right: 'xl',
      },
    },
    '.react-datepicker__month-container': {
      w: '100%',
      marginBottom: 'var(--chakra-space-lg)',

      '.react-datepicker__header': {
        bgColor: 'var(--chakra-colors-baseWhite)',
        borderBottom: '0',
      },
      '.react-datepicker__month': {
        margin: '0',
        '.react-datepicker__week': {
          '.react-datepicker__day': {
            fontSize: 'var(--chakra-fontSizes-md)',
            lineHeight: 'var(--chakra-lineHeights-4)',
            fontWeight: 'var(--chakra-fontWeights-semibold)',
            position: 'relative',
            outline: 'none',
            '.highlight': {
              position: 'absolute',
              top: '0',
              left: 'auto',
              right: '50%',
              zIndex: '1',
            },
            '.day-number': {
              border: '0.125rem solid transparent',
              color: 'var(--chakra-colors-darkGrey1)',
              padding: '0.3125rem',
              borderRadius: '50%',
              position: 'relative',
              zIndex: '2',
            },
            '&:hover:not(.react-datepicker__day--disabled, .react-datepicker__day--in-range)': {
              bgColor: 'transparent',
              borderRadius: '0',
              '.day-number': {
                borderColor: 'var(--chakra-colors-primary)',
                bgColor: 'baseWhite',
                borderRadius: '50%',
                color: 'darkGrey1',
              },
            },
            '&.react-datepicker__day--disabled': {
              '.day-number': {
                color: 'var(--chakra-colors-lightGrey2)',
              },
            },
            '&.react-datepicker__day--today': {
              '.day-number': {
                bgColor: 'var(--chakra-colors-baseWhite)',
              },
            },
            '&.react-datepicker__day--keyboard-selected': {
              bgColor: 'transparent',
              '.day-number': {
                bgColor: 'var(--chakra-colors-baseWhite)',
              },
            },
            '&.react-datepicker__day--selected': {
              bgColor: 'baseWhite',
              '.day-number': {
                color: 'var(--chakra-colors-baseWhite)',
                bgColor: 'var(--chakra-colors-primary)',
                borderRadius: 'full',
              },
              '&:hover': {
                '.day-number': {
                  bgColor: 'var(--chakra-colors-primary)',
                  color: 'baseWhite',
                },
              },
            },
            '&.react-datepicker__day--outside-month': {
              pointerEvents: 'none',
              visibility: 'hidden',
              height: '1px',
            },
            '&.react-datepicker__day--range-end': {
              position: 'relative',
              '.day-number': {
                color: 'baseWhite',
                bgColor: 'primary',
                borderRadius: 'full',
                position: 'relative',
              },
              '&:hover': {
                '.day-number': {
                  bgColor: 'primary',
                },
              },
            },
            '&.react-datepicker__day--range-start': {
              position: 'relative',
              '.day-number': {
                color: 'baseWhite',
                bgColor: 'primary',
                borderRadius: 'full',
                position: 'relative',
              },
              '&:hover': {
                '.day-number': {
                  bgColor: 'primary',
                },
              },
            },
            '&.react-datepicker__day--in-range': {
              bgColor: 'transparent',
              '&:not(.react-datepicker__day--range-start)': {
                '.highlight': {
                  bgColor: 'rangeSelection',
                },
              },
              '&.end-week:not(.react-datepicker__day--range-end, .react-datepicker__day--selected)':
                {
                  '.day-number': {
                    bgColor: 'rangeSelection',
                  },
                },
              '&.end-month:not(.react-datepicker__day--range-end, .react-datepicker__day--selected)':
                {
                  '.day-number': {
                    bgColor: 'rangeSelection',
                  },
                },
              '&.start-week': {
                '.highlight': {
                  bgColor: 'transparent',
                },
                '&:not(.react-datepicker__day--range-start, .react-datepicker__day--range-end)': {
                  '.day-number': {
                    bgColor: 'rangeSelection',
                  },
                },
              },
              '&.start-month': {
                '.highlight': {
                  bgColor: 'transparent',
                },
                '&:not(.react-datepicker__day--range-start, .react-datepicker__day--range-end)': {
                  '.day-number': {
                    bgColor: 'rangeSelection',
                  },
                },
              },
              '&.react-datepicker__day--keyboard-selected:not(.react-datepicker__day--range-start, .react-datepicker__day--range-end)':
                {
                  '.day-number': {
                    bgColor: 'rangeSelection',
                  },
                },
            },
            '&.react-datepicker__day--in-selecting-range + .react-datepicker__day--in-selecting-range':
              {
                '.highlight': {
                  bgColor: 'rangeSelection',
                },
              },
            '&.react-datepicker__day--in-selecting-range': {
              '&.start-week:not(.react-datepicker__day--selected)': {
                '.highlight': {
                  bgColor: 'transparent',
                },
                '.day-number': {
                  bgColor: 'rangeSelection',
                },
              },
              '&.end-week:not(.react-datepicker__day--selected)': {
                '.day-number': {
                  bgColor: 'rangeSelection',
                },
              },
              '&.start-month:not(.react-datepicker__day--selected)': {
                '.highlight': {
                  bgColor: 'transparent',
                },
                '.day-number': {
                  bgColor: 'rangeSelection',
                },
              },
              '&.end-month:not(.react-datepicker__day--selected)': {
                '.day-number': {
                  bgColor: 'rangeSelection',
                },
              },
              '&.react-datepicker__day--keyboard-selected': {
                '.day-number': {
                  bgColor: 'rangeSelection',
                },
              },
            },
            '&.react-datepicker__day--in-selecting-range:not(.react-datepicker__day--in-range, .react-datepicker__month-text--in-range, .react-datepicker__quarter-text--in-range, .react-datepicker__year-text--in-range)':
              {
                bgColor: 'transparent',
              },
            '&.react-datepicker__day--in-selecting-range:not(.react-datepicker__day--selected)': {
              '.day-number': {
                color: 'darkGrey1',
                borderRadius: '0',
                borderColor: 'transparent',
                position: 'relative',
                '&:hover': {
                  borderRadius: '50%',
                  borderColor: 'primary',
                  bgColor: 'baseWhite',
                },
              },
              '&:hover': {
                bgColor: 'baseWhite',
                borderRadius: '50%',
                borderColor: 'primary',
              },
            },
            '&.react-datepicker__day--in-range:not(.react-datepicker__day--range-start, .react-datepicker__day--range-end)':
              {
                bgColor: 'transparent',
                position: 'relative',

                '.day-number': {
                  color: 'darkGrey1',
                  borderRadius: '0',
                  position: 'relative',
                },
              },
          },
        },
      },
      '.react-datepicker__current-month': {
        color: 'var(--chakra-colors-darkGrey1)',
        fontSize: 'var(--chakra-fontSizes-lg)',
        fontWeight: 'var(--chakra-fontWeights-semibold)',
        lineHeight: 'var(--chakra-lineHeights-3)',
        marginBottom: 'var(--chakra-space-sm)',
      },
      '.react-datepicker__day-names': {
        paddingTop: 'var(--chakra-space-sm)',
        display: 'flex',
        justifyContent: 'center',

        '.react-datepicker__day-name': {
          color: 'var(--chakra-colors-darkGrey2)',
          fontSize: 'var(--chakra-fontSizes-sm)',
          fontWeight: 'var(--chakra-fontWeights-medium)',
          lineHeight: 'var(--chakra-lineHeights-4)',
          border: '0.125rem solid transparent',
          textAlign: 'center',
        },
      },
    },
    '@media screen and (min-width: 320px)': {
      '.react-datepicker': {
        w: '20rem',

        '.react-datepicker__header': {
          paddingTop: 'lg',
          px: 'md',
        },
        '.react-datepicker__day-names': {
          '.react-datepicker__day-name': {
            w: 'var(--chakra-space-2xl)',
            h: 'var(--chakra-space-xl)',
            mx: '0',
          },
        },
        '.react-datepicker__month': {
          px: 'md',
          '.react-datepicker__week': {
            '.react-datepicker__day': {
              w: 'var(--chakra-space-2xl)',
              h: 'var(--chakra-space-2xl)',
              lineHeight: 'var(--chakra-lineHeights-4)',
              m: '0',
              '.highlight': {
                w: 'var(--chakra-space-2xl)',
                h: '2.25rem',
              },
              '.day-number': {
                px: 'var(--chakra-space-xs)',
                lineHeight: 'var(--chakra-lineHeights-3)',
                w: '2.25rem',
                h: '2.25rem',
              },
            },
          },
        },
      },
    },
    '@media screen and (min-width: 375px)': {
      '.react-datepicker': {
        w: '21.5rem',
        '.react-datepicker__header': {
          paddingTop: 'var(--chakra-space-xl)',
          px: 'md',
        },
        '.react-datepicker__day-names': {
          '.react-datepicker__day-name': {
            mx: '0',
            w: '2.75rem',
          },
        },
        '.react-datepicker__month': {
          px: 'md',
          '.react-datepicker__week': {
            '.react-datepicker__day': {
              margin: '0',
              width: '2.75rem',
              height: '2.75rem',
              lineHeight: 'var(--chakra-lineHeights-4)',
              '.highlight': {
                w: '2.75rem',
                h: '2.375rem',
              },
              '.day-number': {
                px: 'xs',
                lineHeight: 'var(--chakra-lineHeights-3)',
                w: '2.375rem',
                h: '2.375rem',
              },
            },
          },
        },
      },
    },
    '@media screen and (min-width: 576px)': {
      '.react-datepicker': {
        w: '23rem',

        '.react-datepicker__header': {
          px: 'var(--chakra-space-lg)',
        },
        '.react-datepicker__day-names': {
          '.react-datepicker__day-name': {
            w: '2.875rem',
            h: '2.125rem',
          },
        },
        '.react-datepicker__month': {
          px: 'var(--chakra-space-lg)',
          '.react-datepicker__week': {
            '.react-datepicker__day': {
              width: '2.875rem',
              height: '2.875rem',
              margin: '0',
              lineHeight: 'var(--chakra-lineHeights-4)',
              '.highlight': {
                w: '2.875rem',
                h: '2.5625rem',
              },
              '.day-number': {
                lineHeight: '1.6875rem',
                w: '2.5625rem',
                h: '2.5625rem',
              },
            },
          },
        },
      },
    },
    '.two-month-calendar': {
      '@media screen and (min-width: 1280px)': {
        '.react-datepicker': {
          width: '47rem',
          '.react-datepicker__month-container': {
            width: '50%',
          },
          '.chakra-button': {
            width: '20.25rem',
            height: '3.5rem',
            boxShadow: 'none',
            margin: '0 1.5rem',
          },
        },
      },
    },
  },
};

export default Datepicker;
