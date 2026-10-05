import { get } from '../../../../../apollo/client/rest-client';
import {
  getAccountInfo,
  getNotifications,
  getNotificationsV2
} from '../../../../../apollo/subgraphs/hotel-account-service-opera/services/inn-business-service';
import { endpoints } from '../../../../../apollo/subgraphs/hotel-account-service-opera/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getNotifications', () => {
  const context = {};
  const input = {
    tetheredUserId: 'tetheruserId',
    scheme: 'GB'
  };

  it('should call the get function with correct parameters when getNotifications is called', async () => {
    await getNotifications(input, context);
    expect(get).toHaveBeenCalledWith(endpoints.GET_NOTIFICATIONS, getNotifications, input, context);
  });

  it('should handle errors gracefully when getNotifications throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(getNotifications(input, context)).rejects.toThrow('Test error');
  });
});

describe('getNotificationsV2', () => {
  const context = {};

  it('should call the get function with correct parameters when getNotificationsV2 is called', async () => {
    await getNotificationsV2({}, context);
    expect(get).toHaveBeenCalledWith(endpoints.GET_NOTIFICATIONS, getNotifications, {}, context);
  });

  it('should handle errors gracefully when getNotificationsV2 throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(getNotificationsV2({}, context)).rejects.toThrow('Test error');
  });
});

describe('getAccountInfo', () => {
  const context = {};
  const input = {
    tetheredUserId: 'tetherUserId',
    scheme: 'GB'
  };

  it('should call the get function with correct parameters when getAccountInfo is called', async () => {
    await getAccountInfo(input, context);
    expect(get).toHaveBeenCalledWith(endpoints.GET_ACCOUNT_INFO, getAccountInfo, input, context);
  });

  it('should handle errors gracefully when getAccountInfo throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(getAccountInfo(input, context)).rejects.toThrow('Test error');
  });
});
