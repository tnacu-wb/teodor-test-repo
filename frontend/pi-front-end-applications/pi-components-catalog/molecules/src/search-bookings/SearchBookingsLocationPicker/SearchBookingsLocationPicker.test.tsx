import '@testing-library/jest-dom';

import { act, render, userEvent, waitFor } from '../../utils/test-utils';
import SearchBookingsLocationPicker from './SearchBookingsLocationPicker.component';

const initialData = {
  managedPlaces: [],
  places: [],
  properties: [],
};

const mockSuggestions = {
  properties: [
    {
      code: 'LONSOH',
      brand: 'HUB',
      suggestion: 'hub London Soho',
      geometry: {
        type: 'Point',
        coordinates: [-0.136549, 51.513614],
      },
    },
    {
      code: 'LONRIC',
      brand: 'ZIP',
      suggestion: 'London Richmond',
      geometry: {
        type: 'Point',
        coordinates: [-0.291975, 51.466948],
      },
    },
    {
      code: 'LONARC',
      brand: 'PI',
      suggestion: 'London Archway',
      geometry: {
        type: 'Point',
        coordinates: [-0.135969, 51.565884],
      },
    },
  ],
  managedPlaces: [
    {
      suggestion: 'Isle of Man',
      managedPlaceId: '5',
      placeId: 'ChIJ1YEuRDCFY0gRDeDw8bxbAuo',
      geometry: {
        type: 'Point',
        coordinates: [-4.463196, 54.251186],
      },
    },
  ],
  places: [
    {
      suggestion: 'London, UK',
      placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
    },
  ],
};

const mockErrorLabel = 'Please enter a location or a hotel';
const mockInputChangeHandler = jest.fn();
const mockLocationSelectedHandler = jest.fn();
const mockClearInputHandler = jest.fn();
const mockInputFocusHandler = jest.fn();
const mockInputBlurHandler = jest.fn();

const defaultProps = {
  inputPlaceholder: 'Location',
  inputvalue: '',
  styles: {},
  suggestions: initialData,
  showError: false,
  errorLabel: mockErrorLabel,
  onInputChange: mockInputChangeHandler,
  onSelectLocation: mockLocationSelectedHandler,
  onClearInput: mockClearInputHandler,
  onInputFocus: mockInputFocusHandler,
  onInputBlur: mockInputBlurHandler,
};

describe('SearchBookingsLocationPicker', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render the component', () => {
    const { getByPlaceholderText } = render(<SearchBookingsLocationPicker {...defaultProps} />);
    expect(getByPlaceholderText('Location')).toBeInTheDocument();
  });

  it('should display clear icon when user types a character', async () => {
    const { getByTestId, getByPlaceholderText } = render(
      <SearchBookingsLocationPicker {...defaultProps} inputvalue="m" />
    );
    act(() => {
      const input = getByPlaceholderText('Location');
      userEvent.click(input);
    });

    await waitFor(() => {
      expect(getByTestId('SearchBookingsLocationPicker-clearLocationButton')).toBeInTheDocument();
    });
  });

  it('should call onInputChange when user types in the input', async () => {
    const { getByPlaceholderText } = render(<SearchBookingsLocationPicker {...defaultProps} />);

    act(() => {
      const input = getByPlaceholderText('Location');
      userEvent.type(input, 'm');
    });

    await waitFor(async () => {
      expect(mockInputChangeHandler).toBeCalledWith('m');
    });
  });

  it('should call onInputFocus when user clicks in the input', async () => {
    const { getByPlaceholderText } = render(<SearchBookingsLocationPicker {...defaultProps} />);

    act(() => {
      const input = getByPlaceholderText('Location');
      userEvent.click(input);
    });

    await waitFor(async () => {
      expect(mockInputFocusHandler).toHaveBeenCalledTimes(1);
    });
  });

  it('should call OnClearInput when user clicks on the clear icon', async () => {
    const { findByLabelText, getByPlaceholderText } = render(
      <SearchBookingsLocationPicker {...defaultProps} inputvalue="m" />
    );

    const input = getByPlaceholderText('Location');
    userEvent.click(input);
    const clearIcon = await findByLabelText('clear-icon');
    userEvent.click(clearIcon);

    await waitFor(() => {
      expect(mockClearInputHandler).toHaveBeenCalledTimes(1);
    });
  });

  it('should call onSelectLocation when a location is selected', async () => {
    const { getByPlaceholderText, rerender, findByText } = render(
      <SearchBookingsLocationPicker {...defaultProps} />
    );

    const input = getByPlaceholderText('Location');
    userEvent.click(input);
    userEvent.type(input, 'man');
    window.HTMLElement.prototype.scrollIntoView = jest.fn();

    rerender(
      <SearchBookingsLocationPicker
        {...defaultProps}
        inputvalue="man"
        suggestions={mockSuggestions}
      />
    );

    const item = await findByText(mockSuggestions.places[0].suggestion);
    userEvent.click(item);

    await waitFor(async () => {
      expect(mockLocationSelectedHandler).toBeCalledWith(mockSuggestions.places[0]);
    });
  });

  it('should display error tooltip when there is an error in the input', async () => {
    const { getByRole, getByText } = render(
      <SearchBookingsLocationPicker {...defaultProps} inputvalue="wqwqrqr" showError />
    );

    await waitFor(() => {
      expect(getByRole('alert')).toBeInTheDocument();
      expect(getByText(mockErrorLabel)).toBeInTheDocument();
    });
  });
});
