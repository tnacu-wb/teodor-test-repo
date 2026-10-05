import { FormControl, FormErrorMessage, Input as ChakraInput } from '@chakra-ui/react';
import React, { useCallback, useLayoutEffect, useRef } from 'react';

import { errorMessageStyle } from './DatatransSecureFieldsForm.styles';
import { expiryInputErrorStyle, expiryInputStyle } from './ExpiryInput.styles';

export interface ExpiryInputProps {
  value: string;
  onChange: (value: string) => void;
  onBlur?: () => void;
  error?: string;
  id?: string;
  'data-testid'?: string;
}

// The separator shown between month and year, with a space on each side.
const SEPARATOR = ' / ';
const SEPARATOR_LENGTH = SEPARATOR.length;

/**
 * Single masked text input for card expiry date displayed as "MM / YY".
 *
 * Behaviour:
 * - Accepts digits only; all other characters are stripped on input.
 * - Auto-inserts " / " after the second digit so typing "0327" produces "03 / 27".
 * - Backspace anywhere inside or adjacent to the " / " separator strips the whole
 *   separator in one keystroke (rather than deleting one raw character at a time
 *   from the controlled string, which could leave the mask in a broken state).
 * - Caret position is restored after every reformat so the cursor does not
 *   unexpectedly jump to the end of the input while editing.
 * - Maximum 7 visible characters: "MM / YY".
 *
 * Accessibility:
 * - inputMode="numeric" opens the numeric keyboard on mobile.
 * - autoComplete="cc-exp" + name="cc-exp" lets password managers / browser
 *   autofill populate the field.
 * - aria-label and the surrounding FormControl provide accessible labelling.
 */
export function ExpiryInput({
  value,
  onChange,
  onBlur,
  error,
  id = 'expiry',
  'data-testid': testId = 'DatatransSecureFieldsForm-Expiry',
}: ExpiryInputProps) {
  // Ref to the underlying <input> DOM node so we can restore caret position.
  const inputRef = useRef<HTMLInputElement>(null);

  // Desired caret position after the next render — set by handleChange and
  // consumed by the useLayoutEffect below.
  const caretPositionRef = useRef<number | null>(null);

  // After React commits the new controlled value, move the caret to where the
  // user actually was. useLayoutEffect runs synchronously after the DOM update
  // but before the browser paints, so there is no visible cursor jump.
  useLayoutEffect(() => {
    const pos = caretPositionRef.current;
    if (pos !== null && inputRef.current) {
      inputRef.current.setSelectionRange(pos, pos);
      caretPositionRef.current = null;
    }
  });

  const handleKeyDown = useCallback(
    (e: React.KeyboardEvent<HTMLInputElement>) => {
      if (e.key !== 'Backspace') return;

      const el = e.currentTarget;
      const caret = el.selectionStart ?? value.length;

      // Find whether the caret is at any position within or immediately after
      // the separator.  "Immediately after" covers the most common case where
      // the user has just finished typing the month and the separator was
      // auto-inserted.
      //
      // Separator occupies [sepStart, sepStart + SEPARATOR_LENGTH).
      const sepStart = value.indexOf(SEPARATOR);
      if (sepStart === -1) return;
      const sepEnd = sepStart + SEPARATOR_LENGTH;

      // Caret is inside or just past the separator: remove the whole separator
      // so the user drops back to month digit 2 in one keypress.
      if (caret > sepStart && caret <= sepEnd) {
        e.preventDefault();
        const next = value.slice(0, sepStart) + value.slice(sepEnd);
        caretPositionRef.current = sepStart;
        onChange(next);
      }
      // Otherwise let the browser handle the backspace normally; handleChange
      // will re-derive the clean formatted string from whatever the browser
      // produces (stripping non-digits and reformatting).
    },
    [value, onChange]
  );

  const handleChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const raw = e.target.value;
      // Record caret position from the native event BEFORE React re-renders.
      const rawCaret = e.target.selectionStart ?? raw.length;

      // Strip everything that isn't a digit.
      const digits = raw.replace(/\D/g, '');

      // Cap at 4 digits (MMYY).
      const capped = digits.slice(0, 4);

      // Insert " / " between month and year once we have at least 2 digits.
      const formatted =
        capped.length >= 2 ? `${capped.slice(0, 2)}${SEPARATOR}${capped.slice(2)}` : capped;

      // ── Caret mapping ──────────────────────────────────────────────────────
      // We need to map the caret position in `raw` (what the browser reported)
      // to the equivalent position in `formatted`.
      //
      // Strategy: count how many digit characters appear before the raw caret,
      // then find the position in the formatted string after that many digits.
      let digitsBeforeCaret = 0;
      for (let i = 0; i < Math.min(rawCaret, raw.length); i++) {
        if (/\d/.test(raw[i])) digitsBeforeCaret++;
      }

      let mappedCaret = 0;
      let digitsSeen = 0;
      for (let i = 0; i < formatted.length; i++) {
        if (digitsSeen === digitsBeforeCaret) {
          mappedCaret = i;
          break;
        }
        if (/\d/.test(formatted[i])) digitsSeen++;
        // If we exhaust the string before finding the target, fall through to
        // the assignment after the loop.
        if (i === formatted.length - 1) {
          mappedCaret = formatted.length;
        }
      }

      // If the formatted string is shorter or equal to 2 chars (no separator
      // yet), the caret simply sits at the end of the digit run.
      if (formatted.length <= 2) {
        mappedCaret = formatted.length;
      }

      // When the mapped caret lands inside the separator region, push it to
      // the end of the separator so typing continues at the year position.
      const sepStart = formatted.indexOf(SEPARATOR);
      if (sepStart !== -1) {
        const sepEnd = sepStart + SEPARATOR_LENGTH;
        if (mappedCaret > sepStart && mappedCaret < sepEnd) {
          mappedCaret = sepEnd;
        }
      }

      caretPositionRef.current = mappedCaret;
      onChange(formatted);
    },
    [onChange]
  );

  return (
    <FormControl isInvalid={!!error} position="relative" zIndex={0}>
      <ChakraInput
        ref={inputRef}
        id={id}
        name="cc-exp"
        value={value}
        onChange={handleChange}
        onKeyDown={handleKeyDown}
        onBlur={onBlur}
        placeholder="Expiry date"
        inputMode="numeric"
        autoComplete="cc-exp"
        aria-label="Expiry date"
        data-testid={testId}
        isInvalid={!!error}
        {...(error ? expiryInputErrorStyle : expiryInputStyle)}
      />

      {error && <FormErrorMessage {...errorMessageStyle}>{error}</FormErrorMessage>}
    </FormControl>
  );
}
