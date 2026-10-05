type ThemeWithSemanticTextStyles = {
  [key: string]: unknown;
  breakpoints?: Record<string, string>;
  textStyles?: Record<string, Record<string, string | number>>;
};

type SemanticTextStyleByBreakpoint = {
  base?: string;
  mobile?: string;
  xs?: string;
  sm?: string;
  md?: string;
  lg?: string;
  xl?: string;
};

const BREAKPOINT_ORDER: Array<keyof Omit<SemanticTextStyleByBreakpoint, 'base'>> = [
  'mobile',
  'xs',
  'sm',
  'md',
  'lg',
  'xl',
];

export const getResponsiveSemanticTextStyles = (
  theme: ThemeWithSemanticTextStyles,
  semanticAliases: SemanticTextStyleByBreakpoint
) => {
  const textStyles = theme.textStyles ?? {};
  const breakpoints = theme.breakpoints ?? {};
  const styles: Record<string, string | number | Record<string, string | number>> = {};

  if (semanticAliases.base) {
    Object.assign(styles, textStyles[semanticAliases.base] ?? {});
  }

  BREAKPOINT_ORDER.forEach((breakpoint) => {
    const alias = semanticAliases[breakpoint];
    const minWidth = breakpoints[breakpoint];

    if (!alias || !minWidth) {
      return;
    }

    styles[`@media screen and (min-width: ${minWidth})`] = textStyles[alias] ?? {};
  });

  return styles;
};
