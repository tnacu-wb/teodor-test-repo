import { renderHook } from '@testing-library/react';

import { useFilterPanelTypography } from './useFilterPanelTypography';

jest.mock('@whitbread-eos/utils', () => ({
  useSemanticTypography: () => (legacy) => legacy,
}));

describe('useFilterPanelTypography', () => {
  it('returns headingTypography, labelTypography and mergedButtonProps', () => {
    const { result } = renderHook(() => useFilterPanelTypography());

    expect(result.current).toMatchObject({
      headingTypography: { fontSize: 'md', fontWeight: 'bold', lineHeight: '3' },
      labelTypography: {},
      mergedButtonProps: {},
    });
  });

  it('merges buttonProps into mergedButtonProps', () => {
    const buttonProps = { size: 'sm' };
    const { result } = renderHook(() => useFilterPanelTypography(buttonProps));

    expect(result.current.mergedButtonProps).toMatchObject({ size: 'sm' });
  });

  it('buttonProps takes precedence over typography in mergedButtonProps', () => {
    const buttonProps = { fontSize: 'xl' };
    const { result } = renderHook(() => useFilterPanelTypography(buttonProps));

    expect(result.current.mergedButtonProps).toMatchObject({ fontSize: 'xl' });
  });
});
