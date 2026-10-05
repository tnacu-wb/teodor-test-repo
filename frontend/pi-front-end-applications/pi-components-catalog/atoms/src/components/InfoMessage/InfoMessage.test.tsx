import { render } from '../../utils/test-utils';
import InfoMessage from './InfoMessage.component';

describe('Info Message', () => {
  it('should display the info message', () => {
    const { getByText } = render(<InfoMessage infoMessage="Notification message" />);
    expect(getByText('Notification message')).toBeInTheDocument();
  });

  describe('Accessibility', () => {
    describe('Error variant (default)', () => {
      it('should have role="alert" for error messages', () => {
        const { getByRole } = render(<InfoMessage infoMessage="Error message" />);
        const alert = getByRole('alert');
        expect(alert).toBeInTheDocument();
        expect(alert).toHaveTextContent('Error message');
      });

      it('should have aria-live="polite" for non-interruptive announcements', () => {
        const { getByRole } = render(<InfoMessage infoMessage="Error message" />);
        const alert = getByRole('alert');
        expect(alert).toHaveAttribute('aria-live', 'polite');
      });

      it('should have role="alert" when variant is explicitly "Error"', () => {
        const { getByRole } = render(<InfoMessage infoMessage="Error message" variant="Error" />);
        const alert = getByRole('alert');
        expect(alert).toHaveAttribute('aria-live', 'polite');
      });
    });

    describe('Info variant', () => {
      it('should have role="status" for informational messages', () => {
        const { getByRole } = render(<InfoMessage infoMessage="Info message" variant="Info" />);
        const status = getByRole('status');
        expect(status).toBeInTheDocument();
        expect(status).toHaveTextContent('Info message');
      });

      it('should not have explicit aria-live attribute (implicit polite from role="status")', () => {
        const { getByRole } = render(<InfoMessage infoMessage="Info message" variant="Info" />);
        const status = getByRole('status');
        expect(status).not.toHaveAttribute('aria-live');
      });
    });

    it('should render with messageId when provided', () => {
      const { getByRole } = render(
        <InfoMessage infoMessage="Error message" messageId="test-error-id" />
      );
      const alert = getByRole('alert');
      expect(alert).toHaveAttribute('id', 'test-error-id');
    });

    it('should not have id attribute when messageId is not provided', () => {
      const { getByRole } = render(<InfoMessage infoMessage="Error message" />);
      const alert = getByRole('alert');
      expect(alert).not.toHaveAttribute('id');
    });
  });
});
