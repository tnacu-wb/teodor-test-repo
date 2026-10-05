import type { Meta, StoryObj } from '@storybook/react';
import { useState } from 'react';
import type { DateRange } from 'react-day-picker';

import { Calendar } from './calendar';

const meta: Meta<typeof Calendar> = {
  title: 'Shadcn/Calendar',
  component: Calendar,
  argTypes: {
    mode: {
      description: 'Selection mode for day-picker.',
      options: ['single', 'multiple', 'range'],
      control: { type: 'radio' },
    },
    numberOfMonths: {
      description: 'Number of months rendered side-by-side.',
      control: { type: 'number' },
    },
    isCurrentMonth: {
      description: 'Hides previous month nav when current month is first month.',
      control: 'boolean',
    },
    shouldHideNextMonthIcon: {
      description: 'Hides next-month navigation icon.',
      control: 'boolean',
    },
    isCheckoutDay365: {
      description: 'Toggles custom 365-day checkout styling logic.',
      control: 'boolean',
    },
    hideHeader: {
      description: 'Hides weekday header row for compact displays.',
      control: 'boolean',
    },
    selected: {
      description: 'Current selected range/day.',
      control: false,
    },
    onSelect: {
      description: 'Selection callback.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Range-enabled calendar primitive with Business Booker date styling rules.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

const RangeCalendar = () => {
  const [selected, setSelected] = useState<DateRange | undefined>({
    from: new Date(),
    to: new Date(Date.now() + 3 * 24 * 60 * 60 * 1000),
  });

  return <Calendar mode="range" selected={selected} onSelect={setSelected} numberOfMonths={2} />;
};

export const Default: Story = {
  render: () => <RangeCalendar />,
};

export const HideHeader: Story = {
  render: () => (
    <Calendar
      mode="range"
      selected={{ from: new Date(), to: new Date(Date.now() + 2 * 24 * 60 * 60 * 1000) }}
      onSelect={() => undefined}
      hideHeader
    />
  ),
};
