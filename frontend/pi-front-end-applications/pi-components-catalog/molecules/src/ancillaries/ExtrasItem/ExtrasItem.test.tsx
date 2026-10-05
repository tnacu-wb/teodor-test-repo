import '@testing-library/jest-dom';

import { render, fireEvent, act, waitFor } from '../../utils/test-utils';
import ExtrasItem from './ExtrasItem.component';

const mockDataItem = {
  currency: 'GBP',
  id: 'HSCKIN',
  name: 'Early Check-In',
  order: 1,
  price: 10,
  available: 10,
  isRemovable: true,
  isAvailable: true,
  selectedRoom: 0,
  selectedExtrasList: [
    { packagesList: ['HSCKIN'], reservationId: '1798695' },
    { packagesList: ['HSCOU2'], reservationId: '1798696' },
  ],
  handleSelectedExtrasList: () => jest.fn(),
  allRooms: false,
  numberOfRooms: 2,
  isFree: false,
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  renderSanitizedHtml: (html: string) => html,
  isStringValid: (string: string) => !!string,
}));

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

describe('ExtrasItem Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('should render  <ExtrasItem/> ', async () => {
    const { getByTestId } = render(<ExtrasItem {...mockDataItem} />);

    await waitFor(() => {
      expect(getByTestId('Extras-Item-Wrapper-Early-Check-In')).toBeInTheDocument();
      expect(getByTestId('Extras-Item-Title-Early-Check-In')).toBeInTheDocument();
      expect(getByTestId('Extras-Item-Price-Early-Check-In')).toBeInTheDocument();
      expect(getByTestId('Extras-Item-Description-Early-Check-In')).toBeInTheDocument();
      expect(getByTestId('Extras-Item-ButtonAdd-Early-Check-In')).toBeInTheDocument();
    });
  });

  it('should render the image if the imageSrc string is valid', async () => {
    const imageSrc = '/content/dam/global/restaurants/Global/full-breakfast-booking.png';
    const { getByTestId } = render(<ExtrasItem {...mockDataItem} imageSrc={imageSrc} />);

    const extrasImage = getByTestId('Extras-Item-Image-Early-Check-In');

    await waitFor(() => {
      expect(extrasImage).toBeInTheDocument();
    });
  });

  it('should change button naming when extras is added ', async () => {
    const { getByTestId } = render(<ExtrasItem {...mockDataItem} />);

    const buttonAddRemove = getByTestId('Extras-Item-ButtonAdd-Early-Check-In');

    await act(async () => {
      fireEvent.click(buttonAddRemove);
    });

    await waitFor(() => {
      expect(buttonAddRemove).toHaveTextContent('upsell.extras.remove');
    });
  });
  it('should change button naming when extras is removed ', async () => {
    const { getByTestId } = render(<ExtrasItem {...mockDataItem} isRemovable={false} />);

    const buttonAddRemove = getByTestId('Extras-Item-ButtonAdd-Early-Check-In');

    await act(async () => {
      fireEvent.click(buttonAddRemove);
    });

    await waitFor(() => {
      expect(buttonAddRemove).toHaveTextContent('upsell.extras.add');
    });
  });

  it('should render the unavailable button', async () => {
    const { getByTestId } = render(
      <ExtrasItem {...mockDataItem} isRemovable={false} isAvailable={false} />
    );

    const buttonAddRemove = getByTestId('Extras-Item-ButtonAdd-Early-Check-In');

    await waitFor(() => {
      expect(buttonAddRemove).toBeDisabled();
      expect(buttonAddRemove).toHaveTextContent('hoteldetails.rates.notavailable');
    });
  });

  it('should not render the image if the imageSrc string is not valid', async () => {
    const { queryByTestId } = render(
      <ExtrasItem {...mockDataItem} isRemovable={false} isAvailable={false} imageSrc="" />
    );

    const extrasImage = queryByTestId('Extras-Item-Image-Early-Check-In');

    await waitFor(() => {
      expect(extrasImage).not.toBeInTheDocument();
    });
  });

  it('should render empty string if the description did not come', async () => {
    const { getByTestId } = render(<ExtrasItem {...mockDataItem} />);

    await waitFor(() => {
      expect(getByTestId('Extras-Item-Description-Early-Check-In').textContent).toBe('');
    });
  });

  it('should render all rooms Extras component', async () => {
    const { getByText } = render(
      <ExtrasItem
        {...mockDataItem}
        isRemovable={false}
        isAvailable={false}
        imageSrc=""
        allRooms={true}
      />
    );

    const allRoomsTotalLabel = getByText('ancillaries.extras.total.text');

    await waitFor(() => {
      expect(allRoomsTotalLabel).toBeInTheDocument();
    });
  });
  it('should render free tag when isFree is true', async () => {
    const { getByTestId } = render(<ExtrasItem {...mockDataItem} isFree={true} />);

    await waitFor(() => {
      expect(getByTestId('Extras-Item-Free-tag-Early-Check-In')).toBeInTheDocument();
    });
  });
  it('should not render button when isFree is true', async () => {
    const { queryByTestId } = render(<ExtrasItem {...mockDataItem} isFree={true} />);

    expect(queryByTestId('Extras-Item-ButtonAdd-Early-Check-In')).not.toBeInTheDocument();
  });
  it('should render per day price for ultimate wifi', async () => {
    const { getByTestId } = render(<ExtrasItem {...mockDataItem} id="FI24HR" noNights={2} />);

    await waitFor(() => {
      expect(getByTestId('Extras-Item-Subtitle-Early-Check-In')).toBeInTheDocument();
    });
  });

  it('should render free tag container when isFree is true', async () => {
    const { getByTestId } = render(
      <ExtrasItem {...mockDataItem} isFree={true} promoText="Free with your booking" />
    );

    await waitFor(() => {
      expect(getByTestId('Extras-Item-Free-tag-Early-Check-In')).toBeInTheDocument();
    });
  });
});
