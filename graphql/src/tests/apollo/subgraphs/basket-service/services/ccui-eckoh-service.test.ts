import { get } from '../../../../../apollo/client/rest-client';
import { getEckohRecordingStatus } from '../../../../../apollo/subgraphs/basket-service/services/ccui-eckoh-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getEckohRecordingStatus', () => {
  const context = {};
  const eckohRecordingStatusEndPoint = {
    endpoint: '/v1/baskets/ccui/GAA-5fc37e9f-2be0-415b-9721-c88b77dcdf7c/eckoh',
    flowCode: 'DIGITAL_PAY_007',
    axiosClient: expect.any(Function)
  };

  it('should call the get function with correct parameters when getEckohRecordingStatus is called', async () => {
    await getEckohRecordingStatus(
      { basketReference: 'GAA-5fc37e9f-2be0-415b-9721-c88b77dcdf7c' },
      context
    );
    expect(get).toHaveBeenCalledWith(
      eckohRecordingStatusEndPoint,
      getEckohRecordingStatus,
      null,
      context
    );
  });

  it('should handle errors gracefully when getEckohRecordingStatus throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      getEckohRecordingStatus(
        { basketReference: 'GAA-5fc37e9f-2be0-415b-9721-c88b88dcdf7c' },
        context
      )
    ).rejects.toThrow('Test error');
  });
});
