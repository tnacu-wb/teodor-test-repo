import '@testing-library/jest-dom';
import { HeaderInformationData } from '@whitbread-eos/api';

import { render } from '../../utils/test-utils';
import ManageBookingModal from './ManageBookingModal.component';
import ManageBookingModalContainer from './ManageBookingModal.container';

const props = {
  labels: {
    headerInformation: {
      form: {
        findBookingTitle: 'test',
        findBookingDescription: 'test',
        bookingReferenceLabel: 'bookingReferenceLabel',
        bookingSurnameLabel: 'bookingSurnameLabel',
        invalidReference: 'invalidReference',
        invalidSurname: 'invalidSurname',
        arrivalDateLabel: 'arrival date',
      },
      content: {
        global: {
          today: 'Today',
          tomorrow: 'Tomorrow',
        },
      },
    },
  } as HeaderInformationData,
  onClose: jest.fn(),
  isOpen: true,
  baseTestId: 'ManageBookingModal',
};

const propsContainer = { isOpen: true, onClose: jest.fn() };

let mockResponse = {};
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => mockResponse,
}));

const mockUseRouter = jest.fn(() => {
  return { push: jest.fn() };
});
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

describe('<ManageBookingModal />', () => {
  it('should render the ManageBookingModal with default props', () => {
    const { getByTestId } = render(<ManageBookingModal {...props} />);
    expect(getByTestId(`${props.baseTestId}-ModalContent`)).toBeInTheDocument();
    expect(getByTestId(`${props.baseTestId}_Title`)).toBeInTheDocument();
    expect(getByTestId(`${props.baseTestId}_Description`)).toBeInTheDocument();
    expect(getByTestId(`${props.baseTestId}-Search`)).toBeInTheDocument();
    expect(getByTestId(`${props.baseTestId}-ArrivalDate`)).toBeInTheDocument();
    expect(getByTestId(`${props.baseTestId}-BookingSurname`)).toBeInTheDocument();
    expect(getByTestId(`${props.baseTestId}-BookingReference`)).toBeInTheDocument();
  });

  it('should render the ManageBookingModalContainer container with default props', () => {
    const { getByTestId } = render(<ManageBookingModalContainer {...propsContainer} />);
    expect(getByTestId(`${props.baseTestId}-ModalContent`)).toBeInTheDocument();
    expect(getByTestId(`${props.baseTestId}_Title`)).toBeInTheDocument();
    expect(getByTestId(`${props.baseTestId}_Description`)).toBeInTheDocument();
    expect(getByTestId(`${props.baseTestId}-Search`)).toBeInTheDocument();
    expect(getByTestId(`${props.baseTestId}-ArrivalDate`)).toBeInTheDocument();
    expect(getByTestId(`${props.baseTestId}-BookingSurname`)).toBeInTheDocument();
    expect(getByTestId(`${props.baseTestId}-BookingReference`)).toBeInTheDocument();
  });

  it('should render the ManageBookingModalContainer container with language de', () => {
    mockResponse = { language: 'de' };
    const { getByTestId } = render(<ManageBookingModalContainer {...propsContainer} />);
    expect(getByTestId(`${props.baseTestId}-ModalContent`)).toBeInTheDocument();
    expect(getByTestId(`${props.baseTestId}_Title`)).toBeInTheDocument();
    expect(getByTestId(`${props.baseTestId}_Description`)).toBeInTheDocument();
    expect(getByTestId(`${props.baseTestId}-Search`)).toBeInTheDocument();
    expect(getByTestId(`${props.baseTestId}-ArrivalDate`)).toBeInTheDocument();
    expect(getByTestId(`${props.baseTestId}-BookingSurname`)).toBeInTheDocument();
    expect(getByTestId(`${props.baseTestId}-BookingReference`)).toBeInTheDocument();
  });

  it('should render the ManageBookingModalContainer container with base test undefined', () => {
    props.baseTestId = undefined;
    const { getByTestId } = render(<ManageBookingModalContainer {...propsContainer} />);
    expect(getByTestId(`ManageBookingModal-ArrivalDate`)).toBeInTheDocument();
  });
});
