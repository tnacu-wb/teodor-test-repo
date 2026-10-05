import { render, fireEvent, waitFor, screen } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import LocationResults from './LocationResults.component';

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
      code: 'LONTEST',
      brand: 'ZIP',
      suggestion: 'zip Test',
      geometry: {
        type: 'Point',
        coordinates: [-0.136549, 51.513614],
      },
    },
    {
      code: 'LONTEST2',
      brand: 'PI',
      suggestion: 'London test',
      geometry: {
        type: 'Point',
        coordinates: [-0.136549, 51.513614],
      },
    },
  ],
  places: [
    {
      suggestion: 'London, UK TEST',
      placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
    },
  ],
};

const mockProps = {
  location: 'loneus',
  labels: {} as Record<string, never>,
  setLocation: jest.fn(),
  isInputBlured: false,
  selectedLocation: {},
  setSelectedLocation: jest.fn(),
  onResultsFound: jest.fn(),
  globalLabels: {},
  onOpenChange: jest.fn(),
  mobile: false,
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    getLocationResults: () => {
      return mockSuggestions;
    },
    cn: jest.fn((...classes) => classes.filter(Boolean).join(' ')),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: (url: string) => url || '/',
    useTranslation: () => {
      return {
        t: (str: string) => str,
        i18n: {
          changeLanguage: () => new Promise(() => true),
        },
      };
    },
  };
});

// Mock scrollIntoView globally before tests run
beforeAll(() => {
  Element.prototype.scrollIntoView = jest.fn();
});

describe('LocationResults Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    jest.useFakeTimers();

    // Reset mockSuggestions
    mockSuggestions.properties = [
      {
        code: 'LONSOH',
        brand: 'HUB',
        suggestion: 'hub London Soho',
        geometry: { type: 'Point', coordinates: [-0.136549, 51.513614] },
      },
      {
        code: 'LONTEST',
        brand: 'ZIP',
        suggestion: 'zip Test',
        geometry: { type: 'Point', coordinates: [-0.136549, 51.513614] },
      },
      {
        code: 'LONTEST2',
        brand: 'PI',
        suggestion: 'London test',
        geometry: { type: 'Point', coordinates: [-0.136549, 51.513614] },
      },
    ];
    mockSuggestions.places = [
      {
        suggestion: 'London, UK TEST',
        placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
      },
    ];
  });

  afterEach(() => {
    jest.runOnlyPendingTimers();
    jest.useRealTimers();
  });

  describe('Rendering', () => {
    it('should render LocationResults container with results', async () => {
      const props = { ...mockProps, location: 'loneus' };
      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(screen.getByTestId('Location-Results-Container')).toBeInTheDocument();
      });
    });

    it('should not render with empty location', async () => {
      const props = { ...mockProps, location: '' };
      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(screen.queryByTestId('Location-Results-Container')).not.toBeInTheDocument();
      });
    });

    it('should not render with location less than minimum length', async () => {
      const props = { ...mockProps, location: 'lo' };
      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(screen.queryByTestId('Location-Results-Container')).not.toBeInTheDocument();
      });
    });

    it('should not render when input is blurred', async () => {
      const props = { ...mockProps, location: 'loneus', isInputBlured: true };
      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(screen.queryByTestId('Location-Results-Container')).not.toBeInTheDocument();
      });
    });

    it('should render places list when places exist', async () => {
      const props = { ...mockProps, location: 'loneus' };
      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(screen.getByTestId('IB-Places-List')).toBeInTheDocument();
        expect(screen.getByText('London, UK TEST')).toBeInTheDocument();
      });
    });

    it('should render hotels list when hotels exist', async () => {
      const props = { ...mockProps, location: 'loneus' };
      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(screen.getByTestId('IB-Hotels-List')).toBeInTheDocument();
        expect(screen.getByText('hub London Soho')).toBeInTheDocument();
      });
    });

    it('should render with globalLabels for hotel icons', async () => {
      const props = {
        ...mockProps,
        location: 'loneus',
        globalLabels: {
          brand: {
            piLogo: '/pi-logo.svg',
            zipLogo: '/zip-logo.svg',
            hubLogo: '/hub-logo.svg',
          },
        },
      };
      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(screen.getByTestId('Location-Results-Container')).toBeInTheDocument();
      });
    });
  });

  describe('User Interactions', () => {
    it('should call handlers when clicking on a place', async () => {
      const props = { ...mockProps, location: 'loneus' };
      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(screen.getByText('London, UK TEST')).toBeInTheDocument();
      });

      const firstPlace = screen.getByText('London, UK TEST');
      fireEvent.mouseDown(firstPlace);

      expect(props.setLocation).toHaveBeenCalledWith('London, UK TEST');
      expect(props.setSelectedLocation).toHaveBeenCalled();
      expect(props.onOpenChange).toHaveBeenCalledWith(false);
    });

    it('should call handlers when clicking on a hotel', async () => {
      const props = { ...mockProps, location: 'loneus' };
      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(screen.getByText('hub London Soho')).toBeInTheDocument();
      });

      const firstHotel = screen.getByText('hub London Soho');
      fireEvent.mouseDown(firstHotel);

      expect(props.setLocation).toHaveBeenCalledWith('hub London Soho');
      expect(props.setSelectedLocation).toHaveBeenCalled();
      expect(props.onOpenChange).toHaveBeenCalledWith(false);
    });

    it('should call onResultClick callback when provided', async () => {
      const onResultClick = jest.fn();
      const props = { ...mockProps, location: 'loneus', onResultClick };
      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(screen.getByText('London, UK TEST')).toBeInTheDocument();
      });

      const firstPlace = screen.getByText('London, UK TEST');
      fireEvent.mouseDown(firstPlace);

      expect(onResultClick).toHaveBeenCalledWith('London, UK TEST');
    });

    it('should update focused index on mouse enter', async () => {
      const props = { ...mockProps, location: 'loneus' };
      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(screen.getByText('London, UK TEST')).toBeInTheDocument();
      });

      const firstPlace = screen.getByText('London, UK TEST');
      fireEvent.mouseEnter(firstPlace);
      expect(firstPlace.closest('div')).toHaveClass('bg-lightGrey5');
    });
  });

  describe('Keyboard Navigation', () => {
    it('should navigate down with ArrowDown key', async () => {
      const props = { ...mockProps, location: 'loneus' };
      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(screen.getByTestId('Location-Results-Container')).toBeInTheDocument();
      });

      fireEvent.keyDown(document, { key: 'ArrowDown' });

      await waitFor(() => {
        const places = screen.getByText('London, UK TEST').closest('div');
        expect(places).toHaveClass('bg-lightGrey5');
      });
    });

    it('should navigate up with ArrowUp key', async () => {
      const props = { ...mockProps, location: 'loneus' };
      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(screen.getByTestId('Location-Results-Container')).toBeInTheDocument();
      });

      // Navigate down first
      fireEvent.keyDown(document, { key: 'ArrowDown' });
      fireEvent.keyDown(document, { key: 'ArrowDown' });

      // Then navigate up
      fireEvent.keyDown(document, { key: 'ArrowUp' });

      await waitFor(() => {
        const places = screen.getByText('London, UK TEST').closest('div');
        expect(places).toHaveClass('bg-lightGrey5');
      });
    });

    it('should select item with Enter key', async () => {
      const props = { ...mockProps, location: 'loneus' };
      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(screen.getByTestId('Location-Results-Container')).toBeInTheDocument();
      });

      fireEvent.keyDown(document, { key: 'ArrowDown' });
      fireEvent.keyDown(document, { key: 'Enter' });

      expect(props.setLocation).toHaveBeenCalled();
      expect(props.onOpenChange).toHaveBeenCalledWith(false);
    });

    it('should not navigate when input is blurred', async () => {
      const props = { ...mockProps, location: 'loneus', isInputBlured: true };
      render(<LocationResults {...props} />);

      fireEvent.keyDown(document, { key: 'ArrowDown' });
      expect(screen.queryByTestId('Location-Results-Container')).not.toBeInTheDocument();
    });

    it('should reset focusedIndex to -1 when Tab key is pressed', async () => {
      const props = { ...mockProps, location: 'London' };
      render(<LocationResults {...props} />);

      await screen.findByTestId('Location-Results-Container');

      fireEvent.keyDown(document, { key: 'ArrowDown' });
      await waitFor(() => {
        const firstPlace = screen.getByText('London, UK TEST').closest('div');
        expect(firstPlace).toHaveClass('bg-lightGrey5');
      });

      fireEvent.keyDown(document, { key: 'Tab' });

      // FocusedIndex reset
      await waitFor(() => {
        const firstPlace = screen.getByText('London, UK TEST').closest('div');
        expect(firstPlace).not.toHaveClass('bg-lightGrey5');
      });
    });
  });

  describe('Focus behaviour', () => {
    it('should focus results container when opened', async () => {
      const props = { ...mockProps, location: 'loneus' };
      render(<LocationResults {...props} />);

      await waitFor(() => {
        const container = screen.getByTestId('Location-Results-Container');
        expect(container).toBeInTheDocument();
      });
    });

    it('should focus hotels list when no places exist', async () => {
      mockSuggestions.places = [];
      mockSuggestions.properties = [
        {
          code: 'HOTEL1',
          brand: 'PI',
          suggestion: 'Test Hotel',
          geometry: { type: 'Point', coordinates: [0, 0] },
        },
      ];
      const props = { ...mockProps, location: 'loneus' };
      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(screen.getByTestId('IB-Hotels-List')).toBeInTheDocument();
      });
    });

    it('should close results container and reset focusedIndex when blurring from container', async () => {
      const props = {
        ...mockProps,
        location: 'London',
        onOpenChange: jest.fn(),
      };

      render(<LocationResults {...props} />);

      // wait for component to render
      jest.advanceTimersByTime(300);
      const resultsContainer = await screen.findByTestId('Location-Results-Container');

      fireEvent.keyDown(document, { key: 'ArrowDown' });
      await waitFor(() => {
        const firstPlace = screen.getByText('London, UK TEST').closest('div');
        expect(firstPlace).toHaveClass('bg-lightGrey5');
      });

      const outsideElement = document.createElement('button');
      document.body.appendChild(outsideElement);

      // Blur from container to outside
      const blurEvent = new FocusEvent('blur', {
        bubbles: true,
        relatedTarget: outsideElement,
      });

      fireEvent(resultsContainer, blurEvent);

      // Wait for the blur handler to execute
      await waitFor(() => {
        expect(props.onOpenChange).toHaveBeenCalled();
      });

      document.body.removeChild(outsideElement);
    });

    it('should not close results container when blur stays within container', async () => {
      const props = {
        ...mockProps,
        location: 'London',
        onOpenChange: jest.fn(),
      };

      render(<LocationResults {...props} />);

      const resultsContainer = await screen.findByTestId('Location-Results-Container');
      const firstPlace = await screen.findByText('London, UK TEST');

      // Blur from container to child element
      const blurEvent = new FocusEvent('blur', {
        bubbles: true,
        relatedTarget: firstPlace,
      });
      fireEvent(resultsContainer, blurEvent);

      // Should NOT close when blur stays within container
      expect(props.onOpenChange).not.toHaveBeenCalledWith(false);
    });

    it('should reset focusedIndex when results container regains focus', async () => {
      const props = { ...mockProps, location: 'London' };
      render(<LocationResults {...props} />);

      const resultsContainer = await screen.findByTestId('Location-Results-Container');
      fireEvent.keyDown(document, { key: 'ArrowDown' });

      await waitFor(() => {
        expect(screen.getByText('London, UK TEST').closest('div')).toHaveClass('bg-lightGrey5');
      });

      fireEvent.focus(resultsContainer);

      // Should reset, so ArrowDown should focus first item again
      fireEvent.keyDown(document, { key: 'ArrowDown' });

      await waitFor(() => {
        expect(screen.getByText('London, UK TEST').closest('div')).toHaveClass('bg-lightGrey5');
      });
    });
  });

  describe('Location Results Selection', () => {
    it('should default to first result when no exact match exists on desktop', async () => {
      const setSelectedLocationMock = jest.fn();
      const props = {
        ...mockProps,
        location: 'loneus', // Partial match, not exact
        setSelectedLocation: setSelectedLocationMock,
      };

      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(screen.getByTestId('Location-Results-Container')).toBeInTheDocument();
      });

      // Should default to first result (places come first in allLocationResults)
      expect(setSelectedLocationMock).toHaveBeenCalledWith(
        expect.objectContaining({ suggestion: 'London, UK TEST' })
      );
    });

    it('should set selectedLocation when exact match exists on desktop', async () => {
      const setSelectedLocationMock = jest.fn();
      // Update mock to include exact match
      mockSuggestions.places = [
        {
          suggestion: 'London, UK TEST',
          placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
        },
      ];

      const props = {
        ...mockProps,
        location: 'London, UK TEST', // Exact match
        setSelectedLocation: setSelectedLocationMock,
        mobile: false,
      };

      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(setSelectedLocationMock).toHaveBeenCalledWith(
          expect.objectContaining({ suggestion: 'London, UK TEST' })
        );
      });
    });

    it('should not set selectedLocation on mobile when input is not blurred even with exact match', async () => {
      const setSelectedLocationMock = jest.fn();
      mockSuggestions.places = [
        {
          suggestion: 'London, UK TEST',
          placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
        },
      ];

      const props = {
        ...mockProps,
        location: 'London, UK TEST',
        setSelectedLocation: setSelectedLocationMock,
        mobile: true,
        isInputBlured: false,
      };

      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(screen.getByTestId('Location-Results-Container')).toBeInTheDocument();
      });

      // setSelectedLocation should NOT be called on mobile when not blurred
      expect(setSelectedLocationMock).not.toHaveBeenCalled();
    });

    it('should set selectedLocation on mobile when input is blurred and exact match exists', async () => {
      const setSelectedLocationMock = jest.fn();
      mockSuggestions.places = [
        {
          suggestion: 'London, UK TEST',
          placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
        },
      ];

      const props = {
        ...mockProps,
        location: 'London, UK TEST',
        setSelectedLocation: setSelectedLocationMock,
        mobile: true,
        isInputBlured: true,
      };

      render(<LocationResults {...props} />);

      // Note: Results won't render because isInputBlured is true, but the getResults
      // function should still call setSelectedLocation
      await waitFor(() => {
        expect(setSelectedLocationMock).toHaveBeenCalledWith(
          expect.objectContaining({ suggestion: 'London, UK TEST' })
        );
      });
    });

    it('should default to first result on mobile when blurred and no exact match', async () => {
      const setSelectedLocationMock = jest.fn();
      const props = {
        ...mockProps,
        location: 'loneus', // No exact match
        setSelectedLocation: setSelectedLocationMock,
        mobile: true,
        isInputBlured: true,
      };

      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(setSelectedLocationMock).toHaveBeenCalledWith(
          expect.objectContaining({ suggestion: 'London, UK TEST' })
        );
      });
    });

    it('should not default to first result on mobile when not blurred and no exact match', async () => {
      const setSelectedLocationMock = jest.fn();
      const props = {
        ...mockProps,
        location: 'loneus', // No exact match
        setSelectedLocation: setSelectedLocationMock,
        mobile: true,
        isInputBlured: false,
      };

      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(screen.getByTestId('Location-Results-Container')).toBeInTheDocument();
      });

      // On mobile when not blurred, should not set selectedLocation
      expect(setSelectedLocationMock).not.toHaveBeenCalled();
    });
  });

  describe('Mobile onOpenChange behaviour', () => {
    it('should call onOpenChange(true) on mobile when results exist and isOpen is true', async () => {
      const onOpenChangeMock = jest.fn();
      const props = {
        ...mockProps,
        location: 'loneus',
        mobile: true,
        isInputBlured: false,
        onOpenChange: onOpenChangeMock,
      };

      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(onOpenChangeMock).toHaveBeenCalledWith(true);
      });
    });

    it('should call onOpenChange(false) on mobile when no results exist', async () => {
      mockSuggestions.places = [];
      mockSuggestions.properties = [];

      const onOpenChangeMock = jest.fn();
      const props = {
        ...mockProps,
        location: 'xyz',
        mobile: true,
        onOpenChange: onOpenChangeMock,
      };

      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(onOpenChangeMock).toHaveBeenCalledWith(false);
      });
    });

    it('should call onOpenChange(true) on mobile regardless of blur state when results exist', async () => {
      const onOpenChangeMock = jest.fn();
      const props = {
        ...mockProps,
        location: 'loneus',
        mobile: true,
        isInputBlured: true, // Even when blurred, should still call with true on mobile
        onOpenChange: onOpenChangeMock,
      };

      render(<LocationResults {...props} />);

      await waitFor(() => {
        // On mobile, onOpenChange is based on results existing, NOT blur state
        expect(onOpenChangeMock).toHaveBeenCalledWith(true);
      });
    });

    it('should call onOpenChange based on isInputBlured on desktop', async () => {
      const onOpenChangeMock = jest.fn();
      const props = {
        ...mockProps,
        location: 'loneus',
        mobile: false,
        isInputBlured: false,
        onOpenChange: onOpenChangeMock,
      };
      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(onOpenChangeMock).toHaveBeenCalledWith(true);
      });
    });

    it('should call onOpenChange(false) on desktop when input is blurred', async () => {
      const onOpenChangeMock = jest.fn();
      const props = {
        ...mockProps,
        location: 'loneus',
        mobile: false,
        isInputBlured: true,
        onOpenChange: onOpenChangeMock,
      };

      render(<LocationResults {...props} />);

      await waitFor(() => {
        expect(onOpenChangeMock).toHaveBeenCalledWith(false);
      });
    });
  });
});
