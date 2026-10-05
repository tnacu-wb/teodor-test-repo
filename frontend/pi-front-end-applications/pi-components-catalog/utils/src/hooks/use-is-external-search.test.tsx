import useIsExternalSearch from './use-is-external-search';

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

describe('useIsExternalSearch', () => {
  it('should return false when no CID query param', () => {
    const isExternalSearch = useIsExternalSearch('TRA,TRIVAGO,GHF,BMP');
    expect(isExternalSearch).toEqual(false);
  });

  it('should return false when third parties sent from AEM', () => {
    mockUseRouter.mockReturnValue({
      query: { CID: 'test_CID' },
    });

    const isExternalSearch = useIsExternalSearch('');
    expect(isExternalSearch).toEqual(false);
  });

  it("should return false when CID doesn't match third parties sent from AEM", () => {
    mockUseRouter.mockReturnValue({
      query: { CID: 'CID_test' },
    });

    const isExternalSearch = useIsExternalSearch('TRA,TRIVAGO,GHF,BMP');
    expect(isExternalSearch).toEqual(false);
  });

  it('should return true when CID matches third parties sent from AEM', () => {
    mockUseRouter.mockReturnValue({
      query: { CID: 'TRIVAGO_test' },
    });

    const isExternalSearch = useIsExternalSearch('TRA,TRIVAGO,GHF,BMP');
    expect(isExternalSearch).toEqual(true);
  });

  it('should return false when AEM value is empty space string', () => {
    mockUseRouter.mockReturnValue({
      query: { CID: ' ' },
    });

    const isExternalSearch = useIsExternalSearch(' ');
    expect(isExternalSearch).toEqual(false);
  });

  it('should return false when CID is empty string', () => {
    mockUseRouter.mockReturnValue({
      query: { CID: '' },
    });

    const isExternalSearch = useIsExternalSearch(' ');
    expect(isExternalSearch).toEqual(false);
  });
});
