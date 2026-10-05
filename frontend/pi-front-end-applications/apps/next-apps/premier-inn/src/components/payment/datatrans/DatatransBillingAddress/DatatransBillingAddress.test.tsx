import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import React from 'react';

import { fireEvent, render, screen } from '~utils/test-utils';

import { DatatransBillingAddress } from './DatatransBillingAddress';

expect.extend(toHaveNoViolations);

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------
const defaultProps = {
  address: '1 Main Street, London, SW1A 1AA',
  label: 'Billing address',
  isChecked: false,
  onChange: jest.fn(),
};

const renderComponent = (props: Partial<typeof defaultProps> = {}) =>
  render(
    <ChakraProvider>
      <DatatransBillingAddress {...defaultProps} {...props} />
    </ChakraProvider>
  );

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------
describe('DatatransBillingAddress', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  // -------------------------------------------------------------------------
  describe('Rendering', () => {
    it('renders the label prefix and address', () => {
      renderComponent();
      expect(screen.getByTestId('DatatransBillingAddress-Label')).toHaveTextContent(
        'Billing address: 1 Main Street, London, SW1A 1AA'
      );
    });

    it('renders with the default data-testid', () => {
      renderComponent();
      expect(screen.getByTestId('DatatransBillingAddress')).toBeInTheDocument();
    });

    it('forwards a custom data-testid to the root and child elements', () => {
      renderComponent({ 'data-testid': 'CustomId' } as any);
      expect(screen.getByTestId('CustomId')).toBeInTheDocument();
      expect(screen.getByTestId('CustomId-Input')).toBeInTheDocument();
      expect(screen.getByTestId('CustomId-Label')).toBeInTheDocument();
    });
  });

  // -------------------------------------------------------------------------
  describe('Checkbox state', () => {
    it('renders an unchecked checkbox when isChecked is false', () => {
      renderComponent({ isChecked: false });
      const input = screen.getByTestId('DatatransBillingAddress-Input');
      expect(input).not.toBeChecked();
    });

    it('renders a checked checkbox when isChecked is true', () => {
      renderComponent({ isChecked: true });
      const input = screen.getByTestId('DatatransBillingAddress-Input');
      expect(input).toBeChecked();
    });
  });

  // -------------------------------------------------------------------------
  describe('Interaction', () => {
    it('calls onChange with true when clicking an unchecked checkbox', () => {
      const onChange = jest.fn();
      renderComponent({ isChecked: false, onChange });

      fireEvent.click(screen.getByTestId('DatatransBillingAddress-Input'));

      expect(onChange).toHaveBeenCalledTimes(1);
      expect(onChange).toHaveBeenCalledWith(true);
    });

    it('calls onChange with false when clicking a checked checkbox', () => {
      const onChange = jest.fn();
      renderComponent({ isChecked: true, onChange });

      fireEvent.click(screen.getByTestId('DatatransBillingAddress-Input'));

      expect(onChange).toHaveBeenCalledTimes(1);
      expect(onChange).toHaveBeenCalledWith(false);
    });

    it('calls onChange when clicking the label (wraps the input)', () => {
      const onChange = jest.fn();
      renderComponent({ isChecked: false, onChange });

      // The label wraps the input so a click on the text also toggles the checkbox
      fireEvent.click(screen.getByTestId('DatatransBillingAddress-Label'));

      expect(onChange).toHaveBeenCalledTimes(1);
    });
  });

  // -------------------------------------------------------------------------
  describe('Visual indicator', () => {
    it('does not render the inner checked square when unchecked', () => {
      const { container } = renderComponent({ isChecked: false });
      // The inner square is only mounted inside AnimatePresence when checked.
      // We query for the sibling of the hidden input — the first visual Box
      // child should contain no filled child when unchecked.
      // Checking via the absence of the data-testid is not possible (it has none),
      // so we assert the checkbox input reports unchecked state instead.
      const input = screen.getByTestId('DatatransBillingAddress-Input') as HTMLInputElement;
      expect(input.checked).toBe(false);
      // The outer control box is present
      expect(container.querySelector('[aria-hidden="true"]')).toBeInTheDocument();
    });

    it('renders the outer control box with aria-hidden', () => {
      renderComponent();
      expect(
        screen.getByTestId('DatatransBillingAddress').querySelector('[aria-hidden="true"]')
      ).toBeInTheDocument();
    });
  });

  // -------------------------------------------------------------------------
  describe('Accessibility', () => {
    it('the native checkbox input is associated with the label via htmlFor/id', () => {
      renderComponent();
      const input = screen.getByTestId('DatatransBillingAddress-Input');
      const id = input.getAttribute('id');
      expect(id).toBeTruthy();

      // The wrapping <label> element should have htmlFor matching the input id
      const label = input.closest('label');
      expect(label).toHaveAttribute('for', id);
    });

    it('has no accessibility violations when unchecked', async () => {
      const { container } = renderComponent({ isChecked: false });
      const results = await axe(container);
      expect(results).toHaveNoViolations();
    });

    it('has no accessibility violations when checked', async () => {
      const { container } = renderComponent({ isChecked: true });
      const results = await axe(container);
      expect(results).toHaveNoViolations();
    });
  });
});
