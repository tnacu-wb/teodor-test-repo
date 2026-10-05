import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { FieldsType, FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import { Controller, useForm } from 'react-hook-form';

import { act, render, userEvent, waitFor } from '../../utils/test-utils';
import LocationDropdown from './LocationDropdown.component';

const fieldLabel = 'Hotel Location';
const mockSetClearHotelLocation = jest.fn();
const mockSetClearHotelName = jest.fn();
const mockSetValue = jest.fn();
const mockResetField = jest.fn();
const mockSetError = jest.fn();
const mockClearErrors = jest.fn();
const mockGetValues = jest.fn();
const mockErrors = {};
const mockProps = {
  clearHotelLocation: false,
  clearHotelName: false,
  setClearHotelLocation: mockSetClearHotelLocation,
  setClearHotelName: mockSetClearHotelName,
};
const mockGetFieldProps = jest.fn();
const mockGetSuggestions = jest.fn();
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
    {
      suggestion: 'Isle of SKye',
      managedPlaceId: '5',
      placeId: 'ChIJ1YEuRDCFY0gRDeDw8bxbAuo',
    },
  ],
  places: [
    {
      suggestion: 'London, UK',
      placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
    },
  ],
};

const emptySuggestions = {
  managedPlaces: [],
  places: [],
  properties: [],
};
const mockUseRouter = jest.fn().mockReturnValue({
  locale: 'en',
});
const mockQueryRequest = jest.fn();

const mockHotels = [
  {
    brand: 'PI',
    distance: '0.73',
    hotelId: 'LONLEI',
    name: 'London Leicester Square',
  },
  {
    brand: 'HUB',
    distance: '0.91',
    hotelId: 'LONSTM',
    name: 'hub London Covent Garden',
  },
  {
    brand: 'ZIP',
    distance: '1.35',
    hotelId: 'LONWAB',
    name: 'London Westminster Abbey',
  },
];

const mockModalResponse = {
  isFetching: false,
  isError: false,
  data: {
    hotelsLocations: mockHotels,
  },
};

jest.mock('@whitbread-eos/api', () => ({
  ...jest.requireActual('@whitbread-eos/api'),
  getSuggestions: () => mockGetSuggestions(),
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => mockQueryRequest(),
}));

const Component = () => {
  const { control } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'hotelLocation',
    label: fieldLabel,
    props: { clearHotelFields: mockGetFieldProps() },
  };

  const props = {
    formField: formField,
    errors: mockErrors,
    handleSetValue: mockSetValue,
    handleResetField: mockResetField,
    handleSetError: mockSetError,
    handleClearErrors: mockClearErrors,
    getValues: mockGetValues,
  };
  return (
    <QueryClientProvider client={new QueryClient()}>
      <Controller
        name={formField.name}
        control={control}
        render={({ field }) => {
          return <LocationDropdown {...props} field={field} />;
        }}
      />
    </QueryClientProvider>
  );
};

describe('LocationDropdown', () => {
  beforeEach(() => {
    mockGetSuggestions.mockImplementation(() => {
      return mockSuggestions;
    });
    mockGetFieldProps.mockImplementation(() => {
      return mockProps;
    });
    mockQueryRequest.mockImplementation(() => {
      return mockModalResponse;
    });
    jest.clearAllMocks();
    document.querySelectorAll("[role='dialog']")?.forEach((elem) => elem.remove());
  });

  it('should render the component', () => {
    const { getByPlaceholderText } = render(<Component />);
    expect(getByPlaceholderText(fieldLabel)).toBeInTheDocument();
  });

  it('should update the text in the field as the user types', async () => {
    const { getByPlaceholderText } = render(<Component />);

    const input = getByPlaceholderText(fieldLabel);

    await act(async () => {
      userEvent.type(input, 'ma');
    });

    await waitFor(() => {
      expect(input).toHaveValue('ma');
    });
  });

  it('should display suggestions for locations when valid input is entered', async () => {
    const { getByPlaceholderText, findByRole, getByText } = render(<Component />);

    const input = getByPlaceholderText(fieldLabel);

    await act(async () => {
      userEvent.type(input, 'manc');
    });

    await waitFor(async () => {
      const popover = await findByRole('dialog');
      expect(popover).toBeInTheDocument();
      expect(getByText('Isle of Man')).toBeInTheDocument();
      expect(getByText('Isle of SKye')).toBeInTheDocument();
      expect(getByText('London, UK')).toBeInTheDocument();
    });
  });

  it('should not display suggestions for properties when valid input is entered', async () => {
    const { getByPlaceholderText, findByRole, queryByText } = render(<Component />);

    await act(async () => {
      const input = getByPlaceholderText(fieldLabel);
      userEvent.type(input, 'manc');
    });

    await waitFor(async () => {
      const popover = await findByRole('dialog');
      expect(popover).toBeInTheDocument();
      expect(queryByText('hub London Soho')).not.toBeInTheDocument();
      expect(queryByText('London Richmond')).not.toBeInTheDocument();
      expect(queryByText('London Archway')).not.toBeInTheDocument();
    });
  });

  it('should set error when there are no suggestions for the search term', async () => {
    mockGetSuggestions.mockImplementation(() => {
      return emptySuggestions;
    });
    const { getByPlaceholderText } = render(<Component />);

    await act(async () => {
      const input = getByPlaceholderText(fieldLabel);
      userEvent.type(input, 'qwerqwer');
    });

    await waitFor(() => {
      expect(mockSetError).toHaveBeenCalled();
    });
  });

  it('should clear the field when clear icon is pressed', async () => {
    const { getByPlaceholderText, findByLabelText } = render(<Component />);

    await act(async () => {
      const input = getByPlaceholderText(fieldLabel);
      userEvent.type(input, 'ma');
      const clearIcon = await findByLabelText('clear-icon');
      userEvent.click(clearIcon);
    });

    await waitFor(() => {
      expect(mockSetClearHotelLocation).toHaveBeenCalledWith(true);
      expect(mockSetClearHotelName).toHaveBeenCalledWith(true);
    });
  });

  it('should clear the field when input is removed manually', async () => {
    const { getByPlaceholderText } = render(<Component />);

    await act(async () => {
      const input = getByPlaceholderText(fieldLabel);
      userEvent.type(input, 'man');
      userEvent.clear(input);
    });

    await waitFor(() => {
      expect(mockSetClearHotelLocation).toHaveBeenCalledWith(true);
      expect(mockSetClearHotelName).toHaveBeenCalledWith(true);
    });
  });

  it('should clear the field when clearHotelLocation is true', async () => {
    mockGetFieldProps.mockImplementation(() => {
      return { ...mockProps, clearHotelLocation: true };
    });
    const { getByPlaceholderText } = render(<Component />);
    const input = getByPlaceholderText(fieldLabel);

    await waitFor(() => {
      expect(input).toHaveValue('');
      expect(mockResetField).toHaveBeenCalled();
      expect(mockClearErrors).toHaveBeenCalled();
      expect(mockSetClearHotelLocation).toHaveBeenCalledWith(false);
    });
  });

  it('should open the hotel selection modal when a location is selected', async () => {
    const { getByPlaceholderText, getByTestId, getByText, getByRole } = render(<Component />);

    const input = getByPlaceholderText(fieldLabel);

    await act(async () => {
      userEvent.type(input, 'manc');
    });

    await waitFor(() => {
      const popover = getByTestId('SearchBookingsLocationPicker-autocompleteList');
      expect(popover).toBeInTheDocument();
      expect(getByText('Isle of Man')).toBeInTheDocument();
      expect(getByText('London, UK')).toBeInTheDocument();
    });

    const item = getByText('Isle of Man');

    await act(async () => {
      userEvent.click(item);
    });

    await waitFor(() => {
      expect(getByRole('dialog')).toBeInTheDocument();
      expect(getByText('ccui.manageBooking.hotelsModalTitle')).toBeInTheDocument();
    });
  });

  it('should not select a location and hotel when the modal is closed', async () => {
    const { getByPlaceholderText, getByTestId, getByText, getByRole } = render(<Component />);

    const input = getByPlaceholderText(fieldLabel);
    await userEvent.type(input, 'manc');

    await waitFor(() => {
      const popover = getByTestId('SearchBookingsLocationPicker-autocompleteList');
      expect(popover).toBeInTheDocument();
      expect(getByText('Isle of SKye')).toBeInTheDocument();
    });

    const item = getByText('Isle of SKye');
    await userEvent.click(item);

    const cancelBtn = getByRole('button', { name: 'Close' });
    await userEvent.click(cancelBtn);

    await waitFor(() => {
      expect(mockSetValue).not.toHaveBeenCalled();
      expect(mockSetClearHotelLocation).toHaveBeenCalledWith(true);
      expect(mockSetClearHotelName).toHaveBeenCalledWith(true);
    });
  });

  it('should select a location and hotel when a hotel is picked from the modal', async () => {
    const { getByPlaceholderText, getByTestId, getByText, getAllByTestId } = render(<Component />);

    const input = getByPlaceholderText(fieldLabel);

    await userEvent.type(input, 'manc');

    await waitFor(() => {
      const popover = getByTestId('SearchBookingsLocationPicker-autocompleteList');
      expect(popover).toBeInTheDocument();
      expect(getByText('London, UK')).toBeInTheDocument();
    });

    const item = getByText('London, UK');
    await userEvent.click(item);

    const listItem = getAllByTestId('HotelDropdownModal-ListItem')[0];
    await userEvent.click(listItem);

    await waitFor(() => {
      expect(mockSetValue).toHaveBeenCalled();
    });
  });
});
