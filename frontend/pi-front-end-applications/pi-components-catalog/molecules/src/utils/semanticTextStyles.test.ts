import { getResponsiveSemanticTextStyles } from './semanticTextStyles';

describe('getResponsiveSemanticTextStyles', () => {
  it('merges base semantic styles and adds breakpoint overrides', () => {
    const theme = {
      breakpoints: {
        mobile: '320px',
        lg: '1280px',
      },
      textStyles: {
        'heading-m': {
          fontFamily: 'Proxima Nova Sans',
          fontWeight: 600,
          fontSize: '23px',
          lineHeight: '27.6px',
        },
        'heading-xl': {
          fontFamily: 'Proxima Nova Sans',
          fontWeight: 600,
          fontSize: '29px',
          lineHeight: '34.8px',
        },
      },
    };

    const result = getResponsiveSemanticTextStyles(theme, {
      base: 'heading-m',
      lg: 'heading-xl',
    });

    expect(result).toEqual({
      fontFamily: 'Proxima Nova Sans',
      fontWeight: 600,
      fontSize: '23px',
      lineHeight: '27.6px',
      '@media screen and (min-width: 1280px)': {
        fontFamily: 'Proxima Nova Sans',
        fontWeight: 600,
        fontSize: '29px',
        lineHeight: '34.8px',
      },
    });
  });

  it('skips breakpoints when alias or breakpoint is missing', () => {
    const theme = {
      breakpoints: {
        lg: '1280px',
      },
      textStyles: {
        'heading-xl': {
          fontSize: '29px',
        },
      },
    };

    const result = getResponsiveSemanticTextStyles(theme, {
      md: 'heading-m',
      lg: 'heading-xl',
    });

    expect(result).toEqual({
      '@media screen and (min-width: 1280px)': {
        fontSize: '29px',
      },
    });
  });

  it('returns an empty object when theme does not contain semantic text styles', () => {
    const result = getResponsiveSemanticTextStyles({}, { base: 'heading-m', lg: 'heading-xl' });
    expect(result).toEqual({});
  });
});
