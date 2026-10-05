import { act, fireEvent, render, waitFor } from '@testing-library/react';
import { DynamicFilters } from '@whitbread-eos/api';
import React from 'react';

import { userEvent } from '../../utils/test-utils';
import DestinationFilters from './DestinationFilters.component';

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    query: 'facility=restaurant',
    asPath: '',
    push: jest.fn(),
  }),
}));

const mockLabels = {
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
          queryParam: '11',
        },
        {
          label: 'Chargeable on-site parking',
          info: '',
          codes: 'CPP',
          queryParam: '12',
        },
        {
          label: 'Chargeable off-site parking',
          info: '',
          codes: 'COP,COC',
          queryParam: '13',
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
          codes: 'ACO',
          queryParam: '1',
        },
        {
          label: 'Lift access',
          info: 'Some hotels are ground floor only. Please check directly with the hotel (local rate).',
          codes: 'LFT,HUL',
          queryParam: '2',
        },
        {
          label: 'Meeting rooms',
          info: '',
          codes: 'MEE',
          queryParam: '3',
        },
        {
          label: 'Restaurant',
          info: '',
          codes: 'RES,DIN,HRS',
          queryParam: '4',
        },
        {
          label: 'Premier Plus rooms',
          info: '',
          codes: 'PRR',
          queryParam: '5',
        },
        {
          label: 'EV Charging point',
          info: '',
          codes: 'EVC',
          queryParam: '6',
        },
        {
          label: 'Interconnecting rooms',
          info: '',
          codes: 'ICR',
          queryParam: '7',
        },
        {
          label: 'Luggage storage',
          info: '',
          codes: 'HLG,LUG',
          queryParam: '8',
        },
        {
          label: 'Accessible room with lowered bath',
          info: '',
          codes: 'LWB',
          queryParam: '9',
        },
        {
          label: 'Accessible room with wet room',
          info: '',
          codes: 'WET',
          queryParam: '10',
        },
      ],
    },
  ] as DynamicFilters[],
};

describe('DestinationFilters', () => {
  function Component(props) {
    const [selectedFilters, setSelectedFilters] = React.useState([]);
    return (
      <DestinationFilters
        {...props}
        labels={mockLabels}
        selectedFilters={selectedFilters}
        setSelectedFilters={setSelectedFilters}
      />
    );
  }

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
    jest.clearAllMocks();
  });

  it('should display the Filters panel after pressing the button', async () => {
    const { getByText, queryByText, getByRole } = render(<Component />);
    const filterBtn = getByRole('button');

    expect(queryByText('Filters applied when selected')).not.toBeInTheDocument();
    await act(async () => {
      fireEvent.click(filterBtn);
    });

    expect(document.body.style.overflow).toBe('hidden');
    expect(getByText('Filters applied when selected')).toBeVisible();
  });

  it('should hide the Filters panel after pressing the close button', async () => {
    const { getByRole, queryByText, getByTestId } = render(<Component />);
    const filterBtn = getByRole('button');
    await act(async () => {
      fireEvent.click(filterBtn);
    });

    const closeBtn = getByTestId('DLP-Filters-Close-Button');
    const filterPanel = getByTestId('DLP-Filters-Wrapper');

    await act(async () => {
      fireEvent.click(closeBtn);
    });

    await waitFor(() => {
      expect(filterPanel).not.toBeVisible();
      expect(queryByText('Parking')).toBeFalsy();
      expect(document.body.style.overflow).toBe('auto');
    });
  });

  it('should hide the Filters panel after clicking outside', async () => {
    const { getByRole, queryByText, getByTestId } = render(<Component />);
    const filterBtn = getByRole('button');
    await act(async () => {
      fireEvent.click(filterBtn);
    });

    const filterPanel = getByTestId('DLP-Filters-Wrapper');
    expect(filterPanel).toBeVisible();

    await act(async () => {
      fireEvent.click(document.body);
    });
    fireEvent.click(document.body);
    fireEvent.mouseDown(document.body);
    userEvent.click(document.body);

    await waitFor(() => {
      expect(filterPanel).not.toBeVisible();
      expect(queryByText('Parking')).toBeFalsy();
      expect(document.body.style.overflow).toBe('auto');
    });
  });

  it('should remove a selected filter', async () => {
    const { getByRole, getAllByRole } = render(<Component />);
    const filterBtn = getByRole('button');

    fireEvent.click(filterBtn);

    const checkboxes = getAllByRole('checkbox');

    await act(async () => {
      fireEvent.click(checkboxes[1]);
      fireEvent.click(checkboxes[5]);
      fireEvent.click(checkboxes[6]);
    });

    await waitFor(() => {
      expect(checkboxes[1]).toBeChecked();
      expect(checkboxes[5]).toBeChecked();
      expect(checkboxes[6]).toBeChecked();
    });

    let selectedFilters = getAllByRole('checkbox', { checked: true });

    expect(selectedFilters).toHaveLength(3);

    await act(async () => {
      fireEvent.click(checkboxes[1]);
    });

    await waitFor(() => {
      expect(checkboxes[1]).not.toBeChecked();
      expect(checkboxes[5]).toBeChecked();
      expect(checkboxes[6]).toBeChecked();
    });
    selectedFilters = getAllByRole('checkbox', { checked: true });

    expect(selectedFilters).toHaveLength(2);
  });

  it('should show/hide the Clear filters button', async () => {
    const { getByRole, getAllByRole, queryByText, getByText } = render(<Component />);
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

  describe('accessibility', () => {
    it('exposes dialog semantics on the open panel', async () => {
      const { getByTestId } = render(<Component />);
      await act(async () => {
        fireEvent.click(getByTestId('DLP-Filters-Open-Button'));
      });

      const panel = getByTestId('DLP-Filters-Wrapper');
      await waitFor(() => expect(panel).toHaveFocus());
      expect(panel).toHaveAttribute('role', 'dialog');
      expect(panel).toHaveAttribute('aria-modal', 'true');
      expect(panel).toHaveAttribute('aria-labelledby', 'DLP-Filters-heading');
      expect(document.getElementById('DLP-Filters-heading')).toHaveTextContent(
        'Filters applied when selected'
      );
    });

    it('sets aria-haspopup always and aria-controls only while open', async () => {
      const { getByTestId } = render(<Component />);
      const filterBtn = getByTestId('DLP-Filters-Open-Button');

      expect(filterBtn).toHaveAttribute('aria-haspopup', 'dialog');
      expect(filterBtn).not.toHaveAttribute('aria-controls');

      await act(async () => {
        fireEvent.click(filterBtn);
      });

      expect(filterBtn).toHaveAttribute('aria-controls', 'DLP-Filters-Wrapper');
      expect(filterBtn).toHaveAttribute('aria-expanded', 'true');
    });

    it('closes the panel when Escape is pressed inside it', async () => {
      const { getByTestId, queryByText } = render(<Component />);
      await act(async () => {
        fireEvent.click(getByTestId('DLP-Filters-Open-Button'));
      });

      const openButton = getByTestId('DLP-Filters-Open-Button');

      await act(async () => {
        fireEvent.keyDown(getByTestId('DLP-Filters-Wrapper'), { key: 'Escape', code: 'Escape' });
      });

      await waitFor(() => {
        expect(queryByText('Parking')).toBeFalsy();
      });
      await waitFor(() => expect(openButton).toHaveFocus());
    });

    it('exposes a focusable, labelled close control and activates it via keyboard', async () => {
      const { getByTestId, queryByText } = render(<Component />);
      await act(async () => {
        fireEvent.click(getByTestId('DLP-Filters-Open-Button'));
      });

      const closeControl = getByTestId('DLP-Filters-Close-Button').closest('[role="button"]');
      expect(closeControl).toHaveAttribute('tabindex', '0');
      expect(closeControl).toHaveAttribute('aria-label');

      await act(async () => {
        fireEvent.keyDown(closeControl as Element, { key: 'Enter', code: 'Enter' });
      });

      await waitFor(() => {
        expect(queryByText('Parking')).toBeFalsy();
      });
    });

    it('associates each filter group with its heading', async () => {
      const { getByTestId, getAllByRole } = render(<Component />);
      await act(async () => {
        fireEvent.click(getByTestId('DLP-Filters-Open-Button'));
      });

      const groups = getAllByRole('group');
      expect(groups).toHaveLength(2);
      expect(groups[0]).toHaveAttribute('aria-labelledby', 'DLP-Filters-group-0-heading');
      expect(groups[1]).toHaveAttribute('aria-labelledby', 'DLP-Filters-group-1-heading');
    });
  });
});
