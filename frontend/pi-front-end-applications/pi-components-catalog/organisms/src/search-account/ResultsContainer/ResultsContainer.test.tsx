import '@testing-library/jest-dom';

import { render, userEvent } from '../../utils/test-utils';
import ResultsContainer, { type Props } from './ResultsContainer.container';

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => jest.fn(),
}));

const mockProps: Props = {
  t: jest.fn(),
  baseDataTestId: 'ResultsContainer',
  resultsData: {
    guests: [
      {
        cells: [
          {
            id: 'GuestName',
            value: 'Mr Test Jest',
          },
          {
            id: 'CompanyName',
            value: 'Whitbread',
          },
          {
            id: 'Email',
            value: 'test.jest@whitbread.com',
          },
          {
            id: 'PostCodeHome',
            value: 'CR2 5ER',
          },
          {
            id: 'PostCodeCompany',
            value: 'EC1N 2TD',
          },
        ],
      },
    ],
  },
  handleClickRow: jest.fn(),
  selectedRow: {
    '0': {
      id: 'GuestName',
      value: 'Mr Gabriel Miron',
    },
    '1': {
      id: 'CompanyName',
      value: 'Mironian S.R.L.',
    },
    '2': {
      id: 'Email',
      value: 'gabriel.miron@whitbread.com',
    },
    '3': {
      id: 'PostCodeHome',
      value: '567 MH XL',
    },
    '4': {
      id: 'PostCodeCompany',
      value: '567 MH XL',
    },
    rowNr: 0,
    accountId: '897671234',
  },
  dataForUpdateForm: {
    title: 'Mr',
    firstName: 'Sasa',
    lastName: 'Radivoi',
    companyName: 'Whitbread',
    email: 'sasa.radivoi@whitbread.com',
    address: '',
    postalCode: '',
    mobileNumber: '+440757233444',
    landlineNumber: '+44271160522',
  },
};

describe('ResultsContainer', () => {
  afterAll(() => {
    jest.resetAllMocks();
  });

  it('should render a <ResultsContainer> with default props', function () {
    const { getByTestId } = render(<ResultsContainer {...mockProps} />);

    expect(getByTestId('ResultsContainer-Table-Container')).toBeInTheDocument();
  });

  it('should have the guest name, company name, email and post codes', function () {
    const { getByText } = render(<ResultsContainer {...mockProps} />);

    expect(getByText('Mr Test Jest')).toBeInTheDocument();
    expect(getByText('Whitbread')).toBeInTheDocument();
    expect(getByText('test.jest@whitbread.com')).toBeInTheDocument();
    expect(getByText('CR2 5ER')).toBeInTheDocument();
    expect(getByText('EC1N 2TD')).toBeInTheDocument();
  });

  it('should call handleClickRow when a row is clicked', async function () {
    const { getByTestId } = render(<ResultsContainer {...mockProps} />);
    const displayExtraInfoBtn = getByTestId('ResultsContainer-Table-Row-0');

    userEvent.click(displayExtraInfoBtn);

    // Assert that handleClickRow has been called once
    expect(mockProps.handleClickRow).toHaveBeenCalledTimes(1);
  });
});
