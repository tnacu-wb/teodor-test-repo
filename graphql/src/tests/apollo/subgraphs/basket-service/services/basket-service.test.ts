import { get, post, put } from '../../../../../apollo/client/rest-client';
import {
  backgroundCharge,
  confirmPreCheckIn,
  confirmPreCheckOut,
  getBasket,
  getBasketStatus,
  sendEmailOption,
  updateReservation
} from '../../../../../apollo/subgraphs/basket-service/services/basket-service';
import { endpoints } from '../../../../../apollo/subgraphs/basket-service/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getBasket', () => {
  const context = {};
  const basketEndPoint = {
    endpoint: '/v1/baskets/GAA-5fc37e9f-2be0-415b-9721-c99b77dcdf7c',
    flowCode: 'DIGITAL_BAS_001',
    axiosClient: expect.any(Function)
  };

  it('should call the get function with correct parameters when getBasket is called', async () => {
    await getBasket({ basketReference: 'GAA-5fc37e9f-2be0-415b-9721-c99b77dcdf7c' }, context);
    expect(get).toHaveBeenCalledWith(basketEndPoint, getBasket, null, context);
  });

  it('should handle errors gracefully when getBasket throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      getBasket({ basketReference: 'GAA-5fc37e9f-2be0-415b-9721-c99b77dcdf7c' }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('getBasketStatus', () => {
  const context = {};
  const basketEndPoint = {
    endpoint: '/v1/baskets/GAA-5fc37e9f-2be0-415b-9721-c99b77dcdf7c/checkStatus',
    flowCode: 'DIGITAL_BAS_002',
    axiosClient: expect.any(Function)
  };

  it('should call the get function with correct parameters when getBasketStatus is called', async () => {
    await getBasketStatus({ basketReference: 'GAA-5fc37e9f-2be0-415b-9721-c99b77dcdf7c' }, context);
    expect(get).toHaveBeenCalledWith(basketEndPoint, getBasketStatus, null, context);
  });

  it('should handle errors gracefully when getBasketStatus throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      getBasketStatus({ basketReference: 'GAA-5fc37e9f-2be0-415b-9721-c99b77dcdf7c' }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('confirmPreCheckin', () => {
  const context = {};
  const preCheckInEndPoint = {
    endpoint: '/v1/baskets/GAA-5fc37e9f-2be0-415b-9721-c99b77dcdf7c/preCheckIn',
    flowCode: 'DIGITAL_DRC_002',
    axiosClient: expect.any(Function)
  };

  it('should call the get function with correct parameters when confirmPreCheckIn is called', async () => {
    await confirmPreCheckIn(
      { basketReference: 'GAA-5fc37e9f-2be0-415b-9721-c99b77dcdf7c' },
      context
    );
    expect(put).toHaveBeenCalledWith(preCheckInEndPoint, confirmPreCheckIn, null, context);
  });

  it('should handle errors gracefully when confirmPreCheckIn throws an error', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      confirmPreCheckIn({ basketReference: 'GAA-5fc37e9f-2be0-415b-9721-c99b77dcdf7c' }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('sendEmailOption', () => {
  (put as jest.Mock).mockResolvedValue({
    data: {}
  });
  const context = {};
  const emailNotificationEndPoint = {
    endpoint: '/v1/baskets/GAA-5fc37e9f-2be0-415b-9721-c99b77dcdf7c/emailNotifications',
    flowCode: 'DIGITAL_GDE_003',
    axiosClient: expect.any(Function)
  };

  const sendEmailCriteria = {
    sendEmail: true
  };

  it('should call the get function with correct parameters when sendEmailOption is called', async () => {
    await sendEmailOption(
      {
        basketReference: 'GAA-5fc37e9f-2be0-415b-9721-c99b77dcdf7c',
        sendEmailCriteria: sendEmailCriteria
      },
      context
    );
    expect(put).toHaveBeenCalledWith(
      emailNotificationEndPoint,
      sendEmailOption,
      sendEmailCriteria,
      context
    );
  });

  it('should handle errors gracefully when sendEmailOption throws an error', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      sendEmailOption(
        { basketReference: 'GAA-5fc37e9f-2be0-415b-9721-c99b77dcdf7c', sendEmailCriteria },
        context
      )
    ).rejects.toThrow('Test error');
  });
});

describe('updateReservation', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });
  const context = {};
  const updateReservationCriteria = {
    hotelId: 'LONEUS',
    basketReference: 'basket-123',
    roomsSelections: 'room1',
    requestId: 'request1',
    reservationGuest: 'ReservationGuest1'
  };
  it('should call method with correct parameters when updateReservation is called', async () => {
    (put as jest.Mock).mockResolvedValue({
      data: {}
    });

    await updateReservation({ updateReservationCriteria: updateReservationCriteria }, context);

    expect(put).toHaveBeenCalledWith(
      endpoints.UPDATE_RESERVATION,
      updateReservation,
      updateReservationCriteria,
      context
    );
  });

  it('should handle errors when updateReservation throws an error', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValue(error);
    await expect(
      updateReservation({ updateReservationCriteria: updateReservationCriteria }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('confirmPreCheckOut', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });
  const context = {};
  const basketReference = 'BAS_123';

  it('should call put method with correct parameters when confirmPreCheckOut is called', async () => {
    (put as jest.Mock).mockResolvedValue({
      data: {}
    });
    await confirmPreCheckOut({ basketReference }, context);
    const serviceEndpoint = {
      ...endpoints.CONFIRM_PRE_CHECK_OUT,
      endpoint: '/v1/baskets/BAS_123/preCheckOut'
    };
    expect(put).toHaveBeenCalledWith(serviceEndpoint, confirmPreCheckOut, {}, context);
  });

  it('should handle errors when confirmPreCheckOut throws an error', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValue(error);
    await expect(confirmPreCheckOut({ basketReference }, context)).rejects.toThrow('Test error');
  });
});

describe('backgroundCharge', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  const context = {};
  const basketReference = 'BAS_123';
  const token = 'token_abc';

  it('should call post with the backgroundCharge request payload', async () => {
    (post as jest.Mock).mockResolvedValue({ data: { ok: true } });

    await backgroundCharge({ basketReference, token }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.BACKGROUND_CHARGE,
      backgroundCharge,
      { basketReference, token },
      context
    );
  });

  it('should return basketReference from service response when backgroundCharge succeeds', async () => {
    (post as jest.Mock).mockResolvedValue({ data: { basketReference } });

    const response = await backgroundCharge({ basketReference, token }, context);

    expect(response).toEqual({ basketReference });
  });

  it('should fallback to input basketReference when service response omits it', async () => {
    (post as jest.Mock).mockResolvedValue({ data: { ok: true } });

    const response = await backgroundCharge({ basketReference, token }, context);

    expect(response).toEqual({ basketReference });
  });

  it('should handle errors when backgroundCharge throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValue(error);

    await expect(backgroundCharge({ basketReference, token }, context)).rejects.toThrow(
      'Test error'
    );
  });
});
