import { useMediaQuery } from '@chakra-ui/react';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { SORT_BY_VALUES, THEME_COLORS } from '../HotelsPriceTable/constants';
import PriceFinderRoomSortToggle from './PriceFinderRoomSortToggle';

const handleSortChange = jest.fn();
const handleToggleFilterMenu = jest.fn();

jest.mock('@chakra-ui/react', () => {
  const actual = jest.requireActual('@chakra-ui/react');
  return {
    ...actual,
    useMediaQuery: jest.fn().mockReturnValue([false]),
  };
});

jest.mock('next-i18next', () => {
  // eslint-disable-next-line @typescript-eslint/no-require-imports
  const React = require('react');

  const TestComponent = ({ children, ...props }: any) =>
    React.createElement('div', props, children);
  TestComponent.displayName = 'MockTrans';

  const t = (key: string) => {
    switch (key) {
      case 'priceFinder.filterByRoomTypes':
        return 'Filter by room types';
      case 'priceFinder.filterByRoomTypesMobile':
        return 'Room types';
      case 'priceFinder.sortByDistance':
        return 'Sort by distance';
      case 'search.roomType':
        return 'Room type';
      default:
        return key;
    }
  };

  return {
    __esModule: true,
    useTranslation: () => ({ t }),
    withTranslation: () => (WrappedComponent: any) => {
      const Wrapped = (props: any) => React.createElement(WrappedComponent, { ...props, t });
      Wrapped.displayName = `withTranslation(${
        WrappedComponent.displayName || WrappedComponent.name || 'Component'
      })`;
      return Wrapped;
    },
    i18n: {
      language: 'en',
      changeLanguage: jest.fn(),
    },
    Trans: TestComponent,
  };
});

jest.mock('@whitbread-eos/utils', () => ({
  __esModule: true,
  formatDataTestId: (base: string, key: string) => `${base}-${key}`,
  cn: (...args: string[]) => args.filter(Boolean).join(' '),
}));

const baseProps = {
  sortedByDate: null as string | null,
  handleSortChange,
  handleToggleFilterMenu,
  isMobile: true,
  toggleFilterMenu: false,
  baseDataTestId: 'PriceFinderRoomSortToggle',
  filterSelection: { single: false, double: true },
  isRoomTypeFilterEnabled: true,
};

const mockUseMediaQuery = useMediaQuery as jest.MockedFunction<typeof useMediaQuery>;

describe('PriceFinderRoomSortToggle', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUseMediaQuery.mockReturnValue([false] as any);
  });

  it('should render mobile sort toggle and triggers sort handler when clicked', async () => {
    render(<PriceFinderRoomSortToggle {...baseProps} />);

    const sortToggle = screen.getByText(/Sort by distance/i);
    expect(sortToggle).toBeInTheDocument();

    await userEvent.click(sortToggle);
    expect(handleSortChange).toHaveBeenCalledTimes(1);
    expect(handleSortChange).toHaveBeenCalledWith(SORT_BY_VALUES.DISTANCE);
  });

  it('should show selected filters count and toggles filter handler', async () => {
    render(<PriceFinderRoomSortToggle {...baseProps} />);

    expect(screen.getByText('(1)')).toBeInTheDocument();

    const filterButton = screen.getByText(/Room type|Filter by room type/i);
    expect(filterButton).toBeInTheDocument();

    await userEvent.click(filterButton);
    expect(handleToggleFilterMenu).toHaveBeenCalledTimes(1);
  });

  it('should render small-mobile label when media query matches (isSmallMobile true)', async () => {
    mockUseMediaQuery.mockReturnValue([true] as any);

    render(<PriceFinderRoomSortToggle {...baseProps} />);

    expect(screen.getByText('Room types')).toBeInTheDocument();
  });

  it('should render default mobile label when media query does not match (isSmallMobile false)', () => {
    mockUseMediaQuery.mockReturnValue([false] as any);

    render(<PriceFinderRoomSortToggle {...baseProps} />);
    expect(screen.getByText(/Filter by room type/i)).toBeInTheDocument();
  });

  it('should use disabled color for distance icon when sortedByDate is truthy', () => {
    const props = { ...baseProps, sortedByDate: '2020-01-01' };
    render(<PriceFinderRoomSortToggle {...props} />);

    const icon = screen.getByLabelText('Sorted by distance ascending');
    expect(icon).toBeInTheDocument();
    expect(icon).toHaveStyle({ color: THEME_COLORS.disabledColor });
  });

  it('should render desktop (non-mobile) filter toggle with chevrons and triggers handler', async () => {
    const props = {
      ...baseProps,
      isMobile: false,
      filterSelection: { single: false, double: false },
    };
    render(<PriceFinderRoomSortToggle {...props} />);
    expect(screen.getByText(/Filter by room types/i)).toBeInTheDocument();

    const chevronDown = screen.getByTestId('PriceFinderRoomSortToggle-chevronDown');
    expect(chevronDown).toBeInTheDocument();

    const filterToggle = screen.getByText(/Filter by room types/i);
    await userEvent.click(filterToggle);
    expect(handleToggleFilterMenu).toHaveBeenCalledTimes(1);
  });
});
