import { render } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { useHorizontalScroll } from '@whitbread-eos/utils';

import { generateMonthData } from '../utils/common/date-utils';
import MonthTabsCarousel from './MonthTabsCarousel';

if (typeof window !== 'undefined' && typeof window.HTMLElement !== 'undefined') {
  if (!window.HTMLElement.prototype.scrollTo) {
    /* istanbul ignore next */
    window.HTMLElement.prototype.scrollTo = function () {
      // noop for testing
    };
  }
}

const MONTHS = [
  'January',
  'February',
  'March',
  'April',
  'May',
  'June',
  'July',
  'August',
  'September',
  'October',
  'November',
  'December',
];

const mockScrollToLeft = jest.fn();
const mockScrollToRight = jest.fn();
const mockCheckScrollButtons = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  useHorizontalScroll: jest.fn(),
  getNoOfDaysInYear: jest.fn().mockReturnValue(365),
  formatCurrency: jest.fn().mockReturnValue('£'),
  formatPrice: jest.fn().mockReturnValue('£99'),
  MONTHS: MONTHS,
  cn: (...args: string[]) => args.filter(Boolean).join(' '),
}));

const mockUseHorizontalScroll = useHorizontalScroll as jest.MockedFunction<
  typeof useHorizontalScroll
>;

const setLowestPrice = jest.fn();

describe('MonthTabsCarousel', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    // Default mock implementation
    mockUseHorizontalScroll.mockReturnValue({
      scrollRef: { current: null },
      scrollToLeft: mockScrollToLeft,
      scrollToRight: mockScrollToRight,
      canScrollLeft: false,
      canScrollRight: false,
      hasOverflow: false,
      checkScrollButtons: mockCheckScrollButtons,
    });
  });
  it('renders month tabs and handles month change', () => {
    const handleMonthChange = jest.fn();
    const { getAllByRole } = render(
      <MonthTabsCarousel
        maxMonths={3}
        onMonthChange={handleMonthChange}
        locale="en-GB"
        fullMonthParams="May 2026"
      />
    );
    const buttons = getAllByRole('button');
    expect(buttons.length).toBeGreaterThan(0);
    userEvent.click(buttons[1]);
    expect(handleMonthChange).toHaveBeenCalled();
  });

  it('renders with default props', () => {
    const { getAllByRole } = render(<MonthTabsCarousel />);
    const buttons = getAllByRole('button');
    expect(buttons.length).toBeGreaterThan(0);
  });

  it('renders with initialMonthValue', () => {
    // Get a valid month value from generateMonthData
    const months = generateMonthData(3, 'en-GB');
    const validMonthValue = months[1].value;
    const { getAllByRole } = render(
      <MonthTabsCarousel
        maxMonths={3}
        initialMonthValue={validMonthValue}
        locale="en-GB"
        fullMonthParams="May 2026"
      />
    );
    const buttons = getAllByRole('button');
    expect(buttons.some((btn) => btn.getAttribute('datamonthvalue') === validMonthValue)).toBe(
      false
    );
  });

  it('calls onMonthChange with correct month data', () => {
    const handleMonthChange = jest.fn();
    const { getAllByRole } = render(
      <MonthTabsCarousel maxMonths={3} onMonthChange={handleMonthChange} locale="en-GB" />
    );
    const buttons = getAllByRole('button');
    userEvent.click(buttons[2]);
    expect(handleMonthChange).toHaveBeenCalledWith(
      expect.objectContaining({ value: expect.any(String) })
    );
  });

  it('renders scroll buttons when overflow exists', () => {
    mockUseHorizontalScroll.mockReturnValue({
      scrollRef: { current: document.createElement('div') },
      scrollToLeft: mockScrollToLeft,
      scrollToRight: mockScrollToRight,
      canScrollLeft: true,
      canScrollRight: true,
      hasOverflow: true,
      checkScrollButtons: mockCheckScrollButtons,
    });

    const { getAllByRole } = render(
      <MonthTabsCarousel maxMonths={12} setLowestPrice={setLowestPrice} />
    );
    // 12 months + 2 scroll buttons (always in DOM)
    const buttons = getAllByRole('button');
    expect(buttons.length).toBe(14); // 12 months + 2 scroll buttons
  });

  it('handles invalid initialMonthValue gracefully', () => {
    const { getAllByRole } = render(<MonthTabsCarousel maxMonths={3} initialMonthValue="" />);
    const buttons = getAllByRole('button');
    expect(buttons.length).toBeGreaterThan(0);
  });

  it('should call setLowestPrice when switching between month tabs', () => {
    const months = generateMonthData(3, 'en-GB');
    const firstMonth = months[0];

    const { getAllByRole } = render(
      <MonthTabsCarousel
        maxMonths={3}
        initialMonthValue={firstMonth.value}
        setLowestPrice={setLowestPrice}
        locale="en-GB"
      />
    );

    const buttons = getAllByRole('button');
    userEvent.click(buttons[1]);

    expect(setLowestPrice).toHaveBeenCalledWith({
      price: 0,
      currency: '',
    });
  });

  it('renders with empty months array when no months available', () => {
    const { container } = render(<MonthTabsCarousel maxMonths={0} />);
    expect(container.firstChild).toBeInTheDocument();
  });

  it('handles initialMonthValue that matches available months', () => {
    const months = generateMonthData(6, 'en-GB');
    const validMonthValue = months[2].value; // Third month

    const { container } = render(
      <MonthTabsCarousel maxMonths={6} initialMonthValue={validMonthValue} />
    );

    expect(container.firstChild).toBeInTheDocument();
  });

  it('hides scroll buttons when overflow is false', () => {
    mockUseHorizontalScroll.mockReturnValue({
      scrollRef: { current: document.createElement('div') },
      scrollToLeft: mockScrollToLeft,
      scrollToRight: mockScrollToRight,
      canScrollLeft: false,
      canScrollRight: false,
      hasOverflow: false,
      checkScrollButtons: mockCheckScrollButtons,
    });

    const { getByLabelText } = render(<MonthTabsCarousel maxMonths={2} />);

    const leftButton = getByLabelText('left scroll button');
    const rightButton = getByLabelText('right scroll button');

    // Button wrappers have opacity 0 and pointer-events none
    const leftWrapper = leftButton.parentElement;
    const rightWrapper = rightButton.parentElement;

    expect(leftWrapper).toHaveStyle('opacity: 0');
    expect(leftWrapper).toHaveStyle('pointer-events: none');
    expect(rightWrapper).toHaveStyle('opacity: 0');
    expect(rightWrapper).toHaveStyle('pointer-events: none');
  });

  it('covers edge case with months array having empty value', () => {
    const { container } = render(<MonthTabsCarousel maxMonths={1} initialMonthValue="" />);
    expect(container.firstChild).toBeInTheDocument();
  });

  it('handles onMonthChange callback not being provided', () => {
    const months = generateMonthData(3, 'en-GB');
    const { getAllByRole, container } = render(
      <MonthTabsCarousel maxMonths={3} initialMonthValue={months[0].value} />
    );

    const buttons = getAllByRole('button');
    userEvent.click(buttons[1]);

    expect(container.firstChild).toBeInTheDocument();
  });

  it('calls scrollToLeft when left scroll button is clicked', () => {
    mockUseHorizontalScroll.mockReturnValue({
      scrollRef: { current: document.createElement('div') },
      scrollToLeft: mockScrollToLeft,
      scrollToRight: mockScrollToRight,
      canScrollLeft: true,
      canScrollRight: true,
      hasOverflow: true,
      checkScrollButtons: mockCheckScrollButtons,
    });

    const { getByLabelText } = render(<MonthTabsCarousel maxMonths={12} fullMonthParams="May" />);
    const leftButton = getByLabelText('left scroll button');

    userEvent.click(leftButton);

    expect(mockScrollToLeft).toHaveBeenCalledTimes(1);
  });

  it('calls scrollToRight when right scroll button is clicked', () => {
    mockUseHorizontalScroll.mockReturnValue({
      scrollRef: { current: document.createElement('div') },
      scrollToLeft: mockScrollToLeft,
      scrollToRight: mockScrollToRight,
      canScrollLeft: true,
      canScrollRight: true,
      hasOverflow: true,
      checkScrollButtons: mockCheckScrollButtons,
    });

    const { getByLabelText } = render(<MonthTabsCarousel maxMonths={12} />);
    const rightButton = getByLabelText('right scroll button');

    userEvent.click(rightButton);

    expect(mockScrollToRight).toHaveBeenCalledTimes(1);
  });

  it('sets up scroll event listener when ref is available', () => {
    const mockElement = document.createElement('div');

    mockUseHorizontalScroll.mockReturnValue({
      scrollRef: { current: mockElement },
      scrollToLeft: mockScrollToLeft,
      scrollToRight: mockScrollToRight,
      canScrollLeft: false,
      canScrollRight: false,
      hasOverflow: false,
      checkScrollButtons: mockCheckScrollButtons,
    });

    render(<MonthTabsCarousel maxMonths={3} />);

    expect(mockCheckScrollButtons).toHaveBeenCalled();
  });
});
