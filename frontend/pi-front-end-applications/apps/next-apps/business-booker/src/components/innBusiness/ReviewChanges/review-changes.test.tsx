import '@testing-library/jest-dom';
import { render, fireEvent, act } from '@testing-library/react';

import { ReviewChanges } from './review-changes';

const mockPush = jest.fn();

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
  };
});

jest.mock('next/navigation', () => ({
  useRouter: () => ({
    push: mockPush,
    replace: jest.fn(),
  }),
}));

jest.mock('./review-changes-modal', () => ({
  ReviewChangesModal: ({
    testId,
    open,
    onOpenChange,
    onDiscard,
    onContinue,
  }: {
    testId: string;
    open: boolean;
    onOpenChange?: () => void;
    onDiscard?: () => void;
    onContinue?: () => void;
  }) => (
    <div data-testid={testId} data-open={open}>
      <button data-testid="discard-btn" onClick={onDiscard}>
        Discard
      </button>
      <button data-testid="continue-btn" onClick={onContinue}>
        Continue
      </button>
      <button data-testid="close-btn" onClick={onOpenChange}>
        Close
      </button>
    </div>
  ),
}));

function clickLink(href: string) {
  const link = document.createElement('a');
  link.setAttribute('href', href);
  document.body.appendChild(link);
  link.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, button: 0 }));
  document.body.removeChild(link);
}

describe('ReviewChanges Component', () => {
  const mockOnDiscard = jest.fn();
  const mockOnContinue = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('Basic Rendering', () => {
    it('should render ReviewChanges component', () => {
      const { getByTestId } = render(<ReviewChanges />);
      expect(getByTestId('ReviewChanges')).toBeInTheDocument();
    });

    it('should render with default props', () => {
      const { getByTestId } = render(<ReviewChanges />);
      const modal = getByTestId('ReviewChanges');
      expect(modal).toHaveAttribute('data-open', 'false');
    });
  });

  describe('Manual Control Mode (isOpen=true)', () => {
    it('should show modal when isOpen is true', () => {
      const { getByTestId } = render(<ReviewChanges isOpen={true} />);
      const modal = getByTestId('ReviewChanges');
      expect(modal).toHaveAttribute('data-open', 'true');
    });

    it('should call onDiscard when discard button is clicked in manual mode', () => {
      const { getByTestId } = render(<ReviewChanges isOpen={true} onDiscard={mockOnDiscard} />);

      fireEvent.click(getByTestId('discard-btn'));
      expect(mockOnDiscard).toHaveBeenCalledTimes(1);
    });

    it('should call onContinue when continue button is clicked in manual mode', () => {
      const { getByTestId } = render(<ReviewChanges isOpen={true} onContinue={mockOnContinue} />);

      fireEvent.click(getByTestId('continue-btn'));
      expect(mockOnContinue).toHaveBeenCalledTimes(1);
    });

    it('should call onContinue when close button is clicked in manual mode', () => {
      const { getByTestId } = render(<ReviewChanges isOpen={true} onContinue={mockOnContinue} />);

      fireEvent.click(getByTestId('close-btn'));
      expect(mockOnContinue).toHaveBeenCalledTimes(1);
    });
  });

  describe('Navigation Guard Mode (click interception)', () => {
    it('should show modal when an internal link is clicked', () => {
      const { getByTestId } = render(<ReviewChanges />);

      act(() => {
        clickLink('/en/manage/employees');
      });

      expect(getByTestId('ReviewChanges')).toHaveAttribute('data-open', 'true');
    });

    it('should re-trigger click on the captured link when discard is pressed', () => {
      const { getByTestId } = render(<ReviewChanges />);

      const link = document.createElement('a');
      link.setAttribute('href', '/en/manage/employees');
      document.body.appendChild(link);
      const clickSpy = jest.spyOn(link, 'click');

      act(() => {
        link.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, button: 0 }));
      });

      fireEvent.click(getByTestId('discard-btn'));
      expect(clickSpy).toHaveBeenCalledTimes(1);

      clickSpy.mockRestore();
      document.body.removeChild(link);
    });

    it('should close modal when continue button is clicked after link interception', () => {
      const { getByTestId } = render(<ReviewChanges />);

      act(() => {
        clickLink('/en/manage/employees');
      });

      fireEvent.click(getByTestId('continue-btn'));
      expect(getByTestId('ReviewChanges')).toHaveAttribute('data-open', 'false');
      expect(mockPush).not.toHaveBeenCalled();
    });

    it('should close modal when close button is clicked after link interception', () => {
      const { getByTestId } = render(<ReviewChanges />);

      act(() => {
        clickLink('/en/manage/employees');
      });

      fireEvent.click(getByTestId('close-btn'));
      expect(getByTestId('ReviewChanges')).toHaveAttribute('data-open', 'false');
    });
  });

  describe('Guard Exclusions', () => {
    it('should not intercept access-restricted routes', () => {
      const { getByTestId } = render(<ReviewChanges />);

      act(() => {
        clickLink('/en/access-restricted');
      });

      expect(getByTestId('ReviewChanges')).toHaveAttribute('data-open', 'false');
    });

    it('should not intercept external links', () => {
      const { getByTestId } = render(<ReviewChanges />);

      act(() => {
        clickLink('https://example.com');
      });

      expect(getByTestId('ReviewChanges')).toHaveAttribute('data-open', 'false');
    });

    it('should not intercept protocol-relative URLs', () => {
      const { getByTestId } = render(<ReviewChanges />);

      act(() => {
        clickLink('//example.com/page');
      });

      expect(getByTestId('ReviewChanges')).toHaveAttribute('data-open', 'false');
    });

    it('should not intercept hash links', () => {
      const { getByTestId } = render(<ReviewChanges />);

      act(() => {
        clickLink('#section');
      });

      expect(getByTestId('ReviewChanges')).toHaveAttribute('data-open', 'false');
    });

    it('should not intercept links with target="_blank"', () => {
      const { getByTestId } = render(<ReviewChanges />);

      const link = document.createElement('a');
      link.setAttribute('href', '/en/manage/employees');
      link.setAttribute('target', '_blank');
      document.body.appendChild(link);

      act(() => {
        link.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, button: 0 }));
      });

      document.body.removeChild(link);
      expect(getByTestId('ReviewChanges')).toHaveAttribute('data-open', 'false');
    });

    it('should not intercept links with target="_top"', () => {
      const { getByTestId } = render(<ReviewChanges />);

      const link = document.createElement('a');
      link.setAttribute('href', '/en/manage/employees');
      link.setAttribute('target', '_top');
      document.body.appendChild(link);

      act(() => {
        link.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, button: 0 }));
      });

      document.body.removeChild(link);
      expect(getByTestId('ReviewChanges')).toHaveAttribute('data-open', 'false');
    });

    it('should not intercept links with download attribute', () => {
      const { getByTestId } = render(<ReviewChanges />);

      const link = document.createElement('a');
      link.setAttribute('href', '/en/download/file.pdf');
      link.setAttribute('download', '');
      document.body.appendChild(link);

      act(() => {
        link.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, button: 0 }));
      });

      document.body.removeChild(link);
      expect(getByTestId('ReviewChanges')).toHaveAttribute('data-open', 'false');
    });

    it('should not intercept clicks with metaKey pressed (Cmd+Click)', () => {
      const { getByTestId } = render(<ReviewChanges />);

      const link = document.createElement('a');
      link.setAttribute('href', '/en/manage/employees');
      document.body.appendChild(link);

      act(() => {
        link.dispatchEvent(
          new MouseEvent('click', { bubbles: true, cancelable: true, button: 0, metaKey: true })
        );
      });

      document.body.removeChild(link);
      expect(getByTestId('ReviewChanges')).toHaveAttribute('data-open', 'false');
    });

    it('should not intercept clicks with ctrlKey pressed (Ctrl+Click)', () => {
      const { getByTestId } = render(<ReviewChanges />);

      const link = document.createElement('a');
      link.setAttribute('href', '/en/manage/employees');
      document.body.appendChild(link);

      act(() => {
        link.dispatchEvent(
          new MouseEvent('click', { bubbles: true, cancelable: true, button: 0, ctrlKey: true })
        );
      });

      document.body.removeChild(link);
      expect(getByTestId('ReviewChanges')).toHaveAttribute('data-open', 'false');
    });

    it('should not intercept clicks with shiftKey pressed (Shift+Click)', () => {
      const { getByTestId } = render(<ReviewChanges />);

      const link = document.createElement('a');
      link.setAttribute('href', '/en/manage/employees');
      document.body.appendChild(link);

      act(() => {
        link.dispatchEvent(
          new MouseEvent('click', { bubbles: true, cancelable: true, button: 0, shiftKey: true })
        );
      });

      document.body.removeChild(link);
      expect(getByTestId('ReviewChanges')).toHaveAttribute('data-open', 'false');
    });

    it('should not intercept clicks with altKey pressed (Alt+Click)', () => {
      const { getByTestId } = render(<ReviewChanges />);

      const link = document.createElement('a');
      link.setAttribute('href', '/en/manage/employees');
      document.body.appendChild(link);

      act(() => {
        link.dispatchEvent(
          new MouseEvent('click', { bubbles: true, cancelable: true, button: 0, altKey: true })
        );
      });

      document.body.removeChild(link);
      expect(getByTestId('ReviewChanges')).toHaveAttribute('data-open', 'false');
    });

    it('should not intercept right-clicks (button=2)', () => {
      const { getByTestId } = render(<ReviewChanges />);

      const link = document.createElement('a');
      link.setAttribute('href', '/en/manage/employees');
      document.body.appendChild(link);

      act(() => {
        link.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, button: 2 }));
      });

      document.body.removeChild(link);
      expect(getByTestId('ReviewChanges')).toHaveAttribute('data-open', 'false');
    });

    it('should not intercept middle-clicks (button=1)', () => {
      const { getByTestId } = render(<ReviewChanges />);

      const link = document.createElement('a');
      link.setAttribute('href', '/en/manage/employees');
      document.body.appendChild(link);

      act(() => {
        link.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, button: 1 }));
      });

      document.body.removeChild(link);
      expect(getByTestId('ReviewChanges')).toHaveAttribute('data-open', 'false');
    });

    it('should not intercept clicks on non-link elements', () => {
      const { getByTestId } = render(<ReviewChanges />);

      const button = document.createElement('button');
      document.body.appendChild(button);

      act(() => {
        button.dispatchEvent(
          new MouseEvent('click', { bubbles: true, cancelable: true, button: 0 })
        );
      });

      document.body.removeChild(button);
      expect(getByTestId('ReviewChanges')).toHaveAttribute('data-open', 'false');
    });

    it('should not intercept when navigationGuardEnabled is false', () => {
      const { getByTestId } = render(<ReviewChanges navigationGuardEnabled={false} />);

      act(() => {
        clickLink('/en/manage/employees');
      });

      expect(getByTestId('ReviewChanges')).toHaveAttribute('data-open', 'false');
    });

    it('should not intercept when isOpen is true (manual mode active)', () => {
      render(<ReviewChanges isOpen={true} />);

      act(() => {
        clickLink('/en/manage/employees');
      });

      expect(mockPush).not.toHaveBeenCalled();
    });
  });

  describe('External Navigation Handling', () => {
    it('should intercept external navigation when alwaysShowOnExternalNavigation is true', () => {
      const { getByTestId } = render(<ReviewChanges alwaysShowOnExternalNavigation={true} />);

      act(() => {
        clickLink('/external/path');
      });

      expect(getByTestId('ReviewChanges')).toHaveAttribute('data-open', 'true');
    });

    it('should not intercept /business-pay/apply/ navigation when alwaysShowOnExternalNavigation is true', () => {
      const { getByTestId } = render(<ReviewChanges alwaysShowOnExternalNavigation={true} />);

      act(() => {
        clickLink('/business-pay/apply/some-page');
      });

      expect(getByTestId('ReviewChanges')).toHaveAttribute('data-open', 'false');
    });
  });

  describe('Edge Cases', () => {
    it('should handle undefined callback props gracefully', () => {
      const { getByTestId } = render(<ReviewChanges />);

      act(() => {
        clickLink('/en/manage/employees');
      });

      expect(() => {
        fireEvent.click(getByTestId('discard-btn'));
        fireEvent.click(getByTestId('continue-btn'));
        fireEvent.click(getByTestId('close-btn'));
      }).not.toThrow();
    });

    it('should handle mixed manual and navigation guard props', () => {
      const { getByTestId } = render(
        <ReviewChanges
          isOpen={true}
          onDiscard={mockOnDiscard}
          onContinue={mockOnContinue}
          alwaysShowOnExternalNavigation={true}
        />
      );

      const modal = getByTestId('ReviewChanges');
      expect(modal).toHaveAttribute('data-open', 'true');

      fireEvent.click(getByTestId('discard-btn'));
      expect(mockOnDiscard).toHaveBeenCalledTimes(1);
      expect(mockPush).not.toHaveBeenCalled();
    });
  });
});
