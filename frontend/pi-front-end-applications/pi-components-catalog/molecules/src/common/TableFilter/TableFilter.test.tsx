import '@testing-library/jest-dom';
import preloadAll from 'jest-next-dynamic';
import React from 'react';

import { render, userEvent, waitFor, act } from '../../utils/test-utils';
import TableFilter from './TableFilter.component';

const mockPopoverButtonLabel = 'Trigger';
const mockFilterKey = 'FilterKey';
const mockSelectionCountLabel = 'tableFilter.selectionCount';
const mockSelectAll = 'tableFilter.selectAll';
const mockClearAll = 'tableFilter.clearAll';
const mockSearchPlaceholder = 'tableFilter.search.placeholder';
const mockSearchNoResults = 'tableFilter.search.noResults';
const mockApplyButtonLabel = 'tableFilter.apply';
const mockFilerOption1 = 'FilterOption1';
const mockFilerOption2 = 'FilterOption2';
const mockFilerOption3 = 'FilterOption3';
const mockFilerOption4 = 'FilterOption4';
const mockFilerOption5 = 'FilterOption5';
const shortSearchTerm = 'Fi';
const validSearchTerm = 'Option4';
const invalidSearchTerm = 'abcgefg';
const mockFilerOptions = [
  mockFilerOption1,
  mockFilerOption2,
  mockFilerOption3,
  mockFilerOption4,
  mockFilerOption5,
];
const emptySlectedFilters = [];
const validSelectedFilters = [mockFilerOption1, mockFilerOption4];
const mockPopoverTrigger = () => <button>{mockPopoverButtonLabel}</button>;
const mockApplyFiltersHandler = jest.fn();

const mockProps = {
  popoverTrigger: mockPopoverTrigger,
  filterKey: mockFilterKey,
  filterOptions: mockFilerOptions,
  selectedFilters: emptySlectedFilters,
  onApplyFilters: mockApplyFiltersHandler,
};

describe('TableFilter', () => {
  beforeAll(async () => {
    await preloadAll();
  });
  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should not display the filter popover without clicking on the trigger element', async () => {
    const { queryByRole, getByRole } = render(<TableFilter {...mockProps} />);

    await waitFor(() => {
      expect(getByRole('button', { name: mockPopoverButtonLabel })).toBeInTheDocument();
      expect(queryByRole('dialog')).not.toBeInTheDocument();
    });
  });

  it('should display the filter popover when clicking on the trigger element', async () => {
    const { getByRole } = render(<TableFilter {...mockProps} />);

    const trigger = getByRole('button', { name: mockPopoverButtonLabel });
    await act(async () => {
      userEvent.click(trigger);
    });

    await waitFor(() => expect(getByRole('dialog')).toBeInTheDocument());
  });

  it('should close the filter popover when clicking on the trigger element when the popover is already shown', async () => {
    const { getByRole, queryByRole } = render(<TableFilter {...mockProps} />);

    const trigger = getByRole('button', { name: mockPopoverButtonLabel });

    await act(async () => {
      userEvent.click(trigger);
    });

    await waitFor(() => expect(getByRole('dialog')).toBeInTheDocument());

    await act(async () => {
      userEvent.click(trigger);
    });

    await waitFor(() => expect(queryByRole('dialog')).not.toBeInTheDocument());
  });

  it('should close the filter popover when clicking on the close icon', async () => {
    const { getByRole, getByTestId, queryByRole } = render(<TableFilter {...mockProps} />);

    const trigger = getByRole('button', { name: mockPopoverButtonLabel });

    await act(async () => {
      userEvent.click(trigger);
    });

    await waitFor(() => expect(getByRole('dialog')).toBeInTheDocument());

    const closeButton = getByTestId('TableFilter-PopoverCloseIcon');

    await act(async () => {
      userEvent.click(closeButton);
    });

    await waitFor(() => expect(queryByRole('dialog')).not.toBeInTheDocument());
  });

  it('should display a checkbox for each of the filter options', async () => {
    const { getByRole, getAllByRole } = render(<TableFilter {...mockProps} />);

    const trigger = getByRole('button', { name: mockPopoverButtonLabel });

    await act(async () => {
      userEvent.click(trigger);
    });

    await waitFor(() => {
      expect(getByRole('dialog')).toBeInTheDocument();
      expect(getAllByRole('checkbox')).toHaveLength(5);
    });
  });

  it('should display checkboxes with the correct checked state based on the selected filters prop', async () => {
    const { getByRole, getAllByRole } = render(
      <TableFilter {...{ ...mockProps, selectedFilters: validSelectedFilters }} />
    );

    const trigger = getByRole('button', { name: mockPopoverButtonLabel });

    await act(async () => {
      userEvent.click(trigger);
    });

    await waitFor(() => {
      expect(getByRole('dialog')).toBeInTheDocument();
      expect(getAllByRole('checkbox')).toHaveLength(5);
      expect(getByRole('checkbox', { name: mockFilerOption1 })).toBeChecked();
      expect(getByRole('checkbox', { name: mockFilerOption2 })).not.toBeChecked();
      expect(getByRole('checkbox', { name: mockFilerOption3 })).not.toBeChecked();
      expect(getByRole('checkbox', { name: mockFilerOption4 })).toBeChecked();
      expect(getByRole('checkbox', { name: mockFilerOption5 })).not.toBeChecked();
    });
  });

  it('should indicate the correct number of filters selected', async () => {
    const { getByRole, getByText } = render(<TableFilter {...mockProps} />);

    const trigger = getByRole('button', { name: mockPopoverButtonLabel });

    await act(async () => {
      userEvent.click(trigger);
    });

    await waitFor(() => expect(getByRole('dialog')).toBeInTheDocument());
    await waitFor(() => expect(getByText(`0 ${mockSelectionCountLabel}`)).toBeInTheDocument());

    const checkbox1 = getByRole('checkbox', { name: mockFilerOption1 });
    const checkbox4 = getByRole('checkbox', { name: mockFilerOption4 });

    await act(async () => {
      userEvent.click(checkbox1);
    });

    await act(async () => {
      userEvent.click(checkbox4);
    });

    await waitFor(() => expect(getByText(`2 ${mockSelectionCountLabel}`)).toBeInTheDocument());
  });

  it('should indicate the correct multi selection label based on the selection of filters', async () => {
    const { getByRole, getByText } = render(<TableFilter {...mockProps} />);

    const trigger = getByRole('button', { name: mockPopoverButtonLabel });

    await act(async () => {
      userEvent.click(trigger);
    });

    await waitFor(() => expect(getByRole('dialog')).toBeInTheDocument());
    await waitFor(() => expect(getByText(mockSelectAll)).toBeInTheDocument());

    const checkbox1 = getByRole('checkbox', { name: mockFilerOption1 });
    const checkbox4 = getByRole('checkbox', { name: mockFilerOption4 });

    await act(async () => {
      userEvent.click(checkbox1);
    });

    await act(async () => {
      userEvent.click(checkbox4);
    });

    await waitFor(() => expect(getByText(mockClearAll)).toBeInTheDocument());
  });

  it('should select/unselect all filters when clicking on the multiselection label', async () => {
    const { getByRole, getByText } = render(<TableFilter {...mockProps} />);

    const trigger = getByRole('button', { name: mockPopoverButtonLabel });

    await act(async () => {
      userEvent.click(trigger);
    });

    await waitFor(() => expect(getByRole('dialog')).toBeInTheDocument());

    const selectAll = getByText(mockSelectAll);

    await act(async () => {
      userEvent.click(selectAll);
    });

    await waitFor(() => {
      expect(getByRole('checkbox', { name: mockFilerOption1 })).toBeChecked();
      expect(getByRole('checkbox', { name: mockFilerOption2 })).toBeChecked();
      expect(getByRole('checkbox', { name: mockFilerOption3 })).toBeChecked();
      expect(getByRole('checkbox', { name: mockFilerOption4 })).toBeChecked();
      expect(getByRole('checkbox', { name: mockFilerOption5 })).toBeChecked();
    });

    await waitFor(() => expect(getByText(mockClearAll)).toBeInTheDocument());

    const clearAll = getByText(mockClearAll);

    await act(async () => {
      userEvent.click(clearAll);
    });

    await waitFor(() => {
      expect(getByRole('checkbox', { name: mockFilerOption1 })).not.toBeChecked();
      expect(getByRole('checkbox', { name: mockFilerOption2 })).not.toBeChecked();
      expect(getByRole('checkbox', { name: mockFilerOption3 })).not.toBeChecked();
      expect(getByRole('checkbox', { name: mockFilerOption4 })).not.toBeChecked();
      expect(getByRole('checkbox', { name: mockFilerOption5 })).not.toBeChecked();
    });
  });

  it('should invoke the handler passed as prop when applying filters after selection', async () => {
    const { getByRole, getByText } = render(<TableFilter {...mockProps} />);

    const trigger = getByRole('button', { name: mockPopoverButtonLabel });

    await act(async () => {
      userEvent.click(trigger);
    });

    await waitFor(() => expect(getByRole('dialog')).toBeInTheDocument());
    await waitFor(() => expect(getByText(mockSelectAll)).toBeInTheDocument());

    const checkbox1 = getByRole('checkbox', { name: mockFilerOption1 });
    const checkbox4 = getByRole('checkbox', { name: mockFilerOption4 });

    await act(async () => {
      userEvent.click(checkbox1);
    });

    await act(async () => {
      userEvent.click(checkbox4);
    });

    const apply = getByRole('button', { name: mockApplyButtonLabel });

    await act(async () => {
      userEvent.click(apply);
    });

    await waitFor(() => expect(mockApplyFiltersHandler).toHaveBeenCalledTimes(1));
    await waitFor(() =>
      expect(mockApplyFiltersHandler).toHaveBeenCalledWith(
        [mockFilerOption1, mockFilerOption4],
        mockFilterKey
      )
    );
  });

  it('should not filter the list of filter options based on search term when it is less than 3 characters', async () => {
    const { getByRole, getByPlaceholderText, getAllByRole } = render(
      <TableFilter {...mockProps} />
    );

    const trigger = getByRole('button', { name: mockPopoverButtonLabel });

    await act(async () => {
      userEvent.click(trigger);
    });

    await waitFor(() => expect(getByRole('dialog')).toBeInTheDocument());

    const searchInput = getByPlaceholderText(mockSearchPlaceholder);

    await act(async () => {
      userEvent.type(searchInput, shortSearchTerm);
    });

    await waitFor(() => expect(getAllByRole('checkbox')).toHaveLength(5));
  });

  it('should filter the list of filter options based on search term when it is more than 3 characters', async () => {
    const { getByRole, getByPlaceholderText, getAllByRole, queryByRole, queryByText } = render(
      <TableFilter {...mockProps} />
    );

    const trigger = getByRole('button', { name: mockPopoverButtonLabel });

    await act(async () => {
      userEvent.click(trigger);
    });

    await waitFor(() => expect(getByRole('dialog')).toBeInTheDocument());

    const searchInput = getByPlaceholderText(mockSearchPlaceholder);

    await act(async () => {
      userEvent.type(searchInput, validSearchTerm);
    });

    await waitFor(() => {
      expect(getAllByRole('checkbox')).toHaveLength(1);
      expect(queryByRole('checkbox', { name: mockFilerOption1 })).not.toBeInTheDocument();
      expect(queryByRole('checkbox', { name: mockFilerOption2 })).not.toBeInTheDocument();
      expect(queryByRole('checkbox', { name: mockFilerOption3 })).not.toBeInTheDocument();
      expect(queryByRole('checkbox', { name: mockFilerOption4 })).toBeInTheDocument();
      expect(queryByRole('checkbox', { name: mockFilerOption5 })).not.toBeInTheDocument();
      expect(queryByRole('alert')).not.toBeInTheDocument();
      expect(queryByText(mockSearchNoResults)).not.toBeInTheDocument();
    });
  });

  it('should show a notification when there are no filters matching the input search term', async () => {
    const { getByRole, getByPlaceholderText, queryAllByRole, getByText } = render(
      <TableFilter {...mockProps} />
    );

    const trigger = getByRole('button', { name: mockPopoverButtonLabel });

    await act(async () => {
      userEvent.click(trigger);
    });

    await waitFor(() => expect(getByRole('dialog')).toBeInTheDocument());

    const searchInput = getByPlaceholderText(mockSearchPlaceholder);

    await act(async () => {
      userEvent.type(searchInput, invalidSearchTerm);
    });

    await waitFor(() => {
      expect(queryAllByRole('checkbox')).toHaveLength(0);
      expect(getByRole('status')).toBeInTheDocument();
      expect(getByText(mockSearchNoResults)).toBeInTheDocument();
    });
  });
});
