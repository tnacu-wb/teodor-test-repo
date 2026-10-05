describe('getCostCentreDetails', () => {
  const mockExecuteGraphQLQuery = jest.fn();
  const mockGetCostCenterDetailsQuery = jest.fn();

  const token = 'test-token';
  const tetheredUserGuid = 'test-guid';
  const mockResult = { data: { getCostCentreDetails: { id: 'cc1', name: 'Test Centre' } } };

  beforeEach(() => {
    jest.resetModules();
    jest.clearAllMocks();

    jest.mock('@whitbread-eos/api', () => ({
      getCostCenterDetailsQuery: mockGetCostCenterDetailsQuery,
    }));

    jest.mock('../../../', () => ({
      executeGraphQLQuery: mockExecuteGraphQLQuery,
    }));
  });

  it('should call executeGraphQLQuery with correct arguments and return data', async () => {
    mockGetCostCenterDetailsQuery.mockReturnValue('query');
    mockExecuteGraphQLQuery.mockImplementation(async (_query, _variables, transform) =>
      transform(mockResult)
    );

    const getCostCentreDetails = (await import('./getCostCentreDetails')).default;

    const result = await getCostCentreDetails(token, tetheredUserGuid);

    expect(mockGetCostCenterDetailsQuery).toHaveBeenCalled();
    expect(mockExecuteGraphQLQuery).toHaveBeenCalledWith(
      'query',
      { tetheredUserGuid },
      expect.any(Function),
      token,
      true,
      false
    );
    expect(result).toEqual({ id: 'cc1', name: 'Test Centre' });
  });

  it('should propagate errors from executeGraphQLQuery', async () => {
    mockGetCostCenterDetailsQuery.mockReturnValue('query');
    mockExecuteGraphQLQuery.mockRejectedValue(new Error('GraphQL error'));

    const getCostCentreDetails = (await import('./getCostCentreDetails')).default;

    await expect(getCostCentreDetails(token, tetheredUserGuid)).rejects.toThrow('GraphQL error');
  });
});
