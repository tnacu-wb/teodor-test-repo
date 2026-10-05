import { Meta, StoryObj } from '@storybook/react';

import Badge from './Badge.component';

const meta: Meta<typeof Badge> = {
  title: 'Badge',
  component: Badge,
  argTypes: {
    variant: {
      description: 'Visual variant/style of the badge',
      options: ['primary', 'secondary', 'ZIP', 'hub', 'square', 'solid'],
      control: {
        type: 'radio',
      },
    },
    children: {
      description: 'Text or content displayed inside the badge',
      control: 'text',
    },
    badgecolor: {
      description: 'Custom color for the badge',
      control: {
        type: 'color',
        presetColors: ['blue', 'lightPurple'],
      },
    },
  },
  parameters: {
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=862%3A40520',
    },
    docs: {
      description: {
        component:
          'A versatile badge component for displaying labels, tags, or status indicators with multiple variants and color options.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// --- Basic Usage ---

export const Default: Story = {
  args: {
    variant: 'primary',
    children: 'Primary',
  },
};

export const PrimaryWithLightPurple: Story = {
  args: {
    variant: 'primary',
    children: 'Primary (Light Purple)',
    badgecolor: 'lightPurple',
  },
};

// --- Variants ---

export const SecondaryDefault: Story = {
  args: {
    variant: 'secondary',
    children: 'Secondary',
  },
};

export const SecondaryWithBlue: Story = {
  args: {
    variant: 'secondary',
    children: 'Secondary (Blue)',
    badgecolor: 'blue',
  },
};

export const ZIPVariant: Story = {
  args: {
    variant: 'ZIP',
    children: 'ZIP',
  },
};

export const HubVariant: Story = {
  args: {
    variant: 'hub',
    children: 'hub',
  },
};

export const SquareVariant: Story = {
  args: {
    variant: 'square',
    children: 'Square',
  },
};

export const SolidVariant: Story = {
  args: {
    variant: 'solid',
    children: 'Solid',
  },
};

// --- Configuration ---

export const WithCustomColor: Story = {
  args: {
    variant: 'primary',
    children: 'Custom Color',
    badgecolor: 'blue',
  },
};

// --- Edge Cases ---

export const WithLongContent: Story = {
  args: {
    variant: 'primary',
    children: 'This is a very long badge label that might overflow',
  },
};

export const WithSingleCharacter: Story = {
  args: {
    variant: 'primary',
    children: 'X',
  },
};

export const WithSpecialCharacters: Story = {
  args: {
    variant: 'secondary',
    children: '★ New! 50% off',
  },
};
