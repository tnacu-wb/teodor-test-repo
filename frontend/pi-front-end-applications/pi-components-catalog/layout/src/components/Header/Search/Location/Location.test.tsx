import { LOCALES } from '@whitbread-eos/api';
import React from 'react';

import {
  render,
  mockUseTranslation,
  fireEvent,
  waitFor,
  screen,
} from '../../../../utils/test-utils';
import Location, { LocationProps } from './Location.component';

const mockProps: LocationProps = {
  labels: {},
  location: '',
  setLocation: jest.fn(),
  setSelectedLocation: jest.fn(),
  selectedLocation: {},
  icons: {},
  onOpenChange: () => {
    return;
  },
  mobile: false,
  globalLabels: {},
  showError: false,
  setShowError: jest.fn(),
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getInitials: serverUtils.getInitials,
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    RolesRequired: serverUtils.RolesRequired,
    cn: (...args: any[]) => args.filter(Boolean).join(' '),
    useTranslation: mockUseTranslation,
  };
});

// Mock LocationResults component
jest.mock('./LocationResults/index', () => ({
  LocationResults: ({ location, isInputBlured }: any) => {
    if (!location || location.length < 3 || isInputBlured) return null;

    return (
      <div data-testid="Location-Results-Container">
        <div data-testid="IB-Places-List">
          <div>London, UK TEST</div>
        </div>
        <div data-testid="IB-Hotels-List">
          <button>hub London Soho</button>
        </div>
      </div>
    );
  },
}));

describe('Location Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render Location component', () => {
    const { getByTestId } = render(<Location {...mockProps} />);
    const input = getByTestId('IB-Location-Container');
    expect(input).toBeInTheDocument();
  });

  it('should render Location component and focus input then blur', () => {
    const { getByTestId } = render(<Location {...mockProps} />);
    const input = getByTestId('IB-Location-Input');
    expect(input).toBeInTheDocument();

    fireEvent.focus(input);
    fireEvent.blur(input);
    expect(getByTestId('IB-Location-Container')).toBeInTheDocument();
  });

  it('should render Location component and focus input then blur when suggestion exists', () => {
    mockProps.selectedLocation.suggestion = 'abc';
    const { getByTestId } = render(<Location {...mockProps} />);
    const input = getByTestId('IB-Location-Input');
    expect(input).toBeInTheDocument();
    fireEvent.focus(input);
    fireEvent.blur(input);
    expect(getByTestId('IB-Location-Container')).toBeInTheDocument();
  });

  it('should render Location component and change location', () => {
    const { getByTestId } = render(<Location {...mockProps} />);
    const input = getByTestId('IB-Location-Input');
    expect(input).toBeInTheDocument();

    fireEvent.change(input, { target: { value: 'abc' } });
    expect(getByTestId('IB-Location-Container')).toBeInTheDocument();
  });

  it('should open modal when mobile is true', async () => {
    const props = { ...mockProps, mobile: true };
    const { getByTestId } = render(<Location {...props} />);

    const input = getByTestId('IB-Location-Input-Mobile');
    input.click();

    await waitFor(() => {
      expect(getByTestId('IB-Search-Container-Mobile-Location')).toBeInTheDocument();
    });
  });

  it('should call handleMobileInputFocus and handleMobileInputBlur', async () => {
    const props = {
      ...mockProps,
      mobile: true,
      onOpenChange: jest.fn(),
    };

    const { getByTestId } = render(<Location {...props} />);

    const input = getByTestId('IB-Location-Input-Mobile');

    await waitFor(() => {
      expect(input).toBeInTheDocument();
    });

    fireEvent.focus(input);
    fireEvent.blur(input);

    expect(getByTestId('IB-Search-Container-Mobile-Location')).toBeInTheDocument();
  });

  it('should cancel the mobile modal after changing input value', async () => {
    const props = {
      ...mockProps,
      mobile: true,
      onOpenChange: jest.fn(),
    };

    const { getByTestId } = render(<Location {...props} />);

    const input = getByTestId('IB-Location-Input-Mobile');

    await waitFor(() => {
      expect(input).toBeInTheDocument();
    });

    fireEvent.focus(input);
    fireEvent.change(input, { target: { value: 'abc' } });

    const cancelButton = getByTestId('Mobile-Modal-Close-Button');
    await waitFor(() => {
      expect(cancelButton).toBeInTheDocument();
    });

    fireEvent.click(cancelButton);

    expect(getByTestId('IB-Search-Container-Mobile-Location')).toBeInTheDocument();
  });

  it('should render Location component and focus main input then blur when mobile is true', () => {
    const { getByTestId } = render(<Location {...mockProps} mobile={true} />);
    const input = getByTestId('IB-Location-Input');
    expect(input).toBeInTheDocument();
    fireEvent.focus(input);
    fireEvent.blur(input);

    expect(getByTestId('IB-Search-Container-Mobile-Location')).toBeInTheDocument();
  });

  it('should not blur input when focus moves to results container', async () => {
    const props = {
      ...mockProps,
      location: 'London',
      onOpenChange: jest.fn(),
    };
    render(<Location {...props} />);

    const input = screen.getByTestId('IB-Location-Input');
    fireEvent.focus(input);

    // Wait for LocationResults to render
    const resultsContainer = await screen.findByTestId('Location-Results-Container');
    expect(resultsContainer).toBeInTheDocument();

    const blurEvent = new FocusEvent('blur', {
      bubbles: true,
      relatedTarget: resultsContainer as EventTarget,
    });

    fireEvent(input, blurEvent);
    expect(props.onOpenChange).not.toHaveBeenCalledWith(false);
  });

  it('should blur input when focus moves to child of results container', async () => {
    const props = {
      ...mockProps,
      location: 'London',
      onOpenChange: jest.fn(),
    };

    render(<Location {...props} />);
    const input = screen.getByTestId('IB-Location-Input');

    fireEvent.focus(input);
    const childElement = await screen.findByText('London, UK TEST');

    const blurEvent = new FocusEvent('blur', {
      bubbles: true,
      relatedTarget: childElement,
    });

    fireEvent(input, blurEvent);
    expect(props.onOpenChange).not.toHaveBeenCalledWith(false);
  });

  it('should not blur when focus moves to places list item', async () => {
    const props = {
      ...mockProps,
      location: 'London',
      onOpenChange: jest.fn(),
    };

    render(<Location {...props} />);
    const input = screen.getByTestId('IB-Location-Input');
    fireEvent.focus(input);

    await screen.findByTestId('IB-Places-List');
    const placesListItem = screen.getByText('London, UK TEST');

    const blurEvent = new FocusEvent('blur', {
      bubbles: true,
      relatedTarget: placesListItem,
    });
    fireEvent(input, blurEvent);
    expect(props.onOpenChange).not.toHaveBeenCalledWith(false);
  });

  it('should not blur when focus moves to hotels list item', async () => {
    const props = {
      ...mockProps,
      location: 'London',
      onOpenChange: jest.fn(),
    };

    render(<Location {...props} />);
    const input = screen.getByTestId('IB-Location-Input');
    fireEvent.focus(input);

    await screen.findByTestId('IB-Hotels-List');
    const hotelListItem = screen.getByText('hub London Soho');

    const blurEvent = new FocusEvent('blur', {
      bubbles: true,
      relatedTarget: hotelListItem,
    });

    fireEvent(input, blurEvent);
    expect(props.onOpenChange).not.toHaveBeenCalledWith(false);
  });

  it('should not blur mobile input when focus moves to results container', async () => {
    const props = {
      ...mockProps,
      mobile: true,
      location: 'London',
      onOpenChange: jest.fn(),
    };

    render(<Location {...props} />);

    // Open mobile modal
    const mainInput = screen.getByTestId('IB-Location-Input-Mobile');
    fireEvent.focus(mainInput);

    // Wait for modal to be visible
    await waitFor(() => {
      expect(screen.getByTestId('Location-Header')).toBeInTheDocument();
    });

    // Get the mobile modal input and results container
    const resultsContainer = await screen.findByTestId('Location-Results-Container');
    const blurEvent = new FocusEvent('blur', {
      bubbles: true,
      relatedTarget: resultsContainer as EventTarget,
    });

    fireEvent(mainInput, blurEvent);
    expect(screen.getByTestId('Location-Header')).toBeInTheDocument();
  });

  it('should not blur mobile input when focus stays within mobile modal container', async () => {
    const props = {
      ...mockProps,
      mobile: true,
      location: 'London',
      onOpenChange: jest.fn(),
    };

    render(<Location {...props} />);

    const mainInput = screen.getByTestId('IB-Location-Input-Mobile');
    fireEvent.focus(mainInput);

    await waitFor(() => {
      expect(screen.getByTestId('Location-Header')).toBeInTheDocument();
    });

    const locationHeader = screen.getByTestId('Location-Header');
    const blurEvent = new FocusEvent('blur', {
      bubbles: true,
      relatedTarget: locationHeader as EventTarget,
    });

    fireEvent(mainInput, blurEvent);
    expect(screen.getByTestId('Location-Header')).toBeInTheDocument();
  });

  it('should not blur mobile input when focus moves to close button within modal', async () => {
    const props = {
      ...mockProps,
      mobile: true,
      location: 'London',
      onOpenChange: jest.fn(),
    };

    render(<Location {...props} />);

    const mainInput = screen.getByTestId('IB-Location-Input-Mobile');
    fireEvent.focus(mainInput);

    await waitFor(() => {
      expect(screen.getByTestId('Mobile-Modal-Close-Button')).toBeInTheDocument();
    });

    const closeButton = screen.getByTestId('Mobile-Modal-Close-Button');
    const blurEvent = new FocusEvent('blur', {
      bubbles: true,
      relatedTarget: closeButton as EventTarget,
    });

    fireEvent(mainInput, blurEvent);
    // Modal should still be visible (blur was prevented)
    expect(screen.getByTestId('Location-Header')).toBeInTheDocument();
  });

  it('should blur mobile input when focus leaves the modal entirely (relatedTarget is null)', async () => {
    const props = {
      ...mockProps,
      mobile: true,
      location: 'London',
      onOpenChange: jest.fn(),
    };

    render(<Location {...props} />);

    // Open mobile modal by focusing the main input
    const mainInput = screen.getByTestId('IB-Location-Input');
    fireEvent.focus(mainInput);

    // Wait for modal to be visible and get its input
    await waitFor(() => {
      expect(screen.getByTestId('Location-Header')).toBeInTheDocument();
    });

    const mobileModalInput = screen.getByTestId('IB-Location-Input-Mobile');
    expect(mobileModalInput).toBeInTheDocument();

    // Focus the modal input to set isMobileModalInputBlurred to false
    fireEvent.focus(mobileModalInput);

    // Results should be visible since location='London' and input is not blurred
    await waitFor(() => {
      expect(screen.getByTestId('Location-Results-Container')).toBeInTheDocument();
    });

    // Simulate blur with no relatedTarget (focus leaving the document/modal)
    fireEvent.blur(mobileModalInput, { relatedTarget: null });

    // After blur, results should disappear because isMobileModalInputBlurred becomes true
    await waitFor(() => {
      expect(screen.queryByTestId('Location-Results-Container')).not.toBeInTheDocument();
    });
  });

  it('should blur mobile input when focus moves to element outside modal', async () => {
    const props = {
      ...mockProps,
      mobile: true,
      location: 'London',
      onOpenChange: jest.fn(),
    };

    render(
      <div>
        <button data-testid="outside-button">Outside Button</button>
        <Location {...props} />
      </div>
    );

    const mainInput = screen.getByTestId('IB-Location-Input');
    fireEvent.focus(mainInput);

    await waitFor(() => {
      expect(screen.getByTestId('Location-Header')).toBeInTheDocument();
    });

    const mobileModalInput = screen.getByTestId('IB-Location-Input-Mobile');
    fireEvent.focus(mobileModalInput);

    // Get an element outside the modal
    const outsideButton = screen.getByTestId('outside-button');

    // Simulate blur with relatedTarget being an element outside the modal
    fireEvent.blur(mobileModalInput, { relatedTarget: outsideButton });

    // Modal should still be in DOM but blur should have been triggered
    expect(screen.getByTestId('IB-Search-Container-Mobile-Location')).toBeInTheDocument();
  });

  it('should call setShowError with false when clearing location value', async () => {
    const setShowErrorMock = jest.fn();
    const setLocationMock = jest.fn();
    const props = {
      ...mockProps,
      mobile: false,
      location: 'London',
      setLocation: setLocationMock,
      onOpenChange: jest.fn(),
      setShowError: setShowErrorMock,
    };
    render(<Location {...props} />);

    const input = screen.getByTestId('IB-Location-Input');
    // First focus to unblur
    fireEvent.focus(input);
    // Clear the input value
    fireEvent.change(input, { target: { value: '' } });

    // setShowError should be called with false when value is empty
    expect(setShowErrorMock).toHaveBeenCalledWith(false);
  });

  it('should sync isInputBlured when isOpen changes to false on desktop', async () => {
    const props = {
      ...mockProps,
      mobile: false,
      location: 'London',
      isOpen: true,
      onOpenChange: jest.fn(),
    };
    const { rerender } = render(<Location {...props} />);

    // Rerender with isOpen=false to trigger the useEffect
    rerender(<Location {...props} isOpen={false} />);
    expect(screen.getByTestId('IB-Search-Container-Desktop-Location')).toBeInTheDocument();
  });

  it('should call hideEditSearch when showError is true', async () => {
    const hideEditSearchMock = jest.fn();
    const props = {
      ...mockProps,
      showError: true,
      hideEditSearch: hideEditSearchMock,
    };

    render(<Location {...props} />);
    expect(hideEditSearchMock).toHaveBeenCalled();
  });

  it('should close modal and clear error when selectedLocation has suggestion on mobile modal close', async () => {
    const setShowErrorMock = jest.fn();
    const onOpenChangeMock = jest.fn();
    const props = {
      ...mockProps,
      mobile: true,
      location: 'London',
      selectedLocation: { suggestion: 'London, UK' } as any,
      onOpenChange: onOpenChangeMock,
      setShowError: setShowErrorMock,
    };

    render(<Location {...props} />);

    const mainInput = screen.getByTestId('IB-Location-Input-Mobile');
    fireEvent.focus(mainInput);

    await waitFor(() => {
      expect(screen.getByTestId('Mobile-Modal-Close-Button')).toBeInTheDocument();
    });

    const closeButton = screen.getByTestId('Mobile-Modal-Close-Button');
    fireEvent.click(closeButton);

    expect(setShowErrorMock).toHaveBeenCalledWith(false);
    expect(onOpenChangeMock).toHaveBeenCalledWith(false);
  });
});
