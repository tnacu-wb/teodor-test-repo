import '@testing-library/jest-dom';

import { render } from '../../../utils/test-utils';
import BookingSummaryInfoMessages, { Props } from './BookingSummaryInfoMessages';

const mockProps: Props = {
  infoMessages: ['Test Message'],
  prefixDataTestId: 'TestId',
};

afterEach(() => {
  jest.resetAllMocks();
});

describe('BookingSummaryInfoMessages', () => {
  it('should render a <BookingSummaryInfoMessages> container with data-testid correctly', function () {
    const { getByTestId } = render(<BookingSummaryInfoMessages {...mockProps} />);
    expect(getByTestId('TestId-HotelInformation-InfoMessages')).toBeInTheDocument();
    expect(getByTestId('TestId-HotelInformation-InfoMessages-0')).toBeInTheDocument();
    expect(getByTestId('TestId-HotelInformation-InfoMessages-0').textContent).toBe('Test Message');
  });

  it('should render a <BookingSummaryInfoMessages> container with correct message', function () {
    const { getByTestId } = render(<BookingSummaryInfoMessages {...mockProps} />);
    expect(getByTestId('TestId-HotelInformation-InfoMessages-0').textContent).toBe('Test Message');
  });

  it('should apply formatLinks class to sanitized info message content', function () {
    const { getByText } = render(
      <BookingSummaryInfoMessages
        infoMessages={[
          'Important <a href="https://www.premierinn.com" target="_blank">message</a>',
        ]}
      />
    );

    expect(getByText('Important').closest('.formatLinks')).toBeInTheDocument();
    expect(getByText('message')).toHaveAttribute('href', 'https://www.premierinn.com');
    expect(getByText('message')).toHaveAttribute('target', '_blank');
  });

  it('should render a <BookingSummaryInfoMessages> with info messages an empty list', function () {
    const { queryByTestId } = render(<BookingSummaryInfoMessages infoMessages={[]} />);
    expect(queryByTestId('InfoMessages')).toBeFalsy();
  });

  it('should render a <BookingSummaryInfoMessages> with info messages undefined', function () {
    const { queryByTestId } = render(<BookingSummaryInfoMessages infoMessages={undefined} />);
    expect(queryByTestId('InfoMessages')).toBeFalsy();
  });

  it('should render a <BookingSummaryInfoMessages> with info messages empty text', function () {
    mockProps.infoMessages[0] = '';
    mockProps.infoMessages[1] = 'test';
    const { queryByTestId, getByText } = render(<BookingSummaryInfoMessages {...mockProps} />);
    expect(queryByTestId('TestId-HotelInformation-InfoMessages')).toBeTruthy();
    expect(getByText('test')).toBeTruthy();
  });

  it('should render a <BookingSummaryRateInformation> without data-testid', function () {
    mockProps.prefixDataTestId = null;
    const { getByTestId } = render(<BookingSummaryInfoMessages {...mockProps} />);
    expect(getByTestId('HotelInformation-InfoMessages')).toBeInTheDocument();
  });
});
