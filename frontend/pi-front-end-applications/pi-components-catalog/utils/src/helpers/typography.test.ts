import { getInputTypographyOverride, getTextStyleTypographyOverride } from './typography';

describe('getTextStyleTypographyOverride', () => {
  const textStyles = {
    'label-xl': {
      fontFamily: 'Proxima Nova Sans, helvetica, arial, sans-serif',
      fontSize: '38px',
      fontWeight: 600,
      letterSpacing: '0em',
      lineHeight: '41.6px',
      textTransform: 'none',
    },
  };

  it('returns typography props from a semantic textStyle', () => {
    expect(getTextStyleTypographyOverride({ textStyle: 'label-xl' }, textStyles)).toEqual(
      textStyles['label-xl']
    );
  });

  it('does not override explicit typography props', () => {
    expect(
      getTextStyleTypographyOverride(
        { textStyle: 'label-xl', fontSize: '16px', lineHeight: '20px' },
        textStyles
      )
    ).toEqual({
      ...textStyles['label-xl'],
      fontSize: '16px',
      lineHeight: '20px',
    });
  });

  it('returns no override when textStyle cannot be resolved', () => {
    expect(getTextStyleTypographyOverride({ textStyle: 'unknown-style' }, textStyles)).toEqual({});
    expect(getTextStyleTypographyOverride({ textStyle: ['label-xl'] }, textStyles)).toEqual({});
    expect(getTextStyleTypographyOverride({ textStyle: 'label-xl' }, undefined)).toEqual({});
  });
});

describe('getInputTypographyOverride', () => {
  const textStyles = {
    'body-regular': {
      fontSize: '16px',
    },
    'body-large': {
      fontSize: 18,
    },
  };

  it('returns no override when input styles already include fontSize', () => {
    const result = getInputTypographyOverride(
      { fontSize: '14px', textStyle: 'body-regular' },
      textStyles
    );

    expect(result).toEqual({});
  });

  it('returns no override when textStyle does not resolve to a fontSize', () => {
    expect(getInputTypographyOverride({ textStyle: 'unknown-style' }, textStyles)).toEqual({});
    expect(getInputTypographyOverride({ textStyle: 'body-regular' }, undefined)).toEqual({});
    expect(getInputTypographyOverride({ textStyle: ['body-regular'] }, textStyles)).toEqual({});
  });

  it('sets fontSize and input CSS variable from semantic textStyle', () => {
    const result = getInputTypographyOverride(
      {
        textStyle: 'body-regular',
        sx: {
          color: 'red',
        },
      },
      textStyles
    );

    expect(result).toEqual({
      fontSize: '16px',
      sx: {
        color: 'red',
        '--input-font-size': '16px',
      },
    });
  });

  it('supports numeric semantic font sizes', () => {
    const result = getInputTypographyOverride({ textStyle: 'body-large' }, textStyles);

    expect(result).toEqual({
      fontSize: 18,
      sx: {
        '--input-font-size': 18,
      },
    });
  });
});
