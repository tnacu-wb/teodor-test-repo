import type { Meta, StoryObj } from '@storybook/react';

import { ChartConfig } from '../Chart';
import { FinancialChart } from './financial-chart';

const chartData = [
  { month: 1, cost: 120 },
  { month: 2, cost: 180 },
  { month: 3, cost: 160 },
  { month: 4, cost: 210 },
];

const chartConfig: ChartConfig = {
  cost: {
    label: 'Cost',
    color: 'var(--primaryColor)',
  },
};

const meta: Meta<typeof FinancialChart> = {
  title: 'Shadcn/FinancialChart',
  component: FinancialChart,
  argTypes: {
    chartData: {
      description: 'Input dataset rendered as bar chart rows.',
      control: false,
    },
    chartConfig: {
      description: 'Series config used by chart container and tooltip.',
      control: false,
    },
    axisDataKey: {
      description: 'Field key used for x-axis labels.',
      control: 'text',
    },
    barDataKey: {
      description: 'Field key used for bar values.',
      control: 'text',
    },
    showTooltip: {
      description: 'Force tooltip visible state.',
      control: 'boolean',
    },
    disabled: {
      description: 'Disable chart interactions and dim bars.',
      control: 'boolean',
    },
    onMouseEnter: {
      description: 'Mouse enter callback for bars.',
      control: false,
    },
    onMouseLeave: {
      description: 'Mouse leave callback for bars.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Pre-configured financial bar chart wrapper with optional tooltip interaction.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    chartData,
    chartConfig,
    axisDataKey: 'month',
    barDataKey: 'cost',
    showTooltip: false,
    disabled: false,
  },
};

export const Disabled: Story = {
  args: {
    ...Default.args,
    disabled: true,
  },
};
