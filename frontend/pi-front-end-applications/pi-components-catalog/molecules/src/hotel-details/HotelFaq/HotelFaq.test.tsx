import '@testing-library/jest-dom';
import { Faq } from '@whitbread-eos/api';

import { render } from '../../utils/test-utils';
import { HotelFaq } from './HotelFaq';

const mockUseStaticHotelInformation = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useStaticHotelInformation: () => mockUseStaticHotelInformation(),
}));

const faqdata = {
  faqItems: [
    {
      question: 'Can I check in early?',
      answer:
        '<p>Early check-in is available from 11am for an additional £10, and this will be payable at reception on arrival. Early check-in is subject to availability and not available at all Premier Inn hotels.&nbsp;&nbsp;</p>\r\n',
    },
    {
      question: 'Can I check in out?',
      answer:
        '<p>Early check-in is available from 11am for an additional £10, and this will be payable at reception on arrival. Early check-in is subject to availability and not available at all Premier Inn hotels.&nbsp;&nbsp;</p>\r\n',
    },
  ],
};

const defaultHookReturn = {
  faq: faqdata as Faq,
  brand: 'hub',
  isLoading: false,
  isError: false,
  error: null,
};

describe('HotelFaq', () => {
  beforeEach(() => {
    mockUseStaticHotelInformation.mockReturnValue(defaultHookReturn);
  });

  it('should render Hotel Title Faq', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      ...defaultHookReturn,
      faq: { ...faqdata, title: 'FAQ Title' },
    });
    const { getByRole } = render(<HotelFaq />);
    expect(getByRole('heading')).toBeInTheDocument();
  });

  it('should not render Hotel Faq', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      ...defaultHookReturn,
      brand: 'zip',
    });
    const { queryByRole } = render(<HotelFaq />);
    expect(queryByRole('heading')).not.toBeInTheDocument();
  });

  it('should render the hotel faq list', () => {
    const { getByTestId } = render(<HotelFaq />);
    expect(getByTestId('hdp-faqs-list')).toBeInTheDocument();
  });

  it('should render a loading message, if isLoading prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      ...defaultHookReturn,
      isLoading: true,
    });
    const { getByText } = render(<HotelFaq />);
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should render error message, if isError prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      ...defaultHookReturn,
      isError: true,
      error: { message: 'Error' },
    });
    const { getByText } = render(<HotelFaq />);
    expect(getByText('Error')).toBeInTheDocument();
  });

  it('should render empty string if the faqAnswer did not come', async () => {
    mockUseStaticHotelInformation.mockReturnValue({
      ...defaultHookReturn,
      isLoading: true,
      faq: { faqItems: [{ question: 'Question' }] },
    });
    const { getAllByText } = render(<HotelFaq />);

    expect(getAllByText('')[0]).toBeInTheDocument();
  });
});
