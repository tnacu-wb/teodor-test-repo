import type { TextProps } from '@chakra-ui/react';
import { FT_SEMANTIC_TYPOGRAPHY_TOKENIZATION } from '@whitbread-eos/api';

import useFeatureToggle from './use-feature-toggle';

type LegacyTypographyKeys =
  | 'fontSize'
  | 'fontWeight'
  | 'lineHeight'
  | 'letterSpacing'
  | 'fontFamily'
  | 'textTransform';

export type LegacyTypographyProps = Partial<Pick<TextProps, LegacyTypographyKeys>>;
export type SemanticTypographyProps = Pick<TextProps, 'textStyle'>;

/** Temporary migration helper for semantic typography rollout behind feature toggle. */
export default function useSemanticTypography() {
  const { [FT_SEMANTIC_TYPOGRAPHY_TOKENIZATION]: isSemanticTypographyEnabled } = useFeatureToggle();

  return (
    legacyTypography: LegacyTypographyProps,
    semanticTypography: SemanticTypographyProps
  ): LegacyTypographyProps | SemanticTypographyProps => {
    return isSemanticTypographyEnabled && semanticTypography.textStyle
      ? semanticTypography
      : legacyTypography;
  };
}
