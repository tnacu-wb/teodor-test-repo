import '@testing-library/jest-dom';
import React from 'react';

import { render } from '../../utils/test-utils';
import SearchSummary from './SearchSummary.component';

const defaultProps = {
  isSummaryActive: true,
  location: 'Manchester, Uk',
  dateInterval: '21 Aug - 23 Aug',
  roomSummary: '1 adult, 1 double room',
  numberOfNightsSummary: '365',
  promotionCategorySummary: 'Promotion category',
  contractRateSummary: 'Contract rate',
  editText: 'Edit',
  isLessThanLg: false,
  isLessThanMd: false,
  isLessThanSm: false,
  handleEdit: jest.fn(),
};

describe('SearchSummary Component', () => {
  it('renders component', () => {
    const { getByText } = render(
      <SearchSummary {...defaultProps} style={{ containerStyle: { borderRadius: '50px' } }} />
    );
    expect(getByText('Manchester, Uk')).toBeInTheDocument();
  });

  it('renders location, date and room summary', () => {
    const { getByText } = render(<SearchSummary {...defaultProps} />);
    expect(getByText('Manchester, Uk')).toBeInTheDocument();
    expect(getByText('21 Aug - 23 Aug')).toBeInTheDocument();
    expect(getByText('1 adult, 1 double room')).toBeInTheDocument();
  });

  it('calls handleEdit function on click', () => {
    const { getByText } = render(<SearchSummary {...defaultProps} isLessThanLg />);
    expect(getByText('Edit')).toBeInTheDocument();
    getByText('Edit').click();
    expect(defaultProps.handleEdit).toBeCalled();
  });

  it('calls handleEdit button on click on less than sm', () => {
    const { getByText } = render(<SearchSummary {...defaultProps} isLessThanSm />);
    expect(getByText('Edit')).toBeInTheDocument();
    getByText('Edit').click();
    expect(defaultProps.handleEdit).toBeCalled();
  });

  it('returns null if isSummaryActive false', () => {
    defaultProps.isSummaryActive = false;
    const { queryByTestId } = render(<SearchSummary {...defaultProps} isLessThanSm />);
    expect(queryByTestId('search-summary-container')).toBeNull();
  });
});
