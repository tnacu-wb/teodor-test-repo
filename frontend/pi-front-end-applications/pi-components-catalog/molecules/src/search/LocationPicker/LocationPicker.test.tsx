import '@testing-library/jest-dom';
import { act } from '@testing-library/react';
import user from '@testing-library/user-event';
import { analytics } from '@whitbread-eos/utils';

import { render, screen } from '../../utils/test-utils';
import LocationPicker from './LocationPicker.component';

const initialData = {
  managedPlaces: [],
  places: [],
  properties: [],
};

const mockedSuggestions = {
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
      code: 'LONCRE',
      brand: 'PI',
      suggestion: 'Derry / Londonderry',
      geometry: {
        type: 'Point',
        coordinates: [-7.278844, 54.992376],
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
    {
      code: 'BARPTI',
      brand: 'PI',
      suggestion: 'London Barking',
      geometry: {
        type: 'Point',
        coordinates: [0.0715156, 51.535072],
      },
    },
  ],
  managedPlaces: [],
  places: [
    {
      suggestion: 'London, UK',
      placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
    },
    {
      suggestion: 'London Bridge, London, UK',
      placeId: 'ChIJxRO7WVEDdkgRrGM1fCYoHqY',
    },
    {
      suggestion: 'London Eye, London, UK',
      placeId: 'ChIJc2nSALkEdkgRkuoJJBfzkUI',
    },
    {
      suggestion: 'London Bridge Station, London, UK',
      placeId: 'ChIJ___OyFADdkgRkjYaWF6n5h0',
    },
    {
      suggestion: 'London Stansted Airport (STN), Bassingbourn Road, Stansted, UK',
      placeId: 'ChIJtxsqpbgEdkgRSCY1a5fQpDA',
    },
  ],
};

const mockedLocationErrorLabel = 'Please enter a location or a hotel';

const defaultProps = {
  styles: {},
  inputPlaceholder: 'Location',
  suggestions: initialData,
  locationErrorLabel: mockedLocationErrorLabel,
  isPriceFinder: false,
  showErrorMessage: true,
  errorMessage: 'error',
};

describe('LocationPicker', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('should render the component', () => {
    const { getByPlaceholderText } = render(<LocationPicker {...defaultProps} />);
    expect(getByPlaceholderText('Location')).toBeInTheDocument();
  });

  it('should display clear icon when user types a character', async () => {
    const { queryByText, getByRole } = render(<LocationPicker {...defaultProps} />);
    const input = getByRole('combobox');
    const groupTitle = queryByText(/Hotel/i);
    expect(groupTitle).not.toBeInTheDocument();

    await user.type(input, 'l');
    const clearIcon = screen.getByLabelText('clear-icon');
    expect(clearIcon).toBeVisible();
  });

  it('should call onInputChange when user types in the input', async () => {
    const onInputChange = jest.fn();
    const { getByRole } = render(
      <LocationPicker onInputChange={onInputChange} {...defaultProps} />
    );
    const input = getByRole('combobox');
    act(() => {
      user.type(input, 'lon');
    });
    expect(onInputChange).toBeCalledWith('lon');
  });

  it('should call onSelectLocation when a location is selected', async () => {
    const onSelectLocation = jest.fn();
    const { getByRole, rerender } = render(
      <LocationPicker {...defaultProps} onSelectLocation={onSelectLocation} />
    );

    const input = getByRole('combobox');
    await user.type(input, 'lon');
    window.HTMLElement.prototype.scrollIntoView = jest.fn();

    rerender(
      <LocationPicker
        {...defaultProps}
        onSelectLocation={onSelectLocation}
        suggestions={mockedSuggestions}
      />
    );
    const item = screen.getByText(mockedSuggestions.places[0].suggestion);
    await user.click(item);
    expect(onSelectLocation).toBeCalledWith(mockedSuggestions.places[0]);
  });

  describe('Accessibility', () => {
    it('should display error message with proper accessibility attributes', () => {
      const { getByRole, getByText } = render(
        <LocationPicker
          {...defaultProps}
          showErrorMessage={true}
          errorMessage="Please enter a location"
        />
      );

      const errorAlert = getByRole('alert');
      expect(errorAlert).toBeInTheDocument();
      expect(errorAlert).toHaveAttribute('id', 'location-picker-error');
      expect(errorAlert).toHaveAttribute('aria-live', 'polite');
      expect(getByText('Please enter a location')).toBeInTheDocument();
    });

    it('should set aria-invalid on input when showErrorMessage is true', () => {
      const { getByRole } = render(
        <LocationPicker {...defaultProps} showErrorMessage={true} errorMessage="Error" />
      );
      const input = getByRole('combobox');
      expect(input).toHaveAttribute('aria-invalid', 'true');
    });

    it('should set aria-describedby on input when showErrorMessage is true', () => {
      const { getByRole } = render(
        <LocationPicker {...defaultProps} showErrorMessage={true} errorMessage="Error" />
      );
      const input = getByRole('combobox');
      expect(input).toHaveAttribute('aria-describedby', 'location-picker-error');
    });

    it('should not set aria-describedby when showErrorMessage is false', () => {
      const { getByRole } = render(<LocationPicker {...defaultProps} showErrorMessage={false} />);
      const input = getByRole('combobox');
      expect(input).not.toHaveAttribute('aria-describedby');
    });

    it('should not render error message when showErrorMessage is false', () => {
      const { queryByRole } = render(
        <LocationPicker {...defaultProps} showErrorMessage={false} errorMessage="Error" />
      );
      const errorAlert = queryByRole('alert');
      expect(errorAlert).not.toBeInTheDocument();
    });
  });
});

jest.mock('@whitbread-eos/utils', () => {
  const actual = jest.requireActual('@whitbread-eos/utils');

  return {
    ...actual,
    analytics: {
      update: jest.fn(),
    },
  };
});

describe('LocationPicker - analytics brand', () => {
  const mockSuggestions = {
    managedPlaces: [],
    places: [],
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
    ],
  };

  const defaultProps = {
    inputPlaceholder: 'Search location',
    suggestions: mockSuggestions,
    styles: {},
    onSelectLocation: jest.fn(),
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call analytics.update with brand data when selectedLocation has brand', async () => {
    const { getByRole, findByText } = render(<LocationPicker {...defaultProps} />);

    const input = getByRole('combobox');

    await user.type(input, 'hub');

    const item = await findByText('hub London Soho');
    await user.click(item);

    expect(analytics.update).toHaveBeenCalledWith({
      selectedSearch: {
        brand: 'HUB',
        code: 'LONSOH',
        suggestion: 'hub London Soho',
        geometry: {
          type: 'Point',
          coordinates: [-0.136549, 51.513614],
        },
      },
    });
  });
});
