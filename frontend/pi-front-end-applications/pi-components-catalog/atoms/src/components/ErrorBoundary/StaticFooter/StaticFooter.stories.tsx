import { Meta, StoryObj } from '@storybook/react';

import StaticFooter from './StaticFooter.component';

const meta: Meta<typeof StaticFooter> = {
  title: 'StaticFooter',
  component: StaticFooter,
  argTypes: {},
  parameters: {
    docs: {
      description: {
        component:
          'Static footer component displayed at the bottom of error boundary pages. Shows copyright information with dynamic year.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {},
};
