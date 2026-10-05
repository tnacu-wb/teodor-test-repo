import '@testing-library/jest-dom';
import type { HIImportantInfo } from '@whitbread-eos/api';

import { render } from '../../../utils/test-utils';
import { ImportantNotification } from './ImportantNotification';

const mockUseStaticHotelInformation = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useStaticHotelInformation: () => mockUseStaticHotelInformation(),
}));

const mockImportantInfo = {
  title: 'Important Information',
  infoItems: [
    {
      text: 'There is limited parking at this hotel which is allocated on a first come, first served basis and is chargeable.',
      priority: '1',
      startDate: '03/09/2022',
      endDate: '30/09/2022',
    },
  ],
};

const importantNotificationProps = {
  isLoading: false,
  error: null,
  isError: false,
  importantInfo: mockImportantInfo,
  arrival: '2022-09-25',
  departure: '2022-09-29',
};

describe('ImportantNotification', () => {
  beforeEach(() => {
    mockUseStaticHotelInformation.mockReturnValue({
      importantInfo: mockImportantInfo,
      isLoading: false,
      isError: false,
      error: null,
    });
  });

  it('renders ImportantNotification with default props', () => {
    const { getByTestId } = render(
      <ImportantNotification arrival="2022-09-25" departure="2022-09-29" />
    );
    expect(getByTestId('important-notification')).toBeInTheDocument();
  });

  it('should render ImportantNotification', () => {
    const { getByTestId } = render(
      <ImportantNotification arrival="2022-09-25" departure="2022-09-29" />
    );
    expect(getByTestId('important-notification')).toBeInTheDocument();
  });

  it('should not render ImportantNotification', () => {
    const { queryByTestId } = render(<ImportantNotification arrival="" departure="" />);
    expect(queryByTestId('important-notification')).toBeNull();
  });

  it('should render a loading message if isLoading prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      importantInfo: mockImportantInfo,
      isLoading: true,
      isError: false,
      error: null,
    });

    const { getByText } = render(
      <ImportantNotification arrival="2022-09-25" departure="2022-09-29" />
    );
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should render an error message if isError prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      importantInfo: mockImportantInfo,
      isLoading: false,
      isError: true,
      error: { message: 'Error' },
    });

    const { getByText } = render(
      <ImportantNotification arrival="2022-09-25" departure="2022-09-29" />
    );
    expect(getByText('Error')).toBeInTheDocument();
  });

  it('should render notification text', () => {
    const { getByTestId, getByText } = render(
      <ImportantNotification arrival="2022-09-25" departure="2022-09-29" />
    );
    expect(getByTestId('important-notification')).toBeInTheDocument();
    expect(
      getByText(importantNotificationProps.importantInfo.infoItems[0].text)
    ).toBeInTheDocument();
  });

  it('should render htmlText when provided', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      importantInfo: {
        title: 'Important Information',
        infoItems: [
          {
            text: 'Plain fallback text',
            htmlText:
              'Important <a href="https://www.premierinn.com" target="_blank">hotel update</a>',
            priority: '1',
            startDate: '03/09/2022',
            endDate: '30/09/2022',
          },
        ],
      },
      isLoading: false,
      isError: false,
      error: null,
    });

    const { getByRole, queryByText } = render(
      <ImportantNotification arrival="2022-09-25" departure="2022-09-29" />
    );

    const link = getByRole('link', { name: 'hotel update' });

    expect(link).toHaveAttribute('href', 'https://www.premierinn.com');
    expect(link).toHaveAttribute('target', '_blank');
    expect(queryByText('Plain fallback text')).not.toBeInTheDocument();
  });

  it('should render nothing when no importantInfo is given', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      importantInfo: {} as HIImportantInfo,
      isLoading: false,
      isError: false,
      error: null,
    });

    const { queryByTestId } = render(
      <ImportantNotification arrival="2022-09-25" departure="2022-09-29" />
    );
    expect(queryByTestId('important-notification')).toBeNull();
  });

  it('should render nothing when no infoItems are given', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      importantInfo: {
        title: 'Important Information',
        infoItems: [],
      },
      isLoading: false,
      isError: false,
      error: null,
    });

    const { queryByTestId } = render(
      <ImportantNotification arrival="2022-09-25" departure="2022-09-29" />
    );
    expect(queryByTestId('important-notification')).toBeNull();
  });

  it('should render nothing when notification date is not before arrival and departure', () => {
    const { queryByTestId } = render(
      <ImportantNotification arrival="2022-10-25" departure="2022-10-29" />
    );
    expect(queryByTestId('important-notification')).toBeNull();
  });

  it('should render nothing when notification date is after arrival and departure', () => {
    const { queryByTestId } = render(
      <ImportantNotification arrival="2023-08-25" departure="2023-08-29" />
    );
    expect(queryByTestId('important-notification')).toBeNull();
  });

  it('should render notification when notification date is overlaps between arrival and departure', () => {
    const { getByTestId, getByText } = render(
      <ImportantNotification arrival="2022-09-25" departure="2022-10-29" />
    );
    expect(getByTestId('important-notification')).toBeInTheDocument();
    expect(
      getByText(importantNotificationProps.importantInfo.infoItems[0].text)
    ).toBeInTheDocument();
  });
});
