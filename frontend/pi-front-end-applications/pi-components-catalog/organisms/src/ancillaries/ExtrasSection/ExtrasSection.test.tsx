import '@testing-library/jest-dom';

import { render, waitFor } from '../../utils/test-utils';
import ExtrasSection from './ExtrasSection.component';

const getMockData = () => ({
  extrasDetailsList: [
    {
      currency: 'GBP',
      description: '<p>Check in any time</p>',
      id: 'HSCKIN',
      imageSrc: 'img',
      name: 'Early Check-In',
      order: 1,
      price: 10,
      available: 3,
    },
    {
      currency: 'GBP',
      description: '<p>Check out</p>',
      id: 'HSCOU2',
      imageSrc: 'img',
      name: 'Late Checkout-out',
      order: 2,
      price: 10,
      available: 1,
    },
  ],
  handleSelectedExtrasList: jest.fn(),
  selectedExtrasList: [
    {
      packagesList: ['HSCKIN'],
      reservationId: '1',
      previousEciSelection: 0,
      previousLcoSelection: 0,
    },
    {
      packagesList: ['HSCOU2'],
      reservationId: '2',
      previousEciSelection: 0,
      previousLcoSelection: 0,
    },
  ],
  selectedRoom: 0,
  noNights: 1,
  allRooms: false,
});

const mockDataItems = {
  extrasDetailsList: [
    {
      currency: 'GBP',
      description: '<p>Check in any time from 11am (normal check-in time is 3pm).</p>\r\n',
      id: 'HSCKIN',
      imageSrc: '/content/dam/global/restaurants/Global/full-breakfast-booking.png',
      name: 'Early Check-In',
      order: 1,
      price: 10,
      available: 3,
    },
    {
      currency: 'GBP',
      description: '<p>Check out any time until 2pm (normal check-out time is 12pm).</p>\r\n',
      id: 'HSCOU2',
      imageSrc: '/content/dam/global/restaurants/Global/full-breakfast-booking.png',
      name: 'Late Checkout-out',
      order: 1,
      price: 10,
      available: 1,
    },
  ],
  handleSelectedExtrasList: jest.fn(),
  selectedExtrasList: [
    {
      packagesList: ['HSCKIN'],
      reservationId: '1798695',
      previousEciSelection: 0,
      previousLcoSelection: 0,
    },
    {
      packagesList: ['HSCOU2'],
      reservationId: '1798696',
      previousEciSelection: 0,
      previousLcoSelection: 0,
    },
    {
      packagesList: ['HSCOU2', 'HSCKIN'],
      reservationId: '1798697',
      previousEciSelection: 0,
      previousLcoSelection: 0,
    },
  ],
  selectedRoom: 0,
  noNights: 1,
  allRooms: false,
};

const mockUseRouter = jest.fn();

// temporary solution until next/router will be deprecated
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('next/image', () => ({
  __esModule: true,
  default: () => {
    return 'Next image stub';
  },
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  renderSanitizedHtml: (html: string) => html,
}));

describe('ExtrasSection Component', () => {
  beforeAll(() => {
    mockDataItems.extrasDetailsList[0].id = 'HSCKIN';
    mockDataItems.extrasDetailsList[1].id = 'HSCOU2';
  });
  it('should render  <ExtrasSection/> ', async () => {
    const { getByTestId } = render(<ExtrasSection {...mockDataItems} />);

    await waitFor(() => {
      expect(getByTestId('ExtrasSection-Wrapper')).toBeInTheDocument();
      expect(getByTestId('ExtrasSection-Heading-Title')).toBeInTheDocument();
      expect(getByTestId('Extras-Item-Wrapper-Early-Check-In')).toBeInTheDocument();
      expect(getByTestId('Extras-Item-Wrapper-Late-Checkout-out')).toBeInTheDocument();
    });
  });

  it('should render ExtrasSection notification without allRooms', async () => {
    mockDataItems.extrasDetailsList[0].available = 1;
    mockDataItems.extrasDetailsList[1].available = 2;

    const { getByText } = render(<ExtrasSection {...mockDataItems} />);

    await waitFor(() => {
      expect(getByText('ancillaries.extras.notification.max')).toBeInTheDocument();
    });
  });

  it('should render ExtrasSection notification with allRooms and availibility < numberOfRooms', async () => {
    mockDataItems.extrasDetailsList[0].available = 1;
    mockDataItems.extrasDetailsList[1].available = 1;

    const { getByTestId } = render(<ExtrasSection {...mockDataItems} allRooms={true} />);

    await waitFor(() => {
      expect(
        getByTestId('Extras-Item-allRooms-TotalAllRoomsLabel-Early-Check-In')
      ).toBeInTheDocument();
    });
  });

  it('should render ExtrasSection notification with allRooms and one Extras not available', async () => {
    mockDataItems.extrasDetailsList[0].available = 0;
    mockDataItems.extrasDetailsList[1].available = 1;

    const { getByText } = render(<ExtrasSection {...mockDataItems} allRooms={true} />);

    await waitFor(() => {
      expect(getByText('ancillaries.extras.notification.allRooms')).toBeInTheDocument();
    });
  });

  it('should check the inventory availibility without IDs and available inventory', async () => {
    mockDataItems.extrasDetailsList[0].id = '';
    mockDataItems.extrasDetailsList[1].id = '';
    mockDataItems.extrasDetailsList[0].available = 0;
    mockDataItems.extrasDetailsList[1].available = 1;

    const { getByTestId } = render(<ExtrasSection {...mockDataItems} />);

    await waitFor(() => {
      expect(getByTestId('ExtrasSection-Wrapper')).toBeInTheDocument();
    });
  });

  it('should render ExtrasSection with one room', async () => {
    mockDataItems.allRooms = true;
    mockDataItems.extrasDetailsList = [
      {
        currency: 'GBP',
        description: '<p>Check in any time from 11am (normal check-in time is 3pm).</p>\r\n',
        id: 'HSCKIN',
        imageSrc: '/content/dam/global/restaurants/Global/full-breakfast-booking.png',
        name: 'Early Check-In',
        order: 1,
        price: 10,
        available: 1,
      },
    ];

    mockDataItems.selectedExtrasList = [{ packagesList: ['HSCKIN'], reservationId: '1798695' }];

    const { getByTestId } = render(<ExtrasSection {...mockDataItems} />);

    await waitFor(() => {
      expect(getByTestId('ExtrasSection-allRooms-Wrapper')).toBeInTheDocument();
    });
  });
});

describe('ExtrasSection - Rendering', () => {
  it('should render section and items', async () => {
    const props = getMockData();
    const { getByTestId } = render(<ExtrasSection {...props} />);

    await waitFor(() => {
      expect(getByTestId('ExtrasSection-Wrapper')).toBeInTheDocument();
      expect(getByTestId('ExtrasSection-Heading-Title')).toBeInTheDocument();
    });
  });
});
