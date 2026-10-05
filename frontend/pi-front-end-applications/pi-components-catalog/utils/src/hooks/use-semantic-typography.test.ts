import { renderHook } from '@testing-library/react';
import { FT_SEMANTIC_TYPOGRAPHY_TOKENIZATION } from '@whitbread-eos/api';

import useSemanticTypography from './use-semantic-typography';

const mockUseFeatureToggle = jest.fn();

jest.mock('./use-feature-toggle', () => ({
  __esModule: true,
  default: () => mockUseFeatureToggle(),
}));

describe('useSemanticTypography', () => {
  const legacyTypography = {
    fontSize: 'md',
    fontWeight: 'semibold',
    lineHeight: '3',
  };

  const semanticTypography = {
    textStyle: 'body-m-regular',
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('returns legacy typography when semantic typography flag is disabled', () => {
    mockUseFeatureToggle.mockReturnValue({
      [FT_SEMANTIC_TYPOGRAPHY_TOKENIZATION]: false,
    });

    const { result } = renderHook(() => useSemanticTypography());

    expect(result.current(legacyTypography, semanticTypography)).toEqual(legacyTypography);
  });

  it('returns semantic typography when semantic typography flag is enabled', () => {
    mockUseFeatureToggle.mockReturnValue({
      [FT_SEMANTIC_TYPOGRAPHY_TOKENIZATION]: true,
    });

    const { result } = renderHook(() => useSemanticTypography());

    expect(result.current(legacyTypography, semanticTypography)).toEqual(semanticTypography);
  });

  it('returns legacy typography when semantic typography is enabled but textStyle is missing', () => {
    mockUseFeatureToggle.mockReturnValue({
      [FT_SEMANTIC_TYPOGRAPHY_TOKENIZATION]: true,
    });

    const { result } = renderHook(() => useSemanticTypography());

    expect(result.current(legacyTypography, {})).toEqual(legacyTypography);
  });
});
