import typographySemantic from '../design-tokens/typography-semantic.json';

type LetterSpacingToken = {
  unit?: string;
  value?: number;
};

type LineHeightToken = {
  unit?: string;
  value?: number;
};

type SemanticTypographyToken = {
  fontFamily: string;
  fontWeight: string | number;
  fontSize: string | number;
  lineHeight: string | number | LineHeightToken;
  letterSpacing?: LetterSpacingToken;
  textCase?: string;
};

type TextStyle = {
  fontFamily: string;
  fontWeight: number;
  fontSize: string;
  lineHeight: string;
  letterSpacing?: string;
  textTransform: 'none' | 'uppercase' | 'lowercase' | 'capitalize';
};

const FONT_WEIGHT_MAP: Record<string, number> = {
  Black: 900,
  ExtraBold: 800,
  Bold: 700,
  Semibold: 600,
  Medium: 500,
  Regular: 400,
  Light: 300,
  Thin: 200,
  Hairline: 100,
};

const GENERIC_FALLBACK_STACK = 'helvetica, arial, sans-serif';
const DEFAULT_FONT_FAMILY = `Proxima Nova Sans, ${GENERIC_FALLBACK_STACK}`;

const toPx = (value: string | number): string => (typeof value === 'number' ? `${value}px` : value);

export const toLineHeight = (token: string | number | LineHeightToken): string => {
  if (typeof token === 'string' || typeof token === 'number') {
    return toPx(token);
  }

  if (typeof token.value !== 'number') {
    return 'normal';
  }

  const unit = token.unit?.toLowerCase();

  switch (unit) {
    case 'percent':
      return `${token.value / 100}`;
    case 'em':
    case 'rem':
      return `${token.value}${unit}`;
    case 'px':
    case 'pixel':
    default:
      return `${token.value}px`;
  }
};

export const toLetterSpacing = (token?: LetterSpacingToken): string | undefined => {
  if (!token || typeof token.value !== 'number') {
    return undefined;
  }

  const unit = token.unit?.toLowerCase();

  switch (unit) {
    case 'percent':
      return `${token.value / 100}em`;
    case 'em':
    case 'rem':
      return `${token.value}${unit}`;
    case 'px':
    case 'pixel':
    default:
      return `${token.value}px`;
  }
};

export const mapFontWeight = (fontWeight: string | number): number => {
  if (typeof fontWeight === 'number') {
    return fontWeight;
  }
  return FONT_WEIGHT_MAP[fontWeight] ?? 400;
};

export const mapFontFamily = (fontFamily: string): string => {
  const primaryFont = fontFamily.split(',')[0]?.trim();

  if (!primaryFont || primaryFont.toLowerCase() === 'proxima nova') {
    return DEFAULT_FONT_FAMILY;
  }

  return `${primaryFont}, ${GENERIC_FALLBACK_STACK}`;
};

export const mapTextTransform = (
  textCase?: string
): 'none' | 'uppercase' | 'lowercase' | 'capitalize' => {
  const normalizedTextCase = textCase?.toLowerCase();

  switch (normalizedTextCase) {
    case 'uppercase':
      return 'uppercase';
    case 'lowercase':
      return 'lowercase';
    case 'capitalize':
      return 'capitalize';
    case 'original':
    default:
      return 'none';
  }
};

const aliases = typographySemantic.typography as Record<string, SemanticTypographyToken>;

export const semanticTextStyles: Record<string, TextStyle> = Object.entries(aliases).reduce(
  (accumulator, [alias, token]) => {
    accumulator[alias] = {
      fontFamily: mapFontFamily(token.fontFamily),
      fontWeight: mapFontWeight(token.fontWeight),
      fontSize: toPx(token.fontSize),
      lineHeight: toLineHeight(token.lineHeight),
      letterSpacing: toLetterSpacing(token.letterSpacing),
      textTransform: mapTextTransform(token.textCase),
    };

    return accumulator;
  },
  {} as Record<string, TextStyle>
);
