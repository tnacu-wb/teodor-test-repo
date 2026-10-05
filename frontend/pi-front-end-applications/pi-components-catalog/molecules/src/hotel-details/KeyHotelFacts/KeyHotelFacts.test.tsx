import '@testing-library/jest-dom';
import { act } from '@testing-library/react';
import { Channel, Facts } from '@whitbread-eos/api';
import { useStaticHotelInformation } from '@whitbread-eos/utils';

import { render, userEvent } from '../../utils/test-utils';
import { KeyHotelFacts } from './KeyHotelFacts';

jest.mock('@whitbread-eos/utils', () => {
  const original = jest.requireActual('@whitbread-eos/utils');

  return {
    ...original,
    useStaticHotelInformation: jest.fn(),
  };
});

const keyFactdata = {
  factItems: [
    {
      title: 'Free parking',
      description: '',
    },
    {
      title: 'Lift',
      description: '',
    },
    {
      title: 'Universally accessible rooms',
      description: '',
    },
    {
      title: 'Restaurant',
      description: 'nice restaurant',
    },
    {
      title: 'Breakfast',
      description: 'nice bf',
    },
    {
      title: 'Family rooms',
      description: 'yes room',
    },
    {
      title: 'Payment discount & vouchers',
      description: 'hello',
    },
    {
      title: 'Touchbase',
      description: 'hao',
    },
  ],
};

const keyHotelFactsProps = {
  channel: Channel.Ccui,
};

const mockUseStaticHotelInformation = useStaticHotelInformation as jest.Mock;

const defaultHookReturn = {
  facts: keyFactdata,
  isLoading: false,
  isError: false,
  error: Error,
};

describe('KeyHotelFacts', () => {
  beforeEach(() => {
    mockUseStaticHotelInformation.mockReturnValue(defaultHookReturn);
  });

  it('Should render the see now option', () => {
    const { getByTestId } = render(<KeyHotelFacts {...keyHotelFactsProps} />);
    expect(getByTestId('hdp_hotelKeyFactsLink')).toBeInTheDocument();
  });

  it('Should render the key hotel fact list', () => {
    const { getByTestId } = render(<KeyHotelFacts {...keyHotelFactsProps} />);
    const seeNowButton = getByTestId('hdp_hotelKeyFactsLink');
    act(() => {
      userEvent.click(seeNowButton);
    });
    expect(getByTestId('hdp-key-hotel-fact-list')).toBeInTheDocument();
  });

  it('Should close the key fact popup after clicking close button ', () => {
    const { getByTestId } = render(<KeyHotelFacts {...keyHotelFactsProps} />);
    const seeNowButton = getByTestId('hdp_hotelKeyFactsLink');
    act(() => {
      userEvent.click(seeNowButton);
    });
    expect(getByTestId('ModalCloseButton')).toBeInTheDocument();
    const modalCloseButton = getByTestId('ModalCloseButton');
    userEvent.click(modalCloseButton);
    setTimeout(() => {
      expect(getByTestId('ModalCloseButton')).not.toBeInTheDocument();
    }, 500);
  });

  it('should render a loading message, if isLoading prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      ...defaultHookReturn,
      isLoading: true,
    });

    const { getByText } = render(<KeyHotelFacts {...keyHotelFactsProps} />);
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should render error message, if isError prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      ...defaultHookReturn,
      isError: true,
      error: { message: 'Error' },
    });

    const { getByText } = render(<KeyHotelFacts {...keyHotelFactsProps} />);
    expect(getByText('Error')).toBeInTheDocument();
  });

  it('Should not render the see now option', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      ...defaultHookReturn,
      facts: {} as Facts,
    });

    const { queryByTestId } = render(<KeyHotelFacts {...keyHotelFactsProps} />);
    expect(queryByTestId('hdp_hotelKeyFactsLink')).not.toBeInTheDocument();
  });
  it('Should render Key Hotel Facts', async () => {
    const { getByTestId } = render(<KeyHotelFacts {...keyHotelFactsProps} />);
    expect(getByTestId('keyHotelFacts')).toBeInTheDocument();
  });
});
