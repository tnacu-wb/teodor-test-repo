import { fireEvent, render } from '@testing-library/react';
import { SelectedFilter } from '@whitbread-eos/api';
import { GroupItems } from '@whitbread-eos/api/src/types/graphql';

import { createCheckboxList } from './createCheckboxList';

const srHotelFilters = [
  { code: 'LFT', name: 'Lift' },
  { code: 'WIFI', name: 'Wi-Fi' },
];

const groupItems = [
  { codes: ['LFT', 'HUL'], label: 'Lift' },
  { codes: 'WIFI', label: 'Wi-Fi' },
];

const selectedFiltersString = ['LFT', 'WIFI'];
const selectedFiltersDlp = [{ codes: ['LFT', 'HUL'] }, { codes: ['WIFI'] }];

describe('createCheckboxList', () => {
  const baseDataTestId = 'DLP';
  const liftInfoMessage = 'Lift info';
  const handleFilterChange = jest.fn();

  it('renders checkboxes for SRHotelFilters with string[]', () => {
    const { getByTestId } = render(
      <>
        {createCheckboxList(
          baseDataTestId,
          selectedFiltersString,
          handleFilterChange,
          srHotelFilters,
          liftInfoMessage
        )}
      </>
    );
    expect(getByTestId('DLP-Filters-checkbox-LFT')).toBeInTheDocument();
    expect(getByTestId('DLP-Filters-checkbox-WIFI')).toBeInTheDocument();
  });

  it('renders checkboxes for GroupItems with string[]', () => {
    const { getByTestId } = render(
      <>
        {createCheckboxList(
          baseDataTestId,
          selectedFiltersString,
          handleFilterChange,
          groupItems as GroupItems[],
          liftInfoMessage
        )}
      </>
    );
    expect(getByTestId('DLP-Filters-checkbox-LFT,HUL')).toBeInTheDocument();
    expect(getByTestId('DLP-Filters-checkbox-WIFI')).toBeInTheDocument();
  });

  it('checks the correct checkboxes for string[]', () => {
    const { getByTestId } = render(
      <>
        {createCheckboxList(
          baseDataTestId,
          ['WIFI'],
          handleFilterChange,
          srHotelFilters,
          liftInfoMessage
        )}
      </>
    );
    expect(getByTestId('DLP-Filters-checkbox-LFT').querySelector('input')).not.toBeChecked();
    expect(getByTestId('DLP-Filters-checkbox-WIFI').querySelector('input')).toBeChecked();
  });

  it('calls handleFilterChange on change for string[]', () => {
    const { getByTestId } = render(
      <>
        {createCheckboxList(
          baseDataTestId,
          [],
          handleFilterChange,
          srHotelFilters,
          liftInfoMessage
        )}
      </>
    );
    fireEvent.click(getByTestId('DLP-Filters-checkbox-LFT').querySelector('input')!);
    expect(handleFilterChange).toHaveBeenCalled();
  });

  it('renders tooltip for lift access', () => {
    const { getByText, getByTestId } = render(
      <>{createCheckboxList('SRP', [], handleFilterChange, srHotelFilters, liftInfoMessage)}</>
    );
    expect(getByText('Lift')).toBeInTheDocument();
    expect(getByTestId('SRP-Filters-Lift-access-info-icon')).toBeInTheDocument();
  });

  it('checks the correct checkboxes for SelectedFilter[] and isDlp', () => {
    const { getByTestId } = render(
      <>
        {createCheckboxList(
          baseDataTestId,
          selectedFiltersDlp as SelectedFilter[],
          handleFilterChange,
          srHotelFilters,
          liftInfoMessage,
          true
        )}
      </>
    );
    expect(getByTestId('DLP-Filters-checkbox-LFT').querySelector('input')).toBeChecked();
    expect(getByTestId('DLP-Filters-checkbox-WIFI').querySelector('input')).toBeChecked();
  });

  it('checks the correct checkboxes for SelectedFilter[] with multiple codes and isDlp', () => {
    const { getByTestId } = render(
      <>
        {createCheckboxList(
          baseDataTestId,
          selectedFiltersDlp as SelectedFilter[],
          handleFilterChange,
          groupItems as GroupItems[],
          liftInfoMessage,
          true
        )}
      </>
    );
    expect(getByTestId('DLP-Filters-checkbox-LFT,HUL').querySelector('input')).toBeChecked();
    expect(getByTestId('DLP-Filters-checkbox-WIFI').querySelector('input')).toBeChecked();
  });

  it('calls handleFilterChange on change for SelectedFilter[] and isDlp', () => {
    const { getByTestId } = render(
      <>
        {createCheckboxList(
          baseDataTestId,
          selectedFiltersDlp as SelectedFilter[],
          handleFilterChange,
          srHotelFilters,
          liftInfoMessage,
          true
        )}
      </>
    );
    fireEvent.click(getByTestId('DLP-Filters-checkbox-LFT').querySelector('input')!);
    expect(handleFilterChange).toHaveBeenCalled();
  });
});
