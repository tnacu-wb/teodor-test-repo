import '@testing-library/jest-dom';
import { TableRow } from '@whitbread-eos/api';

import { render } from '../../utils/test-utils';
import ResultList from './ResultList.component';

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    router: {
      locale: 'en',
    },
  }),
}));

const mockHeaderTitles = [
  {
    id: 'GuestName',
    text: 'Guest Name',
  },
  {
    id: 'CompanyName',
    text: 'Company name',
  },
];

const mockRows = [
  {
    cells: [
      {
        id: 'GuestName',
        value: 'Test',
      },
      {
        id: 'CompanyName',
        value: 'Test wb',
      },
    ],
  },
] as TableRow[] | undefined;

const baseDataTestId = 'SearchAccountPage';
const props = {
  baseDataTestId,
  t: (id: string) => id,
  rows: mockRows,
  headerTitles: mockHeaderTitles,
};

describe('Results List', () => {
  it('should render the component', () => {
    const { getByTestId } = render(<ResultList {...props} />);
    expect(getByTestId(`${baseDataTestId}-Table-Container`)).toBeInTheDocument();
    expect(getByTestId(`${baseDataTestId}-TableHeader-GuestName`)).toBeInTheDocument();
    expect(getByTestId(`${baseDataTestId}-TableHeader-CompanyName`)).toBeInTheDocument();
  });
});
