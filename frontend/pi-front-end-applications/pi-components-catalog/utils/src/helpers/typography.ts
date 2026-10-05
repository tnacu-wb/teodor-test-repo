type InputTypographyProps = {
  fontSize?: unknown;
  textStyle?: unknown;
  sx?: Record<string, any>;
};

type TextStyleTypographyProps = {
  fontFamily?: unknown;
  fontSize?: unknown;
  fontWeight?: unknown;
  letterSpacing?: unknown;
  lineHeight?: unknown;
  textStyle?: unknown;
  textTransform?: unknown;
};

type TypographyTextStyle = {
  fontFamily?: string | number;
  fontSize?: string | number;
  fontWeight?: string | number;
  letterSpacing?: string | number;
  lineHeight?: string | number;
  textTransform?: string;
};

type TextStyles = Record<string, TypographyTextStyle>;

const getTextStyleToken = (textStyle?: unknown) =>
  typeof textStyle === 'string' ? textStyle : undefined;

export const getTextStyleTypographyOverride = (
  typographyProps: TextStyleTypographyProps | undefined,
  textStyles?: TextStyles
) => {
  const textStyleToken = getTextStyleToken(typographyProps?.textStyle);
  const textStyle = textStyleToken ? textStyles?.[textStyleToken] : undefined;

  if (!textStyle) {
    return {};
  }

  return {
    fontFamily: typographyProps?.fontFamily ?? textStyle.fontFamily,
    fontSize: typographyProps?.fontSize ?? textStyle.fontSize,
    fontWeight: typographyProps?.fontWeight ?? textStyle.fontWeight,
    letterSpacing: typographyProps?.letterSpacing ?? textStyle.letterSpacing,
    lineHeight: typographyProps?.lineHeight ?? textStyle.lineHeight,
    textTransform: typographyProps?.textTransform ?? textStyle.textTransform,
  };
};

// Chakra inputs derive their rendered font size from the --input-font-size CSS variable.
// When a semantic textStyle supplies a fontSize, mirror it to both fontSize and that variable
// so it is not overwritten by Chakra's input size styles.
export const getInputTypographyOverride = (
  inputElementStyles: InputTypographyProps | undefined,
  textStyles?: Record<string, { fontSize?: string | number }>
) => {
  if (inputElementStyles?.fontSize) {
    return {};
  }

  const textStyleToken = getTextStyleToken(inputElementStyles?.textStyle);
  const semanticFontSize = textStyleToken ? textStyles?.[textStyleToken]?.fontSize : undefined;

  if (!semanticFontSize) {
    return {};
  }

  return {
    fontSize: semanticFontSize,
    sx: {
      ...inputElementStyles?.sx,
      '--input-font-size': semanticFontSize,
    },
  };
};
