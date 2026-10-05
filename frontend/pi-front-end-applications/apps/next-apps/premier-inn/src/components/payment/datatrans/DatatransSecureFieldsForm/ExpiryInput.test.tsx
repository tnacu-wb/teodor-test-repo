import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import { useState } from 'react';

import { fireEvent, render, screen, userEvent } from '~utils/test-utils';

import { ExpiryInput, ExpiryInputProps } from './ExpiryInput';

expect.extend(toHaveNoViolations);

// ---------------------------------------------------------------------------
// Controlled wrapper — lets us test the component the way it is actually used
// (value + onChange round-trip), without coupling to a parent form.
// ---------------------------------------------------------------------------
function ControlledExpiryInput(
  props: Omit<ExpiryInputProps, 'value' | 'onChange'> & { initial?: string }
) {
  const [value, setValue] = useState(props.initial ?? '');
  return (
    <ChakraProvider>
      <ExpiryInput {...props} value={value} onChange={setValue} />
    </ChakraProvider>
  );
}

// Convenience: renders the controlled wrapper and returns the <input> element.
function setup(props: Omit<ExpiryInputProps, 'value' | 'onChange'> & { initial?: string } = {}) {
  render(<ControlledExpiryInput {...props} />);
  return screen.getByTestId(props['data-testid'] ?? 'DatatransSecureFieldsForm-Expiry');
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------
describe('ExpiryInput', () => {
  // ── Rendering ─────────────────────────────────────────────────────────────
  describe('Rendering', () => {
    it('renders the input with accessible label', () => {
      setup();
      expect(screen.getByRole('textbox', { name: /expiry date/i })).toBeInTheDocument();
    });

    it('renders with the default test-id', () => {
      setup();
      expect(screen.getByTestId('DatatransSecureFieldsForm-Expiry')).toBeInTheDocument();
    });

    it('renders with a custom test-id', () => {
      setup({ 'data-testid': 'custom-expiry' });
      expect(screen.getByTestId('custom-expiry')).toBeInTheDocument();
    });

    it('renders with a custom id', () => {
      setup({ id: 'my-expiry' });
      expect(document.getElementById('my-expiry')).toBeInTheDocument();
    });

    it('has name="cc-exp" for autofill', () => {
      const input = setup();
      expect(input).toHaveAttribute('name', 'cc-exp');
    });

    it('has autoComplete="cc-exp"', () => {
      const input = setup();
      expect(input).toHaveAttribute('autocomplete', 'cc-exp');
    });

    it('has inputMode="numeric"', () => {
      const input = setup();
      expect(input).toHaveAttribute('inputmode', 'numeric');
    });

    it('renders no error message by default', () => {
      setup();
      expect(screen.queryByRole('alert')).not.toBeInTheDocument();
    });

    it('renders the error message when error prop is supplied', () => {
      setup({ error: 'Your card has expired' });
      expect(screen.getByText('Your card has expired')).toBeInTheDocument();
    });

    it('does not render an error message when error prop is undefined', () => {
      setup({ error: undefined });
      expect(screen.queryByText(/expired/i)).not.toBeInTheDocument();
    });
  });

  // ── Forward typing / masking ───────────────────────────────────────────────
  describe('Forward typing masking', () => {
    it('accepts a single digit without formatting', () => {
      const input = setup();
      fireEvent.change(input, { target: { value: '0' } });
      expect(input).toHaveValue('0');
    });

    it('auto-inserts separator immediately after two month digits', () => {
      const input = setup();
      fireEvent.change(input, { target: { value: '03' } });
      // The mask inserts " / " as soon as there are exactly 2 digits so the user
      // sees "03 / " and can continue typing the year without extra keypresses.
      expect(input).toHaveValue('03 / ');
    });

    it('auto-inserts separator after two month digits', () => {
      const input = setup();
      fireEvent.change(input, { target: { value: '032' } });
      expect(input).toHaveValue('03 / 2');
    });

    it('formats four digits as MM / YY', () => {
      const input = setup();
      fireEvent.change(input, { target: { value: '0327' } });
      expect(input).toHaveValue('03 / 27');
    });

    it('caps at seven visible characters (MM / YY)', () => {
      const input = setup();
      fireEvent.change(input, { target: { value: '032712' } });
      expect(input).toHaveValue('03 / 27');
    });

    it('strips non-digit characters on input', () => {
      const input = setup();
      fireEvent.change(input, { target: { value: 'ab0c3d2e7f' } });
      expect(input).toHaveValue('03 / 27');
    });

    it('strips letters typed alongside digits', () => {
      const input = setup();
      fireEvent.change(input, { target: { value: '0X3' } });
      // '0X3' strips to '03' (2 digits) → mask auto-inserts separator → '03 / '
      expect(input).toHaveValue('03 / ');
    });

    it('accepts pasted "03/27" and formats correctly', () => {
      const input = setup();
      fireEvent.change(input, { target: { value: '03/27' } });
      expect(input).toHaveValue('03 / 27');
    });

    it('accepts pasted "03-27" and formats correctly', () => {
      const input = setup();
      fireEvent.change(input, { target: { value: '03-27' } });
      expect(input).toHaveValue('03 / 27');
    });

    it('handles already-formatted value without double-inserting separator', () => {
      const input = setup({ initial: '03 / 27' });
      // Simulate the browser reporting the already-formatted value on focus
      fireEvent.change(input, { target: { value: '03 / 27' } });
      expect(input).toHaveValue('03 / 27');
    });
  });

  // ── Backspace key handling ─────────────────────────────────────────────────
  describe('Backspace key handling', () => {
    // jsdom does not advance the controlled value automatically on keydown, so
    // we test handleKeyDown's preventDefault + onChange path by checking whether
    // onChange was called with the stripped value when the caret sits at each
    // position inside the separator window.

    // Helper: fire a keydown at a given caret position and return the resulting
    // input value after the component re-renders.
    async function backspaceAtCaret(initial: string, caretPos: number) {
      const input = setup({ initial });
      // Place the caret at the requested position.
      (input as HTMLInputElement).setSelectionRange(caretPos, caretPos);

      await userEvent.type(input, '{backspace}', {
        initialSelectionStart: caretPos,
        initialSelectionEnd: caretPos,
      });
      return (input as HTMLInputElement).value;
    }

    it('strips the whole separator when caret is immediately after it (pos 7)', async () => {
      // "03 / 27" — caret just past the separator at pos 5 = "03 / " boundary
      const result = await backspaceAtCaret('03 / 27', 5);
      // After separator removal user is back at month position
      expect(result).not.toContain(' / ');
    });

    it('strips the whole separator when caret is inside it (pos 4)', async () => {
      const result = await backspaceAtCaret('03 / 27', 4);
      expect(result).not.toContain(' / ');
    });

    it('strips the whole separator when caret is at its start (pos 3)', async () => {
      // pos 3 is the first char of " / " — pressing backspace here should not
      // leave a dangling separator.
      const result = await backspaceAtCaret('03 / ', 3);
      // The separator should be gone; month digits may remain.
      expect(result).not.toMatch(/ \/ $/);
    });

    it('does not interfere with backspace before the separator', async () => {
      const result = await backspaceAtCaret('03 / 27', 2);
      // Deletes '3' from the month. Remaining digits are '0' + '2' + '7' from year,
      // which reformat to '02 / 7'. The separator is preserved because there are
      // still ≥2 digits after the deletion.
      expect(result).toBe('02 / 7');
    });

    it('does not interfere with backspace on a year digit', async () => {
      const result = await backspaceAtCaret('03 / 27', 7);
      // Deletes '7', leaving '03 / 2'
      expect(result).toBe('03 / 2');
    });

    it('handles backspace on a single digit without crashing', async () => {
      const result = await backspaceAtCaret('0', 1);
      expect(result).toBe('');
    });
  });

  // ── onBlur ─────────────────────────────────────────────────────────────────
  describe('onBlur callback', () => {
    it('calls onBlur when the input loses focus', () => {
      const onBlur = jest.fn();
      const input = setup({ onBlur });
      fireEvent.blur(input);
      expect(onBlur).toHaveBeenCalledTimes(1);
    });

    it('does not throw when onBlur is not supplied', () => {
      const input = setup();
      expect(() => fireEvent.blur(input)).not.toThrow();
    });
  });

  // ── Error state ────────────────────────────────────────────────────────────
  describe('Error state', () => {
    it('marks the input as invalid when error is supplied', () => {
      const input = setup({ error: 'Expiry date is required' });
      expect(input).toHaveAttribute('aria-invalid', 'true');
    });

    it('does not mark the input as invalid when there is no error', () => {
      const input = setup();
      expect(input).not.toHaveAttribute('aria-invalid', 'true');
    });

    it('updates the error message when the error prop changes', () => {
      const { rerender } = render(
        <ChakraProvider>
          <ExpiryInput value="" onChange={jest.fn()} error="First error" />
        </ChakraProvider>
      );
      expect(screen.getByText('First error')).toBeInTheDocument();

      rerender(
        <ChakraProvider>
          <ExpiryInput value="" onChange={jest.fn()} error="Second error" />
        </ChakraProvider>
      );
      expect(screen.queryByText('First error')).not.toBeInTheDocument();
      expect(screen.getByText('Second error')).toBeInTheDocument();
    });

    it('removes the error message when error prop is cleared', () => {
      const { rerender } = render(
        <ChakraProvider>
          <ExpiryInput value="" onChange={jest.fn()} error="Some error" />
        </ChakraProvider>
      );
      expect(screen.getByText('Some error')).toBeInTheDocument();

      rerender(
        <ChakraProvider>
          <ExpiryInput value="" onChange={jest.fn()} error={undefined} />
        </ChakraProvider>
      );
      expect(screen.queryByText('Some error')).not.toBeInTheDocument();
    });
  });

  // ── onChange contract ──────────────────────────────────────────────────────
  describe('onChange contract', () => {
    it('calls onChange with the formatted value', () => {
      const onChange = jest.fn();
      render(
        <ChakraProvider>
          <ExpiryInput value="" onChange={onChange} />
        </ChakraProvider>
      );
      const input = screen.getByTestId('DatatransSecureFieldsForm-Expiry');
      fireEvent.change(input, { target: { value: '0327' } });
      expect(onChange).toHaveBeenCalledWith('03 / 27');
    });

    it('calls onChange with an empty string when all digits are removed', () => {
      const onChange = jest.fn();
      render(
        <ChakraProvider>
          <ExpiryInput value="0" onChange={onChange} />
        </ChakraProvider>
      );
      const input = screen.getByTestId('DatatransSecureFieldsForm-Expiry');
      fireEvent.change(input, { target: { value: '' } });
      expect(onChange).toHaveBeenCalledWith('');
    });

    it('calls onChange only with digits — never with a raw separator string', () => {
      const onChange = jest.fn();
      render(
        <ChakraProvider>
          <ExpiryInput value="" onChange={onChange} />
        </ChakraProvider>
      );
      const input = screen.getByTestId('DatatransSecureFieldsForm-Expiry');
      // Simulate pasting something weird
      fireEvent.change(input, { target: { value: '///' } });
      expect(onChange).toHaveBeenCalledWith('');
    });
  });

  // ── Accessibility ──────────────────────────────────────────────────────────
  describe('Accessibility', () => {
    it('has no axe violations in the default state', async () => {
      const { container } = render(
        <ChakraProvider>
          <ExpiryInput value="" onChange={jest.fn()} />
        </ChakraProvider>
      );
      expect(await axe(container)).toHaveNoViolations();
    });

    it('has no axe violations in the error state', async () => {
      const { container } = render(
        <ChakraProvider>
          <ExpiryInput value="" onChange={jest.fn()} error="Your card has expired" />
        </ChakraProvider>
      );
      expect(await axe(container)).toHaveNoViolations();
    });

    it('has no axe violations when a value is present', async () => {
      const { container } = render(
        <ChakraProvider>
          <ExpiryInput value="03 / 27" onChange={jest.fn()} />
        </ChakraProvider>
      );
      expect(await axe(container)).toHaveNoViolations();
    });
  });
});
