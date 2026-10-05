import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { FieldsType, FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import { Controller, useForm } from 'react-hook-form';

import { act, render, userEvent, waitFor } from '../../utils/test-utils';
import HotelDropdown from './HotelDropdown.component';

const fieldLabel = 'Hotel Name';
const mockSetClearHotelLocation = jest.fn();
const mockSetClearHotelName = jest.fn();
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
const mockGetErrors = jest.fn();
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

jest.mock('@whitbread-eos/api', () => ({
  ...jest.requireActual('@whitbread-eos/api'),
  getSuggestions: () => mockGetSuggestions(),
}));

const Component = () => {
  const { control } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'hotelName',
    label: fieldLabel,
    props: { clearHotelFields: mockGetFieldProps() },
  };

  const props = {
    formField: formField,
    errors: mockGetErrors(),
    handleSetValue: jest.fn(),
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
          return <HotelDropdown {...props} field={field} />;
        }}
      />
    </QueryClientProvider>
  );
};

describe('HotelDropdown', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockGetSuggestions.mockImplementation(() => {
      return mockSuggestions;
    });
    mockGetFieldProps.mockImplementation(() => {
      return mockProps;
    });
    mockGetErrors.mockImplementation(() => {
      return mockErrors;
    });
  });

  it('should render the component', () => {
    const { getByPlaceholderText } = render(<Component />);
    expect(getByPlaceholderText(fieldLabel)).toBeInTheDocument();
  });

  it('should update the text in the field as the user types', () => {
    const { getByPlaceholderText } = render(<Component />);

    const input = getByPlaceholderText(fieldLabel);
    userEvent.type(input, 'ma');

    expect(input).toHaveValue('ma');
  });

  it('should display suggestions for properties when valid input is entered', async () => {
    const { getByPlaceholderText, findByRole, getByText } = render(<Component />);

    const input = getByPlaceholderText(fieldLabel);
    userEvent.type(input, 'manc');

    await waitFor(async () => {
      const popover = await findByRole('dialog');
      expect(popover).toBeInTheDocument();
      expect(getByText('hub London Soho')).toBeInTheDocument();
      expect(getByText('London Richmond')).toBeInTheDocument();
      expect(getByText('London Archway')).toBeInTheDocument();
    });
  });

  it('should not display suggestions for locations when valid input is entered', async () => {
    const { getByPlaceholderText, findByRole, queryByText } = render(<Component />);

    const input = getByPlaceholderText(fieldLabel);
    userEvent.type(input, 'manc');

    await waitFor(async () => {
      const popover = await findByRole('dialog');
      expect(popover).toBeInTheDocument();
      expect(queryByText('Isle of Man')).not.toBeInTheDocument();
      expect(queryByText('London, UK')).not.toBeInTheDocument();
    });
  });

  it('should set error when there are no suggestions for the search term', async () => {
    mockGetSuggestions.mockImplementation(() => {
      return emptySuggestions;
    });
    const { getByPlaceholderText, queryByRole } = render(<Component />);

    const input = getByPlaceholderText(fieldLabel);
    userEvent.type(input, 'qwerqwer');

    await waitFor(() => {
      expect(queryByRole('dialog')).not.toBeInTheDocument();
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
      await userEvent.type(input, 'man');
      await userEvent.clear(input);
    });

    await waitFor(() => {
      expect(mockSetClearHotelLocation).toHaveBeenCalledWith(true);
      expect(mockSetClearHotelName).toHaveBeenCalledWith(true);
    });
  });

  it('should clear the field when clearHotelName is true', async () => {
    mockGetFieldProps.mockImplementation(() => {
      return { ...mockProps, clearHotelName: true };
    });
    const { getByPlaceholderText } = render(<Component />);
    const input = getByPlaceholderText(fieldLabel);

    await waitFor(() => {
      expect(input).toHaveValue('');
      expect(mockResetField).toHaveBeenCalled();
      expect(mockClearErrors).toHaveBeenCalled();
      expect(mockSetClearHotelName).toHaveBeenCalledWith(false);
    });
  });

  it('should update the hotel name when a hotel is selected', async () => {
    const { getByPlaceholderText, findByRole, getByText } = render(<Component />);

    const input = getByPlaceholderText(fieldLabel);
    userEvent.type(input, 'manc');
    const popover = await findByRole('dialog');
    expect(popover).toBeInTheDocument();
    expect(getByText('hub London Soho')).toBeInTheDocument();
    expect(getByText('London Richmond')).toBeInTheDocument();
    expect(getByText('London Archway')).toBeInTheDocument();
    const item = getByText('hub London Soho');
    userEvent.click(item);

    await waitFor(() => {
      expect(input).toHaveValue('hub London Soho');
    });
  });

  it('should diaply an error tooltip when there are no properties for the search term', async () => {
    mockGetErrors.mockImplementation(() => {
      return {
        ...mockErrors,
        hotelDetails: { type: 'custom', message: 'ccui.manageBooking.hotelName.error' },
      };
    });
    const { getByRole, getByText } = render(<Component />);

    await waitFor(() => {
      expect(getByRole('alert')).toBeInTheDocument();
      expect(getByText('ccui.manageBooking.hotelName.error')).toBeInTheDocument();
    });
  });
});
