import { get, put } from '../../../../../apollo/client/rest-client';
import { endpoints } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/services/base-service';
import { Channel } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/models/channel';
import {
  confirmAmend,
  confirmAmendLogic,
  getAmendConfirmationPrices,
  getAmendPaymentOptions,
  getAmendSummary
} from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/services/amend-service';
import { ConfirmAmendLogicCriteria } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/models/confirm-amend-logic-criteria';

jest.mock('../../../../../apollo/client/rest-client');

describe('getManageBooking', () => {
  const context = {};
  afterEach(() => {
    jest.resetAllMocks();
  });

  it('should call get with correct parameters when getAmendSummary is called', async () => {
    await getAmendSummary(
      {
        originalBasketRef: 'Original',
        copyBasketRef: 'Copy',
        token: 'token123',
        bookingChannel: {
          channel: Channel.PI,
          subchannel: 'WEB',
          language: 'en'
        },
        country: 'UK'
      },
      context
    );

    expect(get).toHaveBeenCalledWith(
      endpoints.AMEND_SUMMARY,
      getAmendSummary,
      {
        originalBasketRef: 'Original',
        copyBasketRef: 'Copy',
        token: 'token123',
        'bookingChannel.channel': Channel.PI,
        'bookingChannel.subchannel': 'WEB',
        'bookingChannel.language': 'en',
        country: 'UK'
      },
      context
    );
  });

  it('should call get without optional parameters when getAmendSummary is called', async () => {
    await getAmendSummary(
      {
        originalBasketRef: 'Original',
        copyBasketRef: 'Copy',
        bookingChannel: {
          channel: Channel.PI,
          subchannel: 'WEB',
          language: 'en'
        },
        country: 'UK'
      },
      context
    );

    expect(get).toHaveBeenCalledWith(
      endpoints.AMEND_SUMMARY,
      getAmendSummary,
      {
        originalBasketRef: 'Original',
        copyBasketRef: 'Copy',
        'bookingChannel.channel': Channel.PI,
        'bookingChannel.subchannel': 'WEB',
        'bookingChannel.language': 'en',
        country: 'UK'
      },
      context
    );
  });

  it('should handle errors correctly when getAmendSummary fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValue(error);

    await expect(
      getAmendSummary(
        {
          originalBasketRef: 'Original',
          copyBasketRef: 'Copy',
          token: 'token123',
          bookingChannel: {
            channel: Channel.PI,
            subchannel: 'WEB',
            language: 'en'
          },
          country: 'UK'
        },
        context
      )
    ).rejects.toThrow('Test error');
  });
});

describe('getAmendConfirmationPrices', () => {
  const context = {};
  afterEach(() => {
    jest.resetAllMocks();
  });

  it('should call get with correct parameters when getAmendConfirmationPrices is called', async () => {
    const amendConfirmationPricesRequest = {
      tempBookingRef: 'Temp',
      originalBookingRef: 'Original',
      token: 'token123'
    };
    await getAmendConfirmationPrices(
      { amendConfirmationPricesRequest: amendConfirmationPricesRequest },
      context
    );

    expect(get).toHaveBeenCalledWith(
      endpoints.AMEND_CONFIRMATION_PRICES,
      getAmendConfirmationPrices,
      {
        tempBookingRef: 'Temp',
        originalBookingRef: 'Original',
        token: 'token123'
      },
      context
    );
  });

  it('should handle errors correctly when getAmendConfirmationPrices fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValue(error);
    const criteria = {
      tempBookingRef: 'Temp',
      originalBookingRef: 'Original',
      token: 'token123'
    };
    await expect(
      getAmendConfirmationPrices({ amendConfirmationPricesRequest: criteria }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('getAmendPaymentOptions', () => {
  const context = {};
  afterEach(() => {
    jest.resetAllMocks();
  });

  const paymentOptionsCriteria = {
    originalBookingRef: 'Original',
    tempBookingRef: 'Copy',
    token: 'token123',
    bookingChannel: {
      channel: Channel.PI,
      subchannel: 'WEB',
      language: 'en'
    },
    country: 'UK'
  };

  it('should call get with correct parameters when getAmendPaymentOptions is called', async () => {
    await getAmendPaymentOptions({ paymentOptionsCriteria: paymentOptionsCriteria }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.AMEND_PAYMENT_OPTIONS,
      getAmendPaymentOptions,
      {
        originalBookingRef: 'Original',
        tempBookingRef: 'Copy',
        token: 'token123',
        'bookingChannel.channel': Channel.PI,
        'bookingChannel.subchannel': 'WEB',
        'bookingChannel.language': 'en',
        country: 'UK'
      },
      context
    );
  });

  it('should handle errors correctly when getAmendPaymentOptions fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValue(error);

    await expect(
      getAmendPaymentOptions({ paymentOptionsCriteria: paymentOptionsCriteria }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('confirmAmend', () => {
  const context = {};
  afterEach(() => {
    jest.resetAllMocks();
  });

  const confirmAmendCriteria = {
    bookingChannel: {
      channel: Channel.PI,
      subchannel: 'WEB'
    },
    originalBookingRef: 'Original',
    tempBookingRef: 'Copy',
    token: 'token123',
    emailAddress: 'test@email.com'
  };

  it('should call put with correct parameters when confirmAmend is called', async () => {
    (put as jest.Mock).mockResolvedValue({
      data: {}
    });
    await confirmAmend({ confirmAmendCriteria }, context);

    expect(put).toHaveBeenCalledWith(
      endpoints.CONFIRM_AMEND,
      confirmAmend,
      {
        bookingChannel: {
          channel: Channel.PI,
          subchannel: 'WEB'
        },
        originalBookingRef: 'Original',
        tempBookingRef: 'Copy',
        token: 'token123',
        emailAddress: 'test@email.com'
      },
      context
    );
  });

  it('should handle errors correctly when confirmAmend fails', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValue(error);
    await expect(confirmAmend({ confirmAmendCriteria }, context)).rejects.toThrow('Test error');
  });
});

describe('confirmAmendLogic', () => {
  const context = {};
  afterEach(() => {
    jest.resetAllMocks();
  });

  const confirmAmendLogicCriteria: ConfirmAmendLogicCriteria = {
    tempBookingRef: 'GAN-e4e2ee68',
    originalBookingRef: 'GAN-00ebdff4',
    token: 'token123',
    bookingChannel: {
      channel: Channel.CCUI,
      subchannel: 'WEB',
      language: 'DE'
    },
    paymentOptionSelected: 'PAY_ON_ARRIVAL',
    environment: 'test',
    emailAddress: '',
    ccuiExtraItems: {},
    paymentRequest: {}
  };

  it('should call put with correct parameters when confirmAmendLogic is called', async () => {
    (put as jest.Mock).mockResolvedValue({
      data: {}
    });
    await confirmAmendLogic({ confirmAmendLogicCriteria }, context);

    expect(put).toHaveBeenCalledWith(
      endpoints.CONFIRM_AMEND_LOGIC,
      confirmAmendLogic,
      confirmAmendLogicCriteria,
      context
    );
  });

  it('should handle errors correctly when confirmAmendLogic fails', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValue(error);
    await expect(confirmAmendLogic({ confirmAmendLogicCriteria }, context)).rejects.toThrow(
      'Test error'
    );
  });
});
