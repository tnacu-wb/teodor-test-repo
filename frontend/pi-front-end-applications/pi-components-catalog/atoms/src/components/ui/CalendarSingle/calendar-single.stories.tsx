import type { Meta, StoryObj } from '@storybook/react';
import { useState } from 'react';

import { CalendarSingle } from './calendar-single';

const meta: Meta<typeof CalendarSingle> = {
  title: 'Shadcn/CalendarSingle',
  component: CalendarSingle,
  argTypes: {
    fromMonth: {
      description: 'Earliest month available for navigation.',
      control: 'date',
    },
    toMonth: {
      description: 'Latest month available for navigation.',
      control: 'date',
    },
    locale: {
      description: 'Locale key for month/day formatting.',
      control: 'text',
    },
    disabled: {
      description: 'Disabled date matcher(s).',
      control: false,
    },
    onDayKeyPress: {
      description: 'Keyboard handler for day interactions.',
      control: false,
    },
    selected: {
      description: 'Currently selected day.',
      control: false,
    },
    onDayClick: {
      description: 'Callback when user selects a day.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Single-date calendar picker primitive used in one-day travel flows.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

const SingleCalendar = () => {
  const [day, setDay] = useState<Date | undefined>(new Date());

  return (
    <CalendarSingle
      mode="single"
      fromMonth={new Date()}
      toMonth={new Date(new Date().getFullYear(), new Date().getMonth() + 6, 1)}
      selected={day}
      onDayClick={setDay}
      onDayKeyPress={() => undefined}
    />
  );
};

export const Default: Story = {
  render: () => <SingleCalendar />,
};
