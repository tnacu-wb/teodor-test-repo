import '@testing-library/jest-dom';
import { fireEvent } from '@testing-library/react';

import { render, screen } from '../../../utils/test-utils';
import SplitView from './SplitView.component';

jest.mock('../SearchResultsPIVariant.container', () => ({
  displayNoHotelsFoundWarning: jest.fn((description) => (
    <div data-testid="no-hotels-warning">{description}</div>
  )),
  notificationWrapperStyles: {},
}));

jest.mock('../../../index', () => ({
  ListView: (props) => (
    <div data-testid="list-view">
      <span data-testid="list-view-price-per-night">{String(props.pricePerNight)}</span>
      <button data-testid="list-view-toggle" onClick={props.onTogglePricePerNight}>
        toggle
      </button>
    </div>
  ),
  MapViewPIVariant: (props) => <div data-testid="map-view">{props.items.length} items</div>,
}));

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  SRControls: (props) => (
    <div data-testid="srcontrols">
      <span data-testid="hide-view-toggle">{String(props.hideViewToggle)}</span>
      {props.rightContent}
    </div>
  ),
}));

const baseProps: any = {
  isCompactSplitView: false,
  controlsLabels: { filterLabels: {}, buttonLabels: {} },
  multiSearchParams: { sort: 'DISTANCE', numberOfNights: 1 },
  changeViewType: jest.fn(),
  changeSortValue: jest.fn(),
  handleChangeFilters: jest.fn(),
  defaultFilters: [],
  isPiSortOrderDropdownEnabled: false,
  dynamicFilters: { dynamicFilters: [], filters: { label: '', info: '' } },
  channel: 'PI',
  showSplitViewTotalResults: true,
  showSplitViewPricePerNightToggle: false,
  resultsMeta: { total: 150, currentResults: 20 },
  partialTranslations: {
    searchInformation: {
      content: {
        totalHotels: 'Hotels found',
        results: { notifications: { noFilteredHotels: 'No hotels match your filters' } },
      },
    },
  },
  baseDataTestId: 'SRP',
  t: (key) => key,
  shouldDisplayNoHotelsWarning: false,
  headerInformationData: {
    headerInformation: { results: { notifications: { noResults: 'No hotels found' } } },
  },
  promotionBannerData: undefined,
  hasHotels: true,
  headerAnnouncement: undefined,
  isLoading: false,
  filters: '',
  currentPage: 1,
  availabilityResult: null,
  language: 'en',
  roomTypes: ['DB'],
  hotelList: [],
  fetchNewHotels: jest.fn(),
  items: Array.from({ length: 5 }, (_, index) => ({ name: `hotel-${index}` })),
  isPricePerNightEnabledOnPi: true,
  hasMore: false,
};

describe('SplitView', () => {
  it('renders SRControls with the view toggle hidden', () => {
    render(<SplitView {...baseProps} />);

    expect(screen.getByTestId('srcontrols')).toBeInTheDocument();
    expect(screen.getByTestId('hide-view-toggle')).toHaveTextContent('true');
  });

  it('shows the total results count when showSplitViewTotalResults is true', () => {
    render(<SplitView {...baseProps} />);

    expect(screen.getByText('150 Hotels found')).toBeInTheDocument();
  });

  it('does not show the total results count when showSplitViewTotalResults is false', () => {
    render(<SplitView {...baseProps} showSplitViewTotalResults={false} />);

    expect(screen.queryByText('150 Hotels found')).not.toBeInTheDocument();
  });

  it('does not show the price-per-night toggle when showSplitViewPricePerNightToggle is false', () => {
    render(<SplitView {...baseProps} showSplitViewPricePerNightToggle={false} />);

    expect(
      screen.queryByTestId('SRP-price-per-night-rooms-tp-SwitchToggle')
    ).not.toBeInTheDocument();
  });

  it('shows the price-per-night toggle and flips the value passed to ListView', () => {
    render(<SplitView {...baseProps} showSplitViewPricePerNightToggle />);

    expect(screen.getByTestId('list-view-price-per-night')).toHaveTextContent('false');

    fireEvent.click(screen.getByTestId('list-view-toggle'));

    expect(screen.getByTestId('list-view-price-per-night')).toHaveTextContent('true');
  });

  it('shows the loading spinner instead of ListView while isLoading is true', () => {
    render(<SplitView {...baseProps} isLoading />);

    expect(screen.getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
    expect(screen.queryByTestId('list-view')).not.toBeInTheDocument();
  });

  it('renders ListView once loading has finished', () => {
    render(<SplitView {...baseProps} isLoading={false} />);

    expect(screen.getByTestId('list-view')).toBeInTheDocument();
  });

  it('shows the no-hotels warning when shouldDisplayNoHotelsWarning is true', () => {
    render(<SplitView {...baseProps} shouldDisplayNoHotelsWarning />);

    expect(screen.getByTestId('no-hotels-warning')).toHaveTextContent('No hotels found');
  });

  it('shows the no-filtered-hotels warning when filters are applied and there are no hotels', () => {
    render(
      <SplitView
        {...baseProps}
        hasHotels={false}
        filters="CPF"
        shouldDisplayNoHotelsWarning={false}
      />
    );

    expect(screen.getByTestId('no-hotels-warning')).toHaveTextContent(
      'No hotels match your filters'
    );
  });

  it('passes every loaded hotel to the map view, uncapped, so the map can zoom out to fit them all', () => {
    const items = Array.from({ length: 60 }, (_, index) => ({ name: `hotel-${index}` })) as any;
    render(<SplitView {...baseProps} items={items} />);

    expect(screen.getByTestId('map-view')).toHaveTextContent('60 items');
  });

  it('does not render the map view while isLoading is true', () => {
    render(<SplitView {...baseProps} isLoading />);

    expect(screen.queryByTestId('map-view')).not.toBeInTheDocument();
  });
});
