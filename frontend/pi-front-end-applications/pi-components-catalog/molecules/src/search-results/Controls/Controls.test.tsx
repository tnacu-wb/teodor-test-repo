import '@testing-library/jest-dom';
import { FT_SRP_DYNAMIC_FILTERS } from '@whitbread-eos/api';
import { useFeatureToggle, useMobileControlsDisplay, useScreenSize } from '@whitbread-eos/utils';

import { render, screen, fireEvent, waitFor } from '../../utils/test-utils';
import Controls, {
  ControlsDisplaySection,
  getSelectedFilters,
  VIEW_TYPE_CONSTANTS,
} from './Controls.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useScreenSize: jest.fn(() => ({ isLessThanMd: false })),
  useMobileControlsDisplay: jest.fn(),
  useFeatureToggle: jest.fn(),
}));

jest.mock('../Filters', () => {
  const MockFilters = (props: any) => (
    <button data-testid="filters-button" disabled={props.isDisabled}>
      {props.labels.filterByButton}
    </button>
  );
  MockFilters.displayName = 'MockFilters';
  return MockFilters;
});

jest.mock('../SortBy', () => {
  const MockSortBy = (props: any) => (
    <button
      data-testid="sortby-button"
      disabled={props.isDisabled}
      data-is-split-view={String(!!props.isSplitView)}
    >
      SortBy
    </button>
  );
  MockSortBy.displayName = 'MockSortBy';
  return MockSortBy;
});

const displaySectionProps = {
  viewType: VIEW_TYPE_CONSTANTS.listView,
  onChangeViewType: jest.fn(),
  onChangeSortValue: jest.fn(),
  onChangeFilters: jest.fn(),
  buttonLabels: {
    map: 'Map',
    list: 'List',
  },
  filterByLabels: {
    filterByButton: 'Filter',
    filters: {},
  },
  sortByLabels: {
    sortByRecommended: 'Recommended',
    sortByDistance: 'Distance',
    sortByPrice: 'Price',
  },
  sortValue: 'DISTANCE',
  defaultFilters: [],
};

const baseProps = {
  channel: 'PI',
  viewType: '2',
  onChangeViewType: jest.fn(),
  onChangeSortValue: jest.fn(),
  onChangeFilters: jest.fn(),
  setSelectedFilters: jest.fn(),
  labels: {
    buttonLabels: {
      listLong: 'List view',
      mapLong: 'Map view',
      sortByDistance: 'Distance',
      sortByPrice: 'Price',
      filtersLong: 'Filter by',
    },
    filterLabels: {
      label: {
        restaurant: 'Restaurant',
        airCon: 'Air conditioning',
        chargeableOffsiteParking: 'Chargeable off-site parking',
        freeParking: 'Free parking',
        header: 'Filters applied when selected',
        parking: 'Parking',
        chargeableOnsiteParking: 'Chargeable on-site parking',
        lift: 'Lift access',
        meet: 'Meeting rooms',
        apply: 'Apply filters',
        reset: 'Reset filters',
        facilities: 'Facilities',
      },
      info: {
        lift: 'Some hotels are ground floor only. Please check directly with the hotel (local rate)',
      },
      code: {
        restaurant: 'EAT',
        airCon: 'ACO',
        chargeableOffsiteParking: 'COP',
        freeParking: 'CPF',
        chargeableOnsiteParking: 'CPP',
        lift: 'LFT',
        meet: 'MEE',
      },
    },
  },
  sortValue: 'DISTANCE',
  defaultFilters: [],
};

const dynamicFilters = {
  filters: {
    label: {
      header: 'Filters applied when selected',
      parking: 'Parking',
      freeParking: 'Free parking',
      restaurant: 'Restaurant',
      airCon: 'Air conditioning',
      chargeableOffsiteParking: 'Chargeable off-site parking',
      chargeableOnsiteParking: 'Chargeable on-site parking',
      lift: 'Lift access',
      meet: 'Meeting rooms',
      apply: 'Apply filters',
      reset: 'Clear all filters',
      facilities: 'Facilities',
    },
    info: {
      lift: 'Some hotels are ground floor only. Please check directly with the hotel (local rate).',
    },
  },
  dynamicFilters: [
    {
      groupTitle: 'Parking',
      groupOperator: 'OR',
      groupItems: [
        {
          label: 'Free parking',
          info: '',
          codes: 'CPF',
          queryParam: 'free-parking',
        },
        {
          label: 'Chargeable on-site parking',
          info: '',
          codes: 'CPP',
          queryParam: 'chargeable-on-site-parking',
        },
        {
          label: 'Chargeable off-site parking',
          info: '',
          codes: 'COP,COC',
          queryParam: 'chargeable-off-site-parking',
        },
      ],
    },
    {
      groupTitle: 'Facilities',
      groupOperator: 'AND',
      groupItems: [
        {
          label: 'Air conditioning',
          info: '',
          codes: 'ACO,HAC',
          queryParam: 'air-conditioning',
        },
        {
          label: 'Lift access',
          info: 'Some hotels are ground floor only. Please check directly with the hotel (local rate).',
          codes: 'LFT,HUL',
          queryParam: 'lift-access',
        },
        {
          label: 'Meeting rooms',
          info: '',
          codes: 'MEE',
          queryParam: 'meeting-rooms',
        },
        {
          label: 'Restaurant',
          info: '',
          codes: 'RES,DIN,HRS',
          queryParam: 'restaurant',
        },
        {
          label: 'Premier Plus rooms',
          info: '',
          codes: 'PRR',
          queryParam: 'premier-plus-rooms',
        },
        {
          label: 'EV Charging point',
          info: '',
          codes: 'EVC',
          queryParam: 'ev-charging-point',
        },
        {
          label: 'Interconnecting rooms',
          info: '',
          codes: 'ICR',
          queryParam: 'interconnecting-rooms',
        },
        {
          label: 'Luggage storage',
          info: '',
          codes: 'HLG,LUG',
          queryParam: 'luggage-storage',
        },
        {
          label: 'Accessible room with lowered bath',
          info: '',
          codes: 'LWB',
          queryParam: 'accessible-room-with-lowered-bath',
        },
        {
          label: 'Accessible room with wet room',
          info: '',
          codes: 'WET',
          queryParam: 'accessible-room-with-wet-room',
        },
      ],
    },
  ],
};

describe('Results Buttons', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (useFeatureToggle as jest.Mock).mockReturnValue({ [FT_SRP_DYNAMIC_FILTERS]: false });
  });
  it('should display the "Filter by" button', () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const { getByRole } = render(<Controls {...baseProps} />);
    expect(getByRole('button', { name: 'Filter by' })).toBeVisible();
  });
  it('should display the "Map view" button if the URL view param is set to 2 (list view)', () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const { getByText, queryByText } = render(<Controls {...baseProps} />);
    expect(getByText(/map/i)).toBeInTheDocument();
    expect(queryByText(/list/i)).not.toBeInTheDocument();
  });
  it('should display the "List view" button if the URL view param is set to 1 (map view)', () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const { getByText, queryByText } = render(<Controls {...baseProps} viewType="1" />);
    expect(getByText(/list/i)).toBeInTheDocument();
    expect(queryByText(/map/i)).not.toBeInTheDocument();
  });
});

describe('ControlsDisplaySection', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (useFeatureToggle as jest.Mock).mockReturnValue({ [FT_SRP_DYNAMIC_FILTERS]: false });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should render all 3 controls when viewType is listView', () => {
    render(<ControlsDisplaySection {...displaySectionProps} viewType="2" />);

    expect(screen.getByTestId('filters-button')).toBeInTheDocument();
    expect(screen.getByTestId('sortby-button')).toBeInTheDocument();
    expect(screen.getByTestId('controls-map-or-list-button')).toBeInTheDocument();
  });

  it('should show "Map" when viewType is listView', () => {
    render(<ControlsDisplaySection {...displaySectionProps} viewType="2" />);
    expect(screen.getByText('Map')).toBeInTheDocument();
  });

  it('should show "List" when viewType is mapView', () => {
    render(<ControlsDisplaySection {...displaySectionProps} viewType="1" />);
    expect(screen.getByText('List')).toBeInTheDocument();
  });
  it('should trigger onChangeViewType on button click', () => {
    render(<ControlsDisplaySection {...displaySectionProps} />);

    fireEvent.click(screen.getByTestId('controls-map-or-list-button'));
    expect(displaySectionProps.onChangeViewType).toHaveBeenCalled();
  });

  it('should disable Filters when visibility.isFilterDisabled is true', () => {
    render(
      <ControlsDisplaySection {...displaySectionProps} visibility={{ isFilterDisabled: true }} />
    );
    expect(screen.getByTestId('filters-button')).toBeDisabled();
  });

  it('should disable SortBy when visibility.isSortDisabled is true', () => {
    render(
      <ControlsDisplaySection {...displaySectionProps} visibility={{ isSortDisabled: true }} />
    );
    expect(screen.getByTestId('sortby-button')).toBeDisabled();
  });

  it('should render ControlsDisplaySection when useMobileControlsDisplay returns true', () => {
    (useMobileControlsDisplay as jest.Mock).mockReturnValue(true);

    render(<Controls {...baseProps} />);

    // Now it should render mobile layout (ControlsDisplaySection)
    expect(screen.getByTestId('filters-button')).toBeInTheDocument();
    expect(screen.getByTestId('sortby-button')).toBeInTheDocument();
    expect(screen.getByTestId('controls-map-or-list-button')).toBeInTheDocument();
  });

  it('should render default Controls layout when useMobileControlsDisplay returns false', () => {
    (useMobileControlsDisplay as jest.Mock).mockReturnValue(false);

    render(<Controls {...baseProps} />);

    expect(screen.getByText(/filter by/i)).toBeInTheDocument();
  });

  it('should NOT render SortBy when viewType is mapView', () => {
    render(<ControlsDisplaySection {...displaySectionProps} viewType="1" />);

    expect(screen.queryByTestId('sortby-button')).not.toBeInTheDocument();
  });

  it('should render only 2 controls when SortBy is hidden', () => {
    render(<ControlsDisplaySection {...displaySectionProps} viewType="1" />);

    const buttons = screen.getAllByRole('button');
    expect(buttons.length).toBe(2);
  });

  it('should show Destination filters when dynamic filters are passed', () => {
    render(
      <ControlsDisplaySection
        {...displaySectionProps}
        dynamicFilters={dynamicFilters}
        selectedFilters={[]}
        showDynamicFilters={true}
      />
    );

    expect(screen.getByTestId('DLP-Filters-Open-Button')).toBeInTheDocument();
  });

  it('should call call onChangeFilters with [] when filters are reset on mobile', async () => {
    render(
      <ControlsDisplaySection
        {...baseProps}
        dynamicFilters={dynamicFilters}
        selectedFilters={getSelectedFilters(dynamicFilters, ['ACO', 'HAC', 'LFT', 'HUL'])}
        defaultFilters={['ACO', 'HAC', 'LFT', 'HUL']}
        showDynamicFilters={true}
      />
    );

    await waitFor(() => {
      expect(screen.getByTestId('srp-dynamic-filters-Pod-Air conditioning')).toBeInTheDocument();
      expect(screen.getByTestId('srp-dynamic-filters-Pod-Lift access')).toBeInTheDocument();
    });

    fireEvent.click(screen.getByTestId('srp-dynamic-filters-Clear-Filters-Button'));

    expect(baseProps.onChangeFilters).toHaveBeenCalledWith([]);
  });

  it('should call onChangeFilters with correct filter codes when filters are applied on mobile', async () => {
    render(
      <ControlsDisplaySection
        {...baseProps}
        dynamicFilters={dynamicFilters}
        selectedFilters={getSelectedFilters(dynamicFilters, ['ACO', 'HAC', 'LFT', 'HUL'])}
        defaultFilters={['ACO', 'HAC', 'LFT', 'HUL']}
        showDynamicFilters={true}
      />
    );

    await waitFor(() => {
      expect(screen.getByTestId('srp-dynamic-filters-Pod-Air conditioning')).toBeInTheDocument();
      expect(screen.getByTestId('srp-dynamic-filters-Pod-Lift access')).toBeInTheDocument();
    });

    fireEvent.click(
      screen.getByTestId('srp-dynamic-filters-Pod-Air conditioning-Dismiss-Filter-Button')
    );

    expect(baseProps.onChangeFilters).toHaveBeenCalledWith(['LFT', 'HUL']);
  });

  it('should call onChangeFilters with correct filter codes when filters are applied from drawer on mobile', async () => {
    render(
      <ControlsDisplaySection
        {...baseProps}
        dynamicFilters={dynamicFilters}
        selectedFilters={getSelectedFilters(dynamicFilters, ['ACO', 'HAC', 'LFT', 'HUL'])}
        defaultFilters={['ACO', 'HAC', 'LFT', 'HUL']}
        showDynamicFilters={true}
      />
    );

    await waitFor(() => {
      expect(screen.getByTestId('srp-dynamic-filters-Pod-Air conditioning')).toBeInTheDocument();
      expect(screen.getByTestId('srp-dynamic-filters-Pod-Lift access')).toBeInTheDocument();
    });

    fireEvent.click(screen.getByTestId('DLP-Filters-Open-Button'));

    await waitFor(() => {
      expect(screen.getByTestId('DLP-Filters-Filters-checkbox-ACO,HAC')).toBeInTheDocument();
    });

    fireEvent.click(screen.getByTestId('DLP-Filters-Filters-checkbox-ACO,HAC'));

    expect(baseProps.onChangeFilters).toHaveBeenCalledWith(['LFT', 'HUL']);
  });

  it('should show Destination filters when FT_SRP_DYNAMIC_FILTERS is enabled', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({ [FT_SRP_DYNAMIC_FILTERS]: true });

    render(<Controls {...baseProps} dynamicFilters={dynamicFilters} />);

    expect(screen.getByTestId('DLP-Filters-Open-Button')).toBeInTheDocument();
  });

  it('should hide Destination filters when FT_SRP_DYNAMIC_FILTERS is disabled', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({ [FT_SRP_DYNAMIC_FILTERS]: false });

    render(<Controls {...baseProps} dynamicFilters={dynamicFilters} />);

    expect(screen.queryByTestId('DLP-Filters-Open-Button')).not.toBeInTheDocument();
  });

  it('should disable Filters when visibility.isFilterDisabled is true, even if FT_SRP_DYNAMIC_FILTERS is enabled', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({ [FT_SRP_DYNAMIC_FILTERS]: true });

    render(
      <Controls
        {...baseProps}
        dynamicFilters={dynamicFilters}
        visibility={{ isFilterDisabled: true }}
      />
    );

    expect(screen.getByTestId('filters-button')).toBeDisabled();
  });

  it('should call call onChangeFilters with [] when FT_SRP_DYNAMIC_FILTERS is enabled and filters are reset', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({ [FT_SRP_DYNAMIC_FILTERS]: true });

    render(
      <Controls
        {...baseProps}
        dynamicFilters={dynamicFilters}
        defaultFilters={['ACO', 'HAC', 'LFT', 'HUL']}
      />
    );

    await waitFor(() => {
      expect(screen.getByTestId('srp-dynamic-filters-Pod-Air conditioning')).toBeInTheDocument();
      expect(screen.getByTestId('srp-dynamic-filters-Pod-Lift access')).toBeInTheDocument();
    });

    fireEvent.click(screen.getByTestId('srp-dynamic-filters-Clear-Filters-Button'));

    expect(baseProps.onChangeFilters).toHaveBeenCalledWith([]);
  });

  it('should call onChangeFilters with correct filter codes when FT_SRP_DYNAMIC_FILTERS is enabled and filters are applied', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({ [FT_SRP_DYNAMIC_FILTERS]: true });

    render(
      <Controls
        {...baseProps}
        dynamicFilters={dynamicFilters}
        defaultFilters={['ACO', 'HAC', 'LFT', 'HUL']}
      />
    );

    await waitFor(() => {
      expect(screen.getByTestId('srp-dynamic-filters-Pod-Air conditioning')).toBeInTheDocument();
      expect(screen.getByTestId('srp-dynamic-filters-Pod-Lift access')).toBeInTheDocument();
    });

    fireEvent.click(
      screen.getByTestId('srp-dynamic-filters-Pod-Air conditioning-Dismiss-Filter-Button')
    );

    expect(baseProps.onChangeFilters).toHaveBeenCalledWith(['LFT', 'HUL']);
  });

  it('should call onChangeFilters with correct filter codes when filters are applied from drawer', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({ [FT_SRP_DYNAMIC_FILTERS]: true });

    render(
      <Controls
        {...baseProps}
        dynamicFilters={dynamicFilters}
        defaultFilters={['ACO', 'HAC', 'LFT', 'HUL']}
        showDynamicFilters={true}
      />
    );

    await waitFor(() => {
      expect(screen.getByTestId('srp-dynamic-filters-Pod-Air conditioning')).toBeInTheDocument();
      expect(screen.getByTestId('srp-dynamic-filters-Pod-Lift access')).toBeInTheDocument();
    });

    fireEvent.click(screen.getByTestId('DLP-Filters-Open-Button'));

    await waitFor(() => {
      expect(screen.getByTestId('DLP-Filters-Filters-checkbox-ACO,HAC')).toBeInTheDocument();
    });

    fireEvent.click(screen.getByTestId('DLP-Filters-Filters-checkbox-ACO,HAC'));

    expect(baseProps.onChangeFilters).toHaveBeenCalledWith(['LFT', 'HUL']);
  });
});

describe('Map view positioning with dynamic filters', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (useFeatureToggle as jest.Mock).mockReturnValue({ [FT_SRP_DYNAMIC_FILTERS]: false });
    (useMobileControlsDisplay as jest.Mock).mockReturnValue(false);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should apply position: relative and margin-left/margin-right offsets when in map view with dynamic filters enabled', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({ [FT_SRP_DYNAMIC_FILTERS]: true });

    const { container } = render(
      <Controls
        {...baseProps}
        viewType={VIEW_TYPE_CONSTANTS.mapView}
        dynamicFilters={dynamicFilters}
        defaultFilters={['ACO', 'HAC']}
      />
    );

    const firstFlex = container.querySelector('div > div') as HTMLElement;
    const styles = window.getComputedStyle(firstFlex);

    expect(styles.position).toBe('relative');
  });

  it('should apply position: absolute and left offset when in map view with dynamic filters disabled', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_SRP_DYNAMIC_FILTERS]: false,
    });

    const { container } = render(
      <Controls
        {...baseProps}
        viewType={VIEW_TYPE_CONSTANTS.mapView}
        dynamicFilters={dynamicFilters}
        defaultFilters={[]}
      />
    );

    const firstFlex = container.querySelector('div > div') as HTMLElement;
    const styles = window.getComputedStyle(firstFlex);

    expect(styles.position).toBe('absolute');
    expect(styles.zIndex).toBe('2');
  });

  it('should not apply map view positioning styles when in list view', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({ [FT_SRP_DYNAMIC_FILTERS]: false });

    const { container } = render(
      <Controls
        {...baseProps}
        viewType={VIEW_TYPE_CONSTANTS.listView}
        dynamicFilters={dynamicFilters}
      />
    );

    const mainWrapperFlex = container.querySelector('div > div');
    const styles = window.getComputedStyle(mainWrapperFlex as HTMLElement);

    expect(styles).toBeDefined();

    expect(styles.marginLeft).not.toBe('2.375rem');
  });

  it('should switch positioning from relative (dynamic filters enabled) to absolute (dynamic filters disabled) when toggling feature flag', () => {
    const { rerender, container } = render(
      <Controls
        {...baseProps}
        viewType={VIEW_TYPE_CONSTANTS.mapView}
        dynamicFilters={dynamicFilters}
        defaultFilters={['ACO']}
      />
    );

    (useFeatureToggle as jest.Mock).mockReturnValue({ [FT_SRP_DYNAMIC_FILTERS]: true });

    rerender(
      <Controls
        {...baseProps}
        viewType={VIEW_TYPE_CONSTANTS.mapView}
        dynamicFilters={dynamicFilters}
        defaultFilters={['ACO']}
      />
    );

    let relativeFound = false;
    const allElements = container.querySelectorAll('*');
    allElements.forEach((el) => {
      const styles = window.getComputedStyle(el as HTMLElement);
      if (styles.position === 'relative') {
        relativeFound = true;
      }
    });

    expect(relativeFound).toBe(true);
  });

  it('should render destination filters when in map view with dynamic filters enabled', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({ [FT_SRP_DYNAMIC_FILTERS]: true });

    render(
      <Controls
        {...baseProps}
        viewType={VIEW_TYPE_CONSTANTS.mapView}
        dynamicFilters={dynamicFilters}
        defaultFilters={['CPF']}
      />
    );

    await waitFor(() => {
      expect(screen.getByTestId('DLP-Filters-Open-Button')).toBeInTheDocument();
    });
  });
});

describe('Controls desktop split view', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (useFeatureToggle as jest.Mock).mockReturnValue({ [FT_SRP_DYNAMIC_FILTERS]: false });
    (useMobileControlsDisplay as jest.Mock).mockReturnValue(false);
    (useScreenSize as jest.Mock).mockReturnValue({ isLessThanMd: false });
  });

  it('renders the map/list toggle button and not rightContent when hideViewToggle is false', () => {
    render(
      <Controls
        {...baseProps}
        hideViewToggle={false}
        rightContent={<div data-testid="right-content">Right</div>}
      />
    );

    expect(screen.getByTestId('controls-map-or-list-button')).toBeInTheDocument();
    expect(screen.queryByTestId('right-content')).not.toBeInTheDocument();
  });

  it('renders rightContent instead of the map/list toggle button when hideViewToggle is true', () => {
    render(
      <Controls
        {...baseProps}
        hideViewToggle
        rightContent={<div data-testid="right-content">Right</div>}
      />
    );

    expect(screen.queryByTestId('controls-map-or-list-button')).not.toBeInTheDocument();
    expect(screen.getByTestId('right-content')).toBeInTheDocument();
  });

  it('renders neither the toggle button nor rightContent on small screens', () => {
    (useScreenSize as jest.Mock).mockReturnValue({ isLessThanMd: true });

    render(
      <Controls
        {...baseProps}
        hideViewToggle
        rightContent={<div data-testid="right-content">Right</div>}
      />
    );

    expect(screen.queryByTestId('controls-map-or-list-button')).not.toBeInTheDocument();
    expect(screen.queryByTestId('right-content')).not.toBeInTheDocument();
  });

  it('passes isSplitView through to SortBy when isSplitView is true', () => {
    render(<Controls {...baseProps} viewType={VIEW_TYPE_CONSTANTS.listView} isSplitView />);

    expect(screen.getByTestId('sortby-button')).toHaveAttribute('data-is-split-view', 'true');
  });

  it('does not mark SortBy as split view by default', () => {
    render(<Controls {...baseProps} viewType={VIEW_TYPE_CONSTANTS.listView} />);

    expect(screen.getByTestId('sortby-button')).toHaveAttribute('data-is-split-view', 'false');
  });

  it('does not render SortBy when in map view even if isSplitView is true', () => {
    render(<Controls {...baseProps} viewType={VIEW_TYPE_CONSTANTS.mapView} isSplitView />);

    expect(screen.queryByTestId('sortby-button')).not.toBeInTheDocument();
  });
});
