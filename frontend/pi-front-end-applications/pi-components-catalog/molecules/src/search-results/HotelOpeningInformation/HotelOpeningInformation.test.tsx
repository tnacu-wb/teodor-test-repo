import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import HotelOpeningInformation from './HotelOpeningInformation.component';

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_ASSETS_URL: 'https://secure2.premierinn.com',
  },
}));
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    router: {
      locale: 'en',
    },
  }),
}));

const props = {
  hotelOpeningDate: '2025-09-05',
  labels: {
    openingOn: 'Opening on',
  },
  testId: 'sr-hotel-opening-information',
};

describe('SRP - HotelOpeningInformation', () => {
  it('should render HotelOpeningInformation', () => {
    const { getByTestId } = render(<HotelOpeningInformation {...props} />);
    expect(getByTestId(props.testId)).toBeInTheDocument();
  });
  it('should render "Opening on" label + date of opening: 5 September 2025', () => {
    const { getByText } = render(<HotelOpeningInformation {...props} />);
    expect(getByText(props.labels.openingOn)).toBeInTheDocument();
    expect(getByText('5 September 2025')).toBeInTheDocument();
  });

  it('should not render "Opening on" label', () => {
    const { queryAllByText } = render(<HotelOpeningInformation {...props} hotelOpeningDate="" />);
    expect(queryAllByText(props.labels.openingOn)).toHaveLength(0);
  });
});
