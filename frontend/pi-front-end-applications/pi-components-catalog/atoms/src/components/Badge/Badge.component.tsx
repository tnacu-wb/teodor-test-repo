import { Badge as ChakraBadge, BadgeProps } from '@chakra-ui/react';
import { useSemanticTypography } from '@whitbread-eos/utils';

import { semanticTextStyles } from '../../theme/adapters/semanticTypography';

export type BadgeVariant = 'primary' | 'secondary' | 'ZIP' | 'hub' | 'square' | 'solid';

type BadgeLegacyTypographyProps = Pick<
  BadgeProps,
  'fontSize' | 'fontWeight' | 'lineHeight' | 'letterSpacing' | 'fontFamily' | 'textTransform'
>;
type BadgeSemanticTypographyProps = Pick<BadgeProps, 'textStyle'>;

const defaultBadgeSemanticTypography: BadgeSemanticTypographyProps = {
  textStyle: 'label-s',
};

interface Props extends BadgeProps {
  variant: BadgeVariant;
  badgecolor?: string;
  badgeLegacyTypography?: BadgeLegacyTypographyProps;
  badgeSemanticTypography?: BadgeSemanticTypographyProps;
}

export default function Badge({
  variant,
  textStyle,
  fontSize,
  fontWeight,
  lineHeight,
  letterSpacing,
  fontFamily,
  textTransform,
  badgeLegacyTypography,
  badgeSemanticTypography,
  ...otherProps
}: Readonly<Props>) {
  const getTypographyProps = useSemanticTypography();

  const badgeInlineLegacyTypography: BadgeLegacyTypographyProps = {
    fontSize,
    fontWeight,
    lineHeight,
    letterSpacing,
    fontFamily,
    textTransform,
  };

  const resolvedBadgeLegacyTypography = badgeLegacyTypography ?? badgeInlineLegacyTypography;
  const resolvedBadgeSemanticTypography =
    badgeSemanticTypography ?? (textStyle ? { textStyle } : defaultBadgeSemanticTypography);

  const typographyProps = getTypographyProps(
    resolvedBadgeLegacyTypography,
    resolvedBadgeSemanticTypography
  );

  // Chakra's Badge built-in baseStyle sets fontSize:'xs' which wins over both textStyle prop
  // expansion and sx.textStyle (Chakra's css() in sx does not expand textStyle shorthands).
  // Resolving the semantic token to its actual CSS properties and spreading them as direct
  // inline props is the only approach that reliably overrides the component baseStyle fontSize.
  const isSemanticMode = 'textStyle' in typographyProps;
  const resolvedTypographyStyles = isSemanticMode
    ? (semanticTextStyles[(typographyProps as BadgeSemanticTypographyProps).textStyle as string] ??
      {})
    : typographyProps;

  if (variant === 'ZIP') {
    return (
      <ChakraBadge variant="ZIP" {...otherProps} {...resolvedTypographyStyles}>
        ZIP
      </ChakraBadge>
    );
  }
  if (variant === 'hub') {
    return (
      <ChakraBadge variant="hub" {...otherProps} {...resolvedTypographyStyles}>
        hub
      </ChakraBadge>
    );
  }

  return <ChakraBadge variant={variant} {...otherProps} {...resolvedTypographyStyles} />;
}
