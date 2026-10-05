import '@testing-library/jest-dom';
import { act, fireEvent, render, waitFor } from '@testing-library/react';

import Filters from './Filters.component';

const mockLabels = {
  filterByButton: 'Filter by',
  filters: {
    label: {
      restaurant: 'Restaurant',
      airCon: 'Air conditioning',
      chargeableOffsiteParking: 'Chargeable off-site parking',
      freeParking: 'Free parking',
      parking: 'Parking',
      chargeableOnsiteParking: 'Chargeable on-site parking',
      header: 'Filters applied when selected',
      lift: 'Lift access',
      meet: 'Meeting rooms',
      apply: 'Apply filters',
      reset: 'Clear all filters',
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
};

const baseProps = {
  defaultFilters: ['EAT', 'ACO', 'COP'],
  onChangeFilters: jest.fn(),
  labels: mockLabels,
};

describe('Filters', () => {
  beforeEach(() => {
    jest.mock('react', () => ({
      ...jest.requireActual('react'),
      useRef: jest.fn(() => ({ current: '<div>' })),
    }));
    jest.mock('@chakra-ui/react', () => ({
      ...jest.requireActual('@chakra-ui/react'),
      useOutsideClick: jest.fn(() => ({
        ref: '<div>',
        handler: jest.fn(() => false),
      })),
    }));
    jest.resetAllMocks();
  });

  it('should display the Filters panel after pressing the button', () => {
    const { getByRole, getByText } = render(<Filters {...baseProps} />);
    const filterBtn = getByRole('button');

    expect(filterBtn).toHaveAttribute('aria-expanded', 'false');

    fireEvent.click(filterBtn);

    expect(filterBtn).toHaveAttribute('aria-expanded', 'true');
    expect(getByText('Facilities')).toBeVisible();
  });

  it('should hide the Filters panel after pressing the close button', async () => {
    const { getByRole, queryByText, getByTestId } = render(<Filters {...baseProps} />);
    const filterBtn = getByRole('button');
    await act(async () => {
      fireEvent.click(filterBtn);
    });

    const closeBtn = getByTestId('SRP-Filters-Close-button');

    await act(async () => {
      fireEvent.click(closeBtn);
    });

    await waitFor(() => {
      expect(queryByText('Facilities')).toBeFalsy();
    });
  });

  it('should select a filter', async () => {
    const { getByRole, getAllByRole } = render(<Filters {...baseProps} />);
    const filterBtn = getByRole('button');

    await act(async () => {
      fireEvent.click(filterBtn);
    });

    const checkboxes = getAllByRole('checkbox');

    await act(async () => {
      fireEvent.click(checkboxes[0]);
    });

    await waitFor(() => {
      expect(checkboxes[0]).toBeChecked();
    });
  });
  it('should remove a selected filter', async () => {
    const { getByRole, getAllByRole } = render(
      <Filters {...baseProps} defaultFilters={['EAT', 'ACO', 'COP']} />
    );
    const filterBtn = getByRole('button');

    await act(async () => {
      fireEvent.click(filterBtn);
    });

    const selectedFilters = getAllByRole('checkbox', { checked: true });

    expect(selectedFilters).toHaveLength(3);

    await act(async () => {
      fireEvent.click(selectedFilters[0]);
    });

    const selectedFilters2 = getAllByRole('checkbox', { checked: true });
    expect(selectedFilters2).toHaveLength(2);
  });
  it('should render the component without selected filters', async () => {
    const { getByRole, queryAllByRole } = render(<Filters {...baseProps} />);
    const filterBtn = getByRole('button');

    await act(async () => {
      fireEvent.click(filterBtn);
    });

    await waitFor(() => {
      const selectedFilters = queryAllByRole('checkbox', { checked: true });
      expect(selectedFilters).toHaveLength(3);
    });
  });
  it('should show/hide the Clear filters button', async () => {
    const { getByRole, getAllByRole, queryByText, getByText } = render(<Filters {...baseProps} />);
    const filterBtn = getByRole('button');

    await act(async () => {
      fireEvent.click(filterBtn);
    });

    const checkboxes = getAllByRole('checkbox');

    await act(async () => {
      fireEvent.click(checkboxes[0]);
      fireEvent.click(checkboxes[1]);
    });

    await waitFor(() => {
      const resetFilters = getByText('Clear all filters');
      expect(resetFilters).toBeInTheDocument();
      fireEvent.click(resetFilters);
    });

    await waitFor(() => {
      expect(queryByText('Clear all filters')).not.toBeInTheDocument();
    });
  });

  it('should render the leftIcon on the filter button', () => {
    const LeftIconMock = <span data-testid="mock-left-icon">Icon</span>;
    const { getByTestId } = render(<Filters {...baseProps} leftIcon={LeftIconMock} />);

    const iconElement = getByTestId('mock-left-icon');
    expect(iconElement).toBeInTheDocument();
    expect(iconElement).toHaveTextContent('Icon');
  });

  describe('accessibility', () => {
    it('exposes dialog semantics on the open panel', async () => {
      const { getByRole, getByTestId } = render(<Filters {...baseProps} />);
      await act(async () => {
        fireEvent.click(getByRole('button'));
      });

      const panel = getByTestId('SRP-Filters-content');
      expect(panel).toHaveAttribute('role', 'dialog');
      expect(panel).toHaveAttribute('aria-modal', 'true');
      expect(panel).toHaveAttribute('aria-labelledby', 'SRP-Filters-heading');
      expect(document.getElementById('SRP-Filters-heading')).toHaveTextContent(
        'Filters applied when selected'
      );
    });

    it('sets aria-haspopup always and aria-controls only while open', async () => {
      const { getByRole } = render(<Filters {...baseProps} />);
      const filterBtn = getByRole('button');

      expect(filterBtn).toHaveAttribute('aria-haspopup', 'dialog');
      expect(filterBtn).not.toHaveAttribute('aria-controls');

      await act(async () => {
        fireEvent.click(filterBtn);
      });

      expect(filterBtn).toHaveAttribute('aria-controls', 'SRP-Filters-content');
    });

    it('closes the panel when Escape is pressed inside it', async () => {
      const { getByRole, getByTestId, queryByText } = render(<Filters {...baseProps} />);
      await act(async () => {
        fireEvent.click(getByRole('button'));
      });

      await act(async () => {
        fireEvent.keyDown(getByTestId('SRP-Filters-content'), { key: 'Escape', code: 'Escape' });
      });

      await waitFor(() => {
        expect(queryByText('Facilities')).toBeFalsy();
      });
    });

    it('exposes a focusable, labelled close control and activates it via keyboard', async () => {
      const { getByRole, getByTestId, queryByText } = render(<Filters {...baseProps} />);
      await act(async () => {
        fireEvent.click(getByRole('button'));
      });

      const closeControl = getByTestId('SRP-Filters-Close-button').closest('[role="button"]');
      expect(closeControl).toHaveAttribute('tabindex', '0');
      // labelled by something other than the panel heading text
      expect(closeControl).toHaveAttribute('aria-label');
      expect(closeControl?.getAttribute('aria-label')).not.toBe('Filters applied when selected');

      await act(async () => {
        fireEvent.keyDown(closeControl as Element, { key: 'Enter', code: 'Enter' });
      });

      await waitFor(() => {
        expect(queryByText('Facilities')).toBeFalsy();
      });
    });

    it('exposes a focusable clear-filters control and activates it via keyboard', async () => {
      const { getByRole, getByTestId, queryByText } = render(<Filters {...baseProps} />);
      await act(async () => {
        fireEvent.click(getByRole('button'));
      });

      const clear = getByTestId('SRP-Clear-filters');
      expect(clear).toHaveAttribute('role', 'button');
      expect(clear).toHaveAttribute('tabindex', '0');

      await act(async () => {
        fireEvent.keyDown(clear, { key: ' ', code: 'Space' });
      });

      await waitFor(() => {
        expect(queryByText('Clear all filters')).toBeFalsy();
      });
    });

    it('associates each filter group with its heading', async () => {
      const { getByRole, getAllByRole } = render(<Filters {...baseProps} />);
      await act(async () => {
        fireEvent.click(getByRole('button'));
      });

      const groups = getAllByRole('group');
      expect(groups).toHaveLength(2);
      expect(groups[0]).toHaveAttribute('aria-labelledby', 'SRP-Filters-parking-heading');
      expect(groups[1]).toHaveAttribute('aria-labelledby', 'SRP-Filters-facilities-heading');
    });
  });
});
