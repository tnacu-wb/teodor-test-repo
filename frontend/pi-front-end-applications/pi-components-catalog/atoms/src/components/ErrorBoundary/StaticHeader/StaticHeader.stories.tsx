import { Meta, StoryObj } from '@storybook/react';

import StaticHeader from './StaticHeader.component';

const meta: Meta<typeof StaticHeader> = {
  title: 'StaticHeader',
  component: StaticHeader,
  argTypes: {},
  parameters: {
    docs: {
      description: {
        component:
          'Static header component displayed at the top of error boundary pages. Shows the Premier Inn logo with responsive sizing.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {};
