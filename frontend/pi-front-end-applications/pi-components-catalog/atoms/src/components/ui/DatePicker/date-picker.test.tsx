import { fireEvent, render } from '@testing-library/react';
import { enGB } from 'date-fns/locale';
import { act } from 'react-dom/test-utils';

import { Calendar } from '../Calendar';
import { createCustomLocale } from './createCustomLocale';
import { DatePickerWithRange } from './date-picker';

jest.mock('next/image', () => ({
  __esModule: true,
  default: () => null,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useTranslation: () => ({ t: (key: string) => key }),
  useElementDimensions: () => ({ height: 0, width: 0 }),
}));

const months = [
  'January',
  'February',
  'March',
  'April',
  'May',
  'June',
  'July',
  'August',
  'September',
  'October',
  'November',
  'December',
];
const weekdaysShort = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
const fullWeekdays = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

const formLabels = {
  calendarIcon: 'icon.png',
  datePicker: { months, weekdaysShort },
} as never;

const icons: Record<string, string> = {
  'icon.chevron.left': 'l.png',
  'icon.chevron.right': 'r.png',
  'icon.arrow.right': 'a.png',
  'icon.notification.error': 'e.png',
};

describe('DatePicker weekday header accessibility', () => {
  it('exposes the full weekday name as the column-header aria-label while keeping the short visible label', () => {
    const { container } = render(
      <Calendar
        mode="range"
        selected={undefined}
        onDayClick={() => undefined}
        month={new Date(2025, 11, 1)}
        locale={createCustomLocale(enGB, months, weekdaysShort)}
      />
    );

    const headers = Array.from(container.querySelectorAll('th[scope="col"]'));
    expect(headers.map((th) => th.getAttribute('aria-label'))).toEqual(fullWeekdays);
    expect(headers.map((th) => th.textContent)).toEqual(weekdaysShort);
  });

  it('announces full weekday names in the mobile dialog via sr-only text without adding focusable controls', async () => {
    const { getByTestId } = render(
      <DatePickerWithRange
        className={{} as never}
        locale="en"
        formLabels={formLabels}
        icons={icons}
        mobile
        onDateChange={() => undefined}
        dateFromUrl={undefined}
        showError={false}
        setShowError={() => undefined}
        onOpenChange={() => undefined}
      />
    );

    await act(async () => {
      fireEvent.click(getByTestId('IB-Date-Picker-Input'));
    });

    expect(getByTestId('DatePicker-Dialog-Content')).toBeInTheDocument();

    const srOnlyText = Array.from(document.querySelectorAll('.sr-only')).map(
      (el) => el.textContent
    );
    const ariaHiddenText = Array.from(document.querySelectorAll('[aria-hidden="true"]')).map(
      (el) => el.textContent
    );
    const buttonText = Array.from(document.querySelectorAll('button')).map((b) =>
      b.textContent?.trim()
    );

    fullWeekdays.forEach((full) => expect(srOnlyText).toContain(full));
    weekdaysShort.forEach((short) => {
      expect(ariaHiddenText).toContain(short);

      expect(buttonText).not.toContain(short);
    });
  });
});
