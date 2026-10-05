const getPaymentOptions = (isPayNowEnabled: boolean, isPayOnArrivalEnabled: boolean) => {
  return [
    {
      enabled: isPayNowEnabled,
      order: 1,
      type: 'PAY_NOW',
    },
    {
      enabled: isPayOnArrivalEnabled,
      order: 2,
      type: 'PAY_ON_ARRIVAL',
    },
  ];
};

const cardTypes = {
  MC: {
    logoSrc: '/content/dam/global/booking/Mastercard.jpg',
    name: 'Mastercard',
  },
  AX: {
    logoSrc: '/content/dam/global/booking/AX.jpg',
    name: 'American Express',
  },
  VS: {
    logoSrc: '/content/dam/global/booking/Visa_Debit.jpg',
    name: 'Visa',
  },
};

const mockedAcceptedCardTypes = [
  { ...cardTypes.MC, type: 'MC', name: 'Mastercard Credit' },
  { ...cardTypes.AX, type: 'AX', name: 'American Express' },
  { ...cardTypes.VS, type: 'VS', name: 'Visa Debit' },
  { ...cardTypes.MC, type: 'MC', name: 'Mastercard Debit' },
  { ...cardTypes.VS, type: 'VS', name: 'Visa Credit' },
];

const createPaymentMethod = (config: any) => {
  return {
    acceptedCardTypes: config.acceptedCardTypes || [],
    cnpOptionAvailable: config.cnpOptionAvailable || false,
    cnpPreSelected: config.cnpPreSelected || false,
    enabled: config.enabled || false,
    name: config.name,
    subType: config.subType || null,
    order: config.order,
    paymentOptions: getPaymentOptions(config.isPayNowEnabled, config.isPayOnArrivalEnabled),
    reasons: config.reasons || [],
    type: config.type,
  };
};

export const mockedPaymentMethodCCUI = {
  paymentCcuiMethods: [
    createPaymentMethod({
      acceptedCardTypes: mockedAcceptedCardTypes,
      name: 'CARD',
      order: 1,
      type: 'NEW_CARD',
      enabled: true,
      isPayNowEnabled: true,
      isPayOnArrivalEnabled: true,
    }),
    createPaymentMethod({
      acceptedCardTypes: [],
      cnpOptionAvailable: true,
      name: 'PIBA UK',
      subType: 'PIBAGB',
      enabled: false,
      order: 2,
      type: 'NEW_PIBA',
      reasons: ['PIBA_UK_ALLOWED_ONLY_IN_UK'],
      isPayNowEnabled: false,
      isPayOnArrivalEnabled: false,
    }),
    createPaymentMethod({
      acceptedCardTypes: [],
      cnpOptionAvailable: true,
      name: 'PIBA EU',
      subType: 'PIBADE',
      order: 3,
      type: 'NEW_PIBA',
      enabled: false,
      reasons: [''],
      isPayNowEnabled: false,
      isPayOnArrivalEnabled: true,
    }),
    createPaymentMethod({
      acceptedCardTypes: null,
      cnpOptionAvailable: true,
      name: 'Account to company',
      subType: null,
      order: 4,
      enabled: true,
      type: 'ACCOUNT_COMPANY',
      reasons: [''],
      isPayNowEnabled: false,
      isPayOnArrivalEnabled: false,
    }),
    createPaymentMethod({
      cnpOptionAvailable: true,
      name: 'Non-guaranteed booking',
      subType: null,
      order: 5,
      type: 'RESERVE_WITHOUT_CARD',
      enabled: true,
      reasons: [''],
      isPayNowEnabled: false,
      isPayOnArrivalEnabled: true,
    }),
  ],
};
