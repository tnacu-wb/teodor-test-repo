import {
  mapFontFamily,
  mapFontWeight,
  mapTextTransform,
  semanticTextStyles,
  toLetterSpacing,
  toLineHeight,
} from './semanticTypography';

describe('semanticTypography adapter', () => {
  describe('semanticTextStyles output', () => {
    it('produces correct css styles for display-l-emphasis', () => {
      expect(semanticTextStyles['display-l-emphasis']).toEqual({
        fontFamily: 'SunsetSans, helvetica, arial, sans-serif',
        fontWeight: 900,
        fontSize: '40px',
        lineHeight: '1.2',
        letterSpacing: '0em',
        textTransform: 'none',
      });
    });

    it('produces correct css styles for heading-xl', () => {
      expect(semanticTextStyles['heading-xl']).toEqual({
        fontFamily: 'Proxima Nova Sans, helvetica, arial, sans-serif',
        fontWeight: 600,
        fontSize: '29px',
        lineHeight: '1.2',
        letterSpacing: '0em',
        textTransform: 'none',
      });
    });

    it('produces correct css styles for body-s-regular', () => {
      expect(semanticTextStyles['body-s-regular']).toEqual({
        fontFamily: 'Proxima Nova Sans, helvetica, arial, sans-serif',
        fontWeight: 400,
        fontSize: '14px',
        lineHeight: '1.4',
        letterSpacing: '0em',
        textTransform: 'none',
      });
    });

    it('produces no percentage letterSpacing values across all aliases', () => {
      Object.values(semanticTextStyles).forEach((style) => {
        expect(style.letterSpacing?.includes('%')).toBe(false);
      });
    });
  });

  describe('mapFontWeight', () => {
    it('returns a numeric weight unchanged', () => {
      expect(mapFontWeight(700)).toBe(700);
    });

    it('maps Black to 900', () => expect(mapFontWeight('Black')).toBe(900));
    it('maps ExtraBold to 800', () => expect(mapFontWeight('ExtraBold')).toBe(800));
    it('maps Bold to 700', () => expect(mapFontWeight('Bold')).toBe(700));
    it('maps Semibold to 600', () => expect(mapFontWeight('Semibold')).toBe(600));
    it('maps Medium to 500', () => expect(mapFontWeight('Medium')).toBe(500));
    it('maps Regular to 400', () => expect(mapFontWeight('Regular')).toBe(400));
    it('maps Light to 300', () => expect(mapFontWeight('Light')).toBe(300));
    it('maps Thin to 200', () => expect(mapFontWeight('Thin')).toBe(200));
    it('maps Hairline to 100', () => expect(mapFontWeight('Hairline')).toBe(100));
    it('falls back to 400 for an unrecognised string', () =>
      expect(mapFontWeight('Unknown')).toBe(400));

    // integration: aliases that appear in the design tokens
    it('maps Black alias in semanticTextStyles', () =>
      expect(semanticTextStyles['display-l-emphasis'].fontWeight).toBe(900));
    it('maps Semibold alias in semanticTextStyles', () =>
      expect(semanticTextStyles['heading-xl'].fontWeight).toBe(600));
    it('maps Regular alias in semanticTextStyles', () =>
      expect(semanticTextStyles['body-s-regular'].fontWeight).toBe(400));
  });

  describe('mapFontFamily', () => {
    it('uses only the first provided font and appends fallback fonts', () => {
      expect(mapFontFamily('SunsetSans, serif')).toBe('SunsetSans, helvetica, arial, sans-serif');
    });

    it('maps Proxima Nova to default font family stack', () => {
      expect(mapFontFamily('Proxima Nova')).toBe('Proxima Nova Sans, helvetica, arial, sans-serif');
    });

    it('returns default font family when input is empty', () => {
      expect(mapFontFamily('')).toBe('Proxima Nova Sans, helvetica, arial, sans-serif');
    });
  });

  describe('toLineHeight', () => {
    it('converts PERCENT unit: divides by 100 (unitless)', () => {
      expect(toLineHeight({ unit: 'PERCENT', value: 120 })).toBe('1.2');
      expect(toLineHeight({ unit: 'PERCENT', value: 140 })).toBe('1.4');
      expect(toLineHeight({ unit: 'PERCENT', value: 100 })).toBe('1');
    });

    it('converts em unit: passes value through with em suffix', () => {
      expect(toLineHeight({ unit: 'em', value: 1.5 })).toBe('1.5em');
    });

    it('converts rem unit: passes value through with rem suffix', () => {
      expect(toLineHeight({ unit: 'rem', value: 2 })).toBe('2rem');
    });

    it('converts px unit: appends px', () => {
      expect(toLineHeight({ unit: 'px', value: 24 })).toBe('24px');
    });

    it('converts pixel unit: appends px', () => {
      expect(toLineHeight({ unit: 'pixel', value: 32 })).toBe('32px');
    });

    it('falls back to px for an unrecognised unit', () => {
      expect(toLineHeight({ unit: 'vw', value: 3 })).toBe('3px');
    });

    it('returns normal when token has no numeric value', () => {
      expect(toLineHeight({ unit: 'PERCENT' })).toBe('normal');
      expect(toLineHeight({})).toBe('normal');
    });

    it('passes through a plain number as px', () => {
      expect(toLineHeight(24)).toBe('24px');
    });

    it('passes through a plain string unchanged', () => {
      expect(toLineHeight('normal')).toBe('normal');
      expect(toLineHeight('1.5')).toBe('1.5');
    });

    // integration: PERCENT unit comes from the design tokens
    it('converts PERCENT in semanticTextStyles aliases', () => {
      expect(semanticTextStyles['display-l-emphasis'].lineHeight).toBe('1.2');
      expect(semanticTextStyles['body-s-regular'].lineHeight).toBe('1.4');
    });
  });

  describe('toLetterSpacing', () => {
    it('returns undefined when token is undefined', () => {
      expect(toLetterSpacing(undefined)).toBeUndefined();
    });

    it('returns undefined when token has no numeric value', () => {
      expect(toLetterSpacing({ unit: 'PERCENT' })).toBeUndefined();
    });

    it('converts PERCENT unit: divides by 100 and appends em', () => {
      expect(toLetterSpacing({ unit: 'PERCENT', value: 0 })).toBe('0em');
      expect(toLetterSpacing({ unit: 'PERCENT', value: 50 })).toBe('0.5em');
    });

    it('converts em unit: passes value through with em suffix', () => {
      expect(toLetterSpacing({ unit: 'em', value: 0.05 })).toBe('0.05em');
    });

    it('converts rem unit: passes value through with rem suffix', () => {
      expect(toLetterSpacing({ unit: 'rem', value: 1 })).toBe('1rem');
    });

    it('converts px unit: appends px', () => {
      expect(toLetterSpacing({ unit: 'px', value: 2 })).toBe('2px');
    });

    it('converts pixel unit: appends px', () => {
      expect(toLetterSpacing({ unit: 'pixel', value: 4 })).toBe('4px');
    });

    it('falls back to px for an unrecognised unit', () => {
      expect(toLetterSpacing({ unit: 'vw', value: 3 })).toBe('3px');
    });

    it('falls back to px when unit is absent', () => {
      expect(toLetterSpacing({ value: 1 })).toBe('1px');
    });

    // integration: PERCENT unit comes from the design tokens
    it('converts PERCENT in semanticTextStyles aliases', () => {
      expect(semanticTextStyles['display-l-emphasis'].letterSpacing).toBe('0em');
    });
  });

  describe('mapTextTransform', () => {
    it('maps ORIGINAL to none', () => expect(mapTextTransform('ORIGINAL')).toBe('none'));
    it('maps original (lowercase) to none', () =>
      expect(mapTextTransform('original')).toBe('none'));
    it('maps uppercase to uppercase', () =>
      expect(mapTextTransform('uppercase')).toBe('uppercase'));
    it('maps UPPERCASE to uppercase', () =>
      expect(mapTextTransform('UPPERCASE')).toBe('uppercase'));
    it('maps lowercase to lowercase', () =>
      expect(mapTextTransform('lowercase')).toBe('lowercase'));
    it('maps capitalize to capitalize', () =>
      expect(mapTextTransform('capitalize')).toBe('capitalize'));
    it('defaults to none when called with undefined', () =>
      expect(mapTextTransform(undefined)).toBe('none'));
    it('defaults to none for an unrecognised value', () =>
      expect(mapTextTransform('smallcaps')).toBe('none'));

    // integration: all current design token aliases use ORIGINAL
    it('maps ORIGINAL in semanticTextStyles aliases', () => {
      expect(semanticTextStyles['display-l-emphasis'].textTransform).toBe('none');
    });
  });
});
