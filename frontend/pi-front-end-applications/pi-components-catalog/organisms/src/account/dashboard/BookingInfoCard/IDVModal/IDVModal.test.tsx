import '@testing-library/jest-dom';

import { render, userEvent } from '../../../../utils/test-utils';
import type { Props } from './IDVModal.component';
import IDVModal from './IDVModal.component';

const closeButtonMock = jest.fn();

const props = {
  isVisible: true,
  onClose: closeButtonMock,
  t: (value: string) => value,
  data: {
    personalInformation: {
      bookerName: 'Graem Philip',
      guestName: 'Graem Philip',
      address: '34 Hawthorn crescent',
      postcode: 'Ky4 8EF',
      telephoneNumber: '07964737101',
      cardUsedToMakeBooking: '**** 7034',
    },
    bookingInformation: {
      reservationNumber: {
        value: '#basketReference',
        partOfSearch: true,
      },
      hotelName: 'Glasgow City',
      arrivalDate: '2022-03-28',
      departureDate: '2022-03-29',
      emailAddress: 'graem.philip@gmail.com',
    },
    dpaStatus: {
      dpaPassed: true,
      dpaOverride: false,
      eCnpPassword: 'XXXXX',
    },
  },
  setData: jest.fn(),
  inputValues: {
    bookerLastName: 'Test',
  },
  onReuseDetails: jest.fn(),
} as unknown as Props;

describe('IDVModal', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('it should render the IDVModal', () => {
    const { getByText } = render(<IDVModal {...props} />);
    expect(getByText('ccui.idv.heading')).toBeInTheDocument();
    expect(getByText('ccui.idv.personalInformation.heading')).toBeInTheDocument();
    expect(getByText('ccui.idv.bookingInformation.heading')).toBeInTheDocument();
    expect(getByText('ccui.idv.dpaStatus.heading')).toBeInTheDocument();
    expect(getByText('ccui.idv.closeButton')).toBeInTheDocument();
  });

  it('it should render the IDVModal with inputvalues.', () => {
    const { getByText } = render(
      <IDVModal
        {...{
          ...props,
          inputValues: {
            guestLastName: 'George',
            bookerAddress: 'Ha, Greenwich',
            bookerPostcode: 'E1 6AN',
            bookerPhone: '0876666232',
            bookerLastName: undefined,
            bookerCard: 'card',
            bookingReference: 'AWM8159458',
            hotelName: 'Manold',
            arrivalDate: '2023-12-20',
            departureDate: '2023-12-21',
            bookerEmail: 'test@gmail.com',
          },
        }}
      />
    );
    expect(getByText('ccui.idv.heading')).toBeInTheDocument();
    expect(getByText('ccui.idv.personalInformation.heading')).toBeInTheDocument();
    expect(getByText('ccui.idv.bookingInformation.heading')).toBeInTheDocument();
    expect(getByText('ccui.idv.dpaStatus.heading')).toBeInTheDocument();
    expect(getByText('ccui.idv.closeButton')).toBeInTheDocument();
  });

  it('it should render the IDVModal and close', () => {
    const { getByTestId } = render(<IDVModal {...props} />);

    const closeModal = getByTestId('IDVModal-CloseButton');
    userEvent.click(closeModal);
    expect(closeButtonMock).toBeCalled();
  });

  it('should trigger a change when click on radio', () => {
    const { getByTestId, getByRole } = render(<IDVModal {...props} />);
    const radiogroup = getByTestId('IDVModal-DpaStatus-DpaPassed-RadioGroup');
    const radiodpaPassedYes = getByRole('radio', { name: `ccui.idv.dpaStatus.dpaPassedYes` });

    const radiodpaPassedNo = getByRole('radio', { name: `ccui.idv.dpaStatus.dpaPassedNo` });

    expect(radiogroup).toBeInTheDocument();

    userEvent.click(radiodpaPassedNo);

    userEvent.click(radiodpaPassedYes);
  });

  it('should change the value when 2 yes radio are clicked', async () => {
    const { getByTestId, getByRole } = render(<IDVModal {...props} />);
    const radiogroupDpaPassed = getByTestId('IDVModal-DpaStatus-DpaPassed-RadioGroup');
    const radiogroupDpaOverride = getByTestId('IDVModal-DpaStatus-DpaOverride-RadioGroup');

    const radiodpaPassedYes = getByRole('radio', { name: `ccui.idv.dpaStatus.dpaPassedYes` });
    const radiodpaOverrideYes = getByRole('radio', { name: `ccui.idv.dpaStatus.dpaOverrideYes` });
    const radiodpaPassedNo = getByRole('radio', { name: `ccui.idv.dpaStatus.dpaPassedNo` });
    const radiodpaOverrideNo = getByRole('radio', { name: `ccui.idv.dpaStatus.dpaOverrideNo` });

    expect(radiogroupDpaPassed).toBeInTheDocument();
    expect(radiogroupDpaOverride).toBeInTheDocument();

    await userEvent.click(radiodpaOverrideYes);
    await userEvent.click(radiodpaPassedYes);

    expect((radiodpaPassedNo as HTMLInputElement).checked).toEqual(false);

    await userEvent.click(radiodpaPassedYes);

    expect((radiodpaOverrideNo as HTMLInputElement).checked).toEqual(true);
  });
});
