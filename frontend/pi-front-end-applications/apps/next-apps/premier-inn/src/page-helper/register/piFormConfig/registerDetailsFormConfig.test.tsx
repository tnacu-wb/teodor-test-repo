import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { registerDetailsFormConfig } from './registerDetailsFormConfig';

const mockT = (key: string) => key;
const mockGetFormState = jest.fn();
const mockOnSubmit = jest.fn();
const mockSetIsLocationRequired = jest.fn();

const defaultProps = {
  getFormState: mockGetFormState,
  defaultValues: {},
  onSubmit: mockOnSubmit,
  baseDataTestId: 'RegisterTest',
  t: mockT,
  currentLang: 'en',
  resetForm: 0,
  clearPhoneFields: false,
  isLocationRequired: false,
  setIsLocationRequired: mockSetIsLocationRequired,
  bkngData: undefined,
  registerIsError: false,
  isCompanyNameAdvanceEnabled: false,
  isCountrySelectorFilterableEnabled: false,
};

describe('registerDetailsFormConfig - ManualAddressToggle', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('Keyboard Accessibility', () => {
    it('should render manual address link as a button element', () => {
      const config = registerDetailsFormConfig(defaultProps);
      const manualAddressToggle = config.elements.fields.find(
        (field: any) => field.name === 'manualAddressToggle'
      );

      expect(manualAddressToggle).toBeDefined();
      expect(manualAddressToggle!.Component).toBeDefined();

      const MockComponent = manualAddressToggle!.Component as any;
      const mockField = { value: '', name: 'manualAddressToggle' };
      const mockHandleSetValue = jest.fn();

      const { container } = render(
        <MockComponent field={mockField} handleSetValue={mockHandleSetValue} errors={undefined} />
      );

      const button = container.querySelector('button');
      expect(button).toBeInTheDocument();
      expect(button).toHaveAttribute('type', 'button');
    });

    it('should render link with proper text content', () => {
      const config = registerDetailsFormConfig(defaultProps);
      const manualAddressToggle = config.elements.fields.find(
        (field: any) => field.name === 'manualAddressToggle'
      );

      const MockComponent = manualAddressToggle!.Component as any;
      const mockField = { value: '', name: 'manualAddressToggle' };
      const mockHandleSetValue = jest.fn();

      const { container } = render(
        <MockComponent field={mockField} handleSetValue={mockHandleSetValue} errors={undefined} />
      );

      const button = container.querySelector('button');
      expect(button).toHaveTextContent('account.register.enterManualAddress');
    });

    it('should be keyboard focusable and activatable with Enter key', async () => {
      const user = userEvent.setup();
      const config = registerDetailsFormConfig(defaultProps);
      const manualAddressToggle = config.elements.fields.find(
        (field: any) => field.name === 'manualAddressToggle'
      );

      const MockComponent = manualAddressToggle!.Component as any;
      const mockField = { value: '', name: 'manualAddressToggle' };
      const mockHandleSetValue = jest.fn();

      render(
        <MockComponent field={mockField} handleSetValue={mockHandleSetValue} errors={undefined} />
      );

      const button = screen.getByRole('button', {
        name: 'account.register.enterManualAddress',
      });

      await user.tab();
      expect(button).toHaveFocus();

      await user.keyboard('{Enter}');

      await waitFor(() => {
        expect(mockHandleSetValue).toHaveBeenCalledWith('manualAddressToggle', 'manualAddress');
      });
    });

    it('should be activatable with Space key', async () => {
      const user = userEvent.setup();
      const config = registerDetailsFormConfig(defaultProps);
      const manualAddressToggle = config.elements.fields.find(
        (field: any) => field.name === 'manualAddressToggle'
      );

      const MockComponent = manualAddressToggle!.Component as any;
      const mockField = { value: '', name: 'manualAddressToggle' };
      const mockHandleSetValue = jest.fn();

      render(
        <MockComponent field={mockField} handleSetValue={mockHandleSetValue} errors={undefined} />
      );

      const button = screen.getByRole('button', {
        name: 'account.register.enterManualAddress',
      });

      button.focus();
      await user.keyboard(' ');

      await waitFor(() => {
        expect(mockHandleSetValue).toHaveBeenCalledWith('manualAddressToggle', 'manualAddress');
      });
    });

    it('should call handleSetValue when clicked', async () => {
      const user = userEvent.setup();
      const config = registerDetailsFormConfig(defaultProps);
      const manualAddressToggle = config.elements.fields.find(
        (field: any) => field.name === 'manualAddressToggle'
      );

      const MockComponent = manualAddressToggle!.Component as any;
      const mockField = { value: '', name: 'manualAddressToggle' };
      const mockHandleSetValue = jest.fn();

      render(
        <MockComponent field={mockField} handleSetValue={mockHandleSetValue} errors={undefined} />
      );

      const button = screen.getByRole('button', {
        name: 'account.register.enterManualAddress',
      });

      await user.click(button);

      await waitFor(() => {
        expect(mockHandleSetValue).toHaveBeenCalledTimes(1);
        expect(mockHandleSetValue).toHaveBeenCalledWith('manualAddressToggle', 'manualAddress');
      });
    });

    it('should not render when field value is manualAddress', () => {
      const config = registerDetailsFormConfig(defaultProps);
      const manualAddressToggle = config.elements.fields.find(
        (field: any) => field.name === 'manualAddressToggle'
      );

      const MockComponent = manualAddressToggle!.Component as any;
      const mockField = { value: 'manualAddress', name: 'manualAddressToggle' };
      const mockHandleSetValue = jest.fn();

      const { container } = render(
        <MockComponent field={mockField} handleSetValue={mockHandleSetValue} errors={undefined} />
      );

      expect(container.firstChild).toBeNull();
    });

    it('should handle click gracefully when handleSetValue is undefined', async () => {
      const user = userEvent.setup();
      const config = registerDetailsFormConfig(defaultProps);
      const manualAddressToggle = config.elements.fields.find(
        (field: any) => field.name === 'manualAddressToggle'
      );

      const MockComponent = manualAddressToggle!.Component as any;
      const mockField = { value: '', name: 'manualAddressToggle' };

      render(<MockComponent field={mockField} handleSetValue={undefined} errors={undefined} />);

      const button = screen.getByRole('button', {
        name: 'account.register.enterManualAddress',
      });

      await user.click(button);

      await waitFor(() => {
        expect(button).toBeInTheDocument();
      });
    });

    it('should render for German language when appropriate toggle is used', () => {
      const config = registerDetailsFormConfig({
        ...defaultProps,
        currentLang: 'de',
      });

      const manualAddressToggleDE = config.elements.fields.find(
        (field: any) => field.name === 'manualAddressToggle-de'
      );

      expect(manualAddressToggleDE).toBeDefined();
      expect(manualAddressToggleDE!.hidden).toBe(false);

      const manualAddressToggle = config.elements.fields.find(
        (field: any) => field.name === 'manualAddressToggle'
      );

      expect(manualAddressToggle!.hidden).toBe(true);
    });

    it('should render for English language with correct toggle', () => {
      const config = registerDetailsFormConfig({
        ...defaultProps,
        currentLang: 'en',
      });

      const manualAddressToggle = config.elements.fields.find(
        (field: any) => field.name === 'manualAddressToggle'
      );

      expect(manualAddressToggle).toBeDefined();
      expect(manualAddressToggle!.hidden).toBe(false);

      const manualAddressToggleDE = config.elements.fields.find(
        (field: any) => field.name === 'manualAddressToggle-de'
      );

      expect(manualAddressToggleDE!.hidden).toBe(true);
    });
  });
});
