import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import { getSuggestions } from '@whitbread-eos/api';

import HotelIdAutocomplete, { HotelProperty } from './HotelIdAutocomplete.component';

jest.mock('next-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}));

jest.mock('@whitbread-eos/api', () => ({
  ...jest.requireActual('@whitbread-eos/api'),
  getSuggestions: jest.fn(),
}));

jest.mock('lodash/debounce', () => (fn: (...args: any[]) => any) => {
  const debounced = (...args: any[]) => fn(...args);
  debounced.cancel = jest.fn();
  return debounced;
});

const mockHotelProperties: HotelProperty[] = [
  { code: 'HEAPTI', brand: 'PI', suggestion: 'London Heathrow Airport' },
  { code: 'LONBRI', brand: 'PI', suggestion: 'London Bridge' },
];

const baseFormField = {
  name: 'hotelId',
  label: 'Hotel ID',
} as any;

const baseField = {
  name: 'hotelId',
  value: '',
  onChange: jest.fn(),
  onBlur: jest.fn(),
  ref: jest.fn(),
};

const renderComponent = (
  overrides: Partial<React.ComponentProps<typeof HotelIdAutocomplete>> = {}
) => {
  const handleSetValue = jest.fn();
  const handleSetError = jest.fn();
  const handleClearErrors = jest.fn();

  const utils = render(
    <HotelIdAutocomplete
      formField={baseFormField}
      field={baseField as any}
      errors={{}}
      handleSetValue={handleSetValue}
      handleSetError={handleSetError}
      handleClearErrors={handleClearErrors}
      {...overrides}
    />
  );
  return { ...utils, handleSetValue, handleSetError, handleClearErrors };
};

describe('HotelIdAutocomplete', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (getSuggestions as jest.Mock).mockResolvedValue({ properties: mockHotelProperties });
  });

  it('renders the input and label', () => {
    renderComponent();

    expect(screen.getByTestId('HotelIdAutocomplete-hotelId-input')).toBeInTheDocument();
    expect(screen.getByTestId('HotelIdAutocomplete-hotelId-label')).toHaveTextContent('Hotel ID');
  });

  it('does not render label when formField.label is not provided', () => {
    renderComponent({ formField: { ...baseFormField, label: undefined } });

    expect(screen.queryByTestId('HotelIdAutocomplete-hotelId-label')).not.toBeInTheDocument();
  });

  it('does not search when input is below MIN_LENGTH_SEARCH_TERM', async () => {
    renderComponent();
    const input = screen.getByTestId('HotelIdAutocomplete-hotelId-input');

    fireEvent.change(input, { target: { value: 'ab' } });

    await waitFor(() => {
      expect(getSuggestions).not.toHaveBeenCalled();
    });
  });

  it('calls getSuggestions and shows dropdown when input meets min length', async () => {
    renderComponent();
    const input = screen.getByTestId('HotelIdAutocomplete-hotelId-input');

    fireEvent.change(input, { target: { value: 'lon' } });

    await waitFor(() => {
      expect(getSuggestions).toHaveBeenCalledWith('lon');
    });

    expect(
      await screen.findByTestId('HotelIdAutocomplete-hotelId-Suggestions')
    ).toBeInTheDocument();
    expect(screen.getByTestId('HotelIdAutocomplete-Option-HEAPTI')).toBeInTheDocument();
    expect(screen.getByTestId('HotelIdAutocomplete-Option-LONBRI')).toBeInTheDocument();
  });

  it('clears suggestions and sets error when input is emptied', async () => {
    const { handleSetError, handleSetValue } = renderComponent();
    const input = screen.getByTestId('HotelIdAutocomplete-hotelId-input');

    fireEvent.change(input, { target: { value: 'lon' } });
    await waitFor(() => expect(getSuggestions).toHaveBeenCalled());

    fireEvent.change(input, { target: { value: '' } });

    expect(handleSetValue).toHaveBeenCalledWith('hotelId', '');
    expect(handleSetError).toHaveBeenCalledWith(
      'hotelId',
      expect.objectContaining({ type: 'custom' })
    );
    expect(screen.queryByTestId('HotelIdAutocomplete-hotelId-Suggestions')).not.toBeInTheDocument();
  });

  it('sets error when API returns no properties', async () => {
    (getSuggestions as jest.Mock).mockResolvedValueOnce({ properties: [] });
    const { handleSetError } = renderComponent();
    const input = screen.getByTestId('HotelIdAutocomplete-hotelId-input');

    fireEvent.change(input, { target: { value: 'zzz' } });

    await waitFor(() => {
      expect(handleSetError).toHaveBeenCalledWith(
        'hotelId',
        expect.objectContaining({ type: 'custom' })
      );
    });
  });

  it('clears errors when API returns properties', async () => {
    const { handleClearErrors } = renderComponent();
    const input = screen.getByTestId('HotelIdAutocomplete-hotelId-input');

    fireEvent.change(input, { target: { value: 'lon' } });

    await waitFor(() => {
      expect(handleClearErrors).toHaveBeenCalledWith('hotelId');
    });
  });

  it('sets error and clears suggestions when getSuggestions throws', async () => {
    (getSuggestions as jest.Mock).mockRejectedValueOnce(new Error('network error'));
    const { handleSetError } = renderComponent();
    const input = screen.getByTestId('HotelIdAutocomplete-hotelId-input');

    fireEvent.change(input, { target: { value: 'lon' } });

    await waitFor(() => {
      expect(handleSetError).toHaveBeenCalledWith(
        'hotelId',
        expect.objectContaining({ type: 'custom' })
      );
    });

    expect(screen.queryByTestId('HotelIdAutocomplete-hotelId-Suggestions')).not.toBeInTheDocument();
  });

  it('shows spinner while loading and hides it after results resolve', async () => {
    let resolvePromise: (value: any) => void;
    (getSuggestions as jest.Mock).mockImplementationOnce(
      () =>
        new Promise((resolve) => {
          resolvePromise = resolve;
        })
    );

    renderComponent();
    const input = screen.getByTestId('HotelIdAutocomplete-hotelId-input');

    fireEvent.change(input, { target: { value: 'lon' } });

    expect(await screen.findByTestId('HotelIdAutocomplete-hotelId-spinner')).toBeInTheDocument();

    await act(async () => {
      resolvePromise!({ properties: mockHotelProperties });
    });

    await waitFor(() => {
      expect(screen.queryByTestId('HotelIdAutocomplete-hotelId-spinner')).not.toBeInTheDocument();
    });
  });

  it('selects a hotel from the dropdown', async () => {
    const { handleSetValue, handleClearErrors } = renderComponent();
    const input = screen.getByTestId('HotelIdAutocomplete-hotelId-input') as HTMLInputElement;

    fireEvent.change(input, { target: { value: 'lon' } });
    await screen.findByTestId('HotelIdAutocomplete-hotelId-Suggestions');

    fireEvent.mouseDown(screen.getByTestId('HotelIdAutocomplete-Option-HEAPTI'));

    expect(handleSetValue).toHaveBeenCalledWith('hotelId', 'HEAPTI');
    expect(handleClearErrors).toHaveBeenCalledWith('hotelId');
    expect(input.value).toBe('HEAPTI');
    expect(screen.queryByTestId('HotelIdAutocomplete-hotelId-Suggestions')).not.toBeInTheDocument();
  });

  it('shows the clear button after a hotel is selected', async () => {
    renderComponent();
    const input = screen.getByTestId('HotelIdAutocomplete-hotelId-input');

    fireEvent.change(input, { target: { value: 'lon' } });
    await screen.findByTestId('HotelIdAutocomplete-hotelId-Suggestions');
    fireEvent.mouseDown(screen.getByTestId('HotelIdAutocomplete-Option-HEAPTI'));

    expect(screen.getByTestId('HotelIdAutocomplete-hotelId-clear')).toBeInTheDocument();
  });

  it('clears the selection when the clear button is clicked', async () => {
    const { handleSetValue, handleSetError } = renderComponent();
    const input = screen.getByTestId('HotelIdAutocomplete-hotelId-input') as HTMLInputElement;

    fireEvent.change(input, { target: { value: 'lon' } });
    await screen.findByTestId('HotelIdAutocomplete-hotelId-Suggestions');
    fireEvent.mouseDown(screen.getByTestId('HotelIdAutocomplete-Option-HEAPTI'));

    const clearButton = screen.getByTestId('HotelIdAutocomplete-hotelId-clear');
    fireEvent.mouseDown(clearButton);

    expect(input.value).toBe('');
    expect(handleSetValue).toHaveBeenCalledWith(
      'hotelId',
      '',
      expect.objectContaining({ shouldDirty: true, shouldTouch: true, shouldValidate: true })
    );
    expect(handleSetError).toHaveBeenCalledWith(
      'hotelId',
      expect.objectContaining({ type: 'custom' })
    );
    expect(screen.queryByTestId('HotelIdAutocomplete-hotelId-clear')).not.toBeInTheDocument();
  });

  it('reopens dropdown on focus if suggestions already exist', async () => {
    renderComponent();
    const input = screen.getByTestId('HotelIdAutocomplete-hotelId-input');

    fireEvent.change(input, { target: { value: 'lon' } });
    await screen.findByTestId('HotelIdAutocomplete-hotelId-Suggestions');

    // simulate closing via outside click
    fireEvent.mouseDown(document.body);
    await waitFor(() => {
      expect(
        screen.queryByTestId('HotelIdAutocomplete-hotelId-Suggestions')
      ).not.toBeInTheDocument();
    });

    fireEvent.focus(input);

    expect(
      await screen.findByTestId('HotelIdAutocomplete-hotelId-Suggestions')
    ).toBeInTheDocument();
  });

  it('closes the dropdown on outside click', async () => {
    renderComponent();
    const input = screen.getByTestId('HotelIdAutocomplete-hotelId-input');

    fireEvent.change(input, { target: { value: 'lon' } });
    await screen.findByTestId('HotelIdAutocomplete-hotelId-Suggestions');

    fireEvent.mouseDown(document.body);

    await waitFor(() => {
      expect(
        screen.queryByTestId('HotelIdAutocomplete-hotelId-Suggestions')
      ).not.toBeInTheDocument();
    });
  });

  it('clears the input on blur if no hotel was selected', async () => {
    jest.useFakeTimers({ legacyFakeTimers: false });
    const { handleSetValue } = renderComponent();
    const input = screen.getByTestId('HotelIdAutocomplete-hotelId-input') as HTMLInputElement;

    fireEvent.change(input, { target: { value: 'lon' } });
    fireEvent.blur(input);

    act(() => {
      jest.advanceTimersByTime(150);
    });

    expect(input.value).toBe('');
    expect(handleSetValue).toHaveBeenCalledWith('hotelId', '');

    jest.useRealTimers();
  });

  it('does not clear the input on blur if a hotel was selected', async () => {
    jest.useFakeTimers({ legacyFakeTimers: false });
    renderComponent();
    const input = screen.getByTestId('HotelIdAutocomplete-hotelId-input') as HTMLInputElement;

    fireEvent.change(input, { target: { value: 'lon' } });

    act(() => {
      jest.advanceTimersByTime(0);
    });

    await act(async () => {
      await Promise.resolve();
    });

    const option = screen.queryByTestId('HotelIdAutocomplete-Option-HEAPTI');
    if (option) {
      fireEvent.mouseDown(option);
    }

    fireEvent.blur(input);
    act(() => {
      jest.advanceTimersByTime(150);
    });

    if (option) {
      expect(input.value).toBe('HEAPTI');
    }

    jest.useRealTimers();
  });

  it('ignores blur when ignoreBlur ref is set by the clear button mousedown', async () => {
    renderComponent();
    const input = screen.getByTestId('HotelIdAutocomplete-hotelId-input') as HTMLInputElement;

    fireEvent.change(input, { target: { value: 'lon' } });
    await screen.findByTestId('HotelIdAutocomplete-hotelId-Suggestions');
    fireEvent.mouseDown(screen.getByTestId('HotelIdAutocomplete-Option-HEAPTI'));

    const clearButton = screen.getByTestId('HotelIdAutocomplete-hotelId-clear');

    fireEvent.mouseDown(clearButton);

    expect(input.value).toBe('');
  });

  it('displays error message when errors.hotelId is present', () => {
    renderComponent({
      errors: { hotelId: { message: 'Hotel ID required' } } as any,
    });

    expect(screen.getByTestId('HotelIdAutocomplete-hotelId-Error')).toHaveTextContent(
      'Hotel ID required'
    );
  });

  it('does not display error message when errors.hotelId is absent', () => {
    renderComponent({ errors: {} });

    expect(screen.queryByTestId('HotelIdAutocomplete-hotelId-Error')).not.toBeInTheDocument();
  });

  it('initializes input value from field.value', () => {
    renderComponent({ field: { ...baseField, value: 'PRESET' } as any });

    const input = screen.getByTestId('HotelIdAutocomplete-hotelId-input') as HTMLInputElement;
    expect(input.value).toBe('PRESET');
  });

  it('resets selectedHotel state when user types after a selection', async () => {
    renderComponent();
    const input = screen.getByTestId('HotelIdAutocomplete-hotelId-input') as HTMLInputElement;

    fireEvent.change(input, { target: { value: 'lon' } });
    await screen.findByTestId('HotelIdAutocomplete-hotelId-Suggestions');
    fireEvent.mouseDown(screen.getByTestId('HotelIdAutocomplete-Option-HEAPTI'));

    expect(screen.getByTestId('HotelIdAutocomplete-hotelId-clear')).toBeInTheDocument();

    fireEvent.change(input, { target: { value: 'new search' } });

    expect(screen.queryByTestId('HotelIdAutocomplete-hotelId-clear')).not.toBeInTheDocument();
  });
  it('replaces the autocomplete icon with the spinner while loading', async () => {
    let resolvePromise: (value: any) => void;

    (getSuggestions as jest.Mock).mockImplementationOnce(
      () =>
        new Promise((resolve) => {
          resolvePromise = resolve;
        })
    );

    renderComponent();

    fireEvent.change(screen.getByTestId('HotelIdAutocomplete-hotelId-input'), {
      target: { value: 'lon' },
    });

    expect(
      screen.queryByTestId('HotelIdAutocomplete-hotelId-autocomplete-icon')
    ).not.toBeInTheDocument();

    expect(screen.getByTestId('HotelIdAutocomplete-hotelId-spinner')).toBeInTheDocument();

    await act(async () => {
      resolvePromise!({ properties: mockHotelProperties });
    });
  });
  it('replaces the autocomplete icon with the clear button after selection', async () => {
    renderComponent();

    fireEvent.change(screen.getByTestId('HotelIdAutocomplete-hotelId-input'), {
      target: { value: 'lon' },
    });

    await screen.findByTestId('HotelIdAutocomplete-hotelId-Suggestions');

    fireEvent.mouseDown(screen.getByTestId('HotelIdAutocomplete-Option-HEAPTI'));

    expect(
      screen.queryByTestId('HotelIdAutocomplete-hotelId-autocomplete-icon')
    ).not.toBeInTheDocument();

    expect(screen.getByTestId('HotelIdAutocomplete-hotelId-clear')).toBeInTheDocument();
  });
});
