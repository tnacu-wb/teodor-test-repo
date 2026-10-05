import { Meta, StoryObj } from '@storybook/react';

import Button from './Button.component';

const meta: Meta<typeof Button> = {
  title: 'Button',
  component: Button,
  argTypes: {
    variant: {
      description: 'Visual variant of the button',
      options: [
        'primary',
        'secondary',
        'tertiary',
        'default',
        'generic',
        'genericSecondary',
        'marketingDefault',
        'marketingSolid',
        'circle',
        'unbrandedRestaurant',
        'login',
      ],
      control: {
        type: 'select',
      },
    },
    size: {
      description: 'Size of the button',
      options: ['xxs', 'xs', 'xsm', 'sm', 'md', 'full'],
      control: {
        type: 'select',
      },
    },
    disabled: {
      description: 'Disable button interactions (deprecated, use isDisabled)',
      control: {
        type: 'boolean',
      },
    },
    isDisabled: {
      description: 'Disable button interactions',
      control: {
        type: 'boolean',
      },
    },
    onClick: {
      description: 'Callback function when button is clicked',
      control: false,
    },
    children: {
      description: 'Button label text or content',
      control: { type: 'text' },
    },
  },
  parameters: {
    docs: {
      description: {
        component:
          'Clickable action button with multiple visual variants, sizes, and disabled state support.',
      },
    },
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=862%3A40519',
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// --- Basic Usage ---

export const Default: Story = {
  args: {
    variant: 'default',
    size: 'md',
    isDisabled: false,
    children: 'Button',
  },
};

// --- Variants ---

export const PrimaryMedium: Story = {
  args: {
    variant: 'primary',
    size: 'md',
    isDisabled: false,
    children: 'Primary Button',
  },
};

// --- Sizes ---

export const PrimarySmall: Story = {
  args: {
    ...PrimaryMedium.args,
    size: 'sm',
    children: 'Primary Small',
  },
};

// --- States ---

export const PrimaryDisabled: Story = {
  args: {
    ...PrimaryMedium.args,
    isDisabled: true,
    children: 'Primary Disabled',
  },
};

export const PrimarySmallDisabled: Story = {
  args: {
    ...PrimarySmall.args,
    isDisabled: true,
    children: 'Primary Small Disabled',
  },
};

export const SecondaryMedium: Story = {
  args: {
    variant: 'secondary',
    size: 'md',
    isDisabled: false,
    children: 'Secondary Button',
  },
};

export const SecondarySmall: Story = {
  args: {
    variant: 'secondary',
    size: 'sm',
    isDisabled: false,
    children: 'Secondary Small',
  },
};

export const SecondaryDisabled: Story = {
  args: {
    ...SecondaryMedium.args,
    isDisabled: true,
    children: 'Secondary Disabled',
  },
};

export const SecondarySmallDisabled: Story = {
  args: {
    ...SecondarySmall.args,
    isDisabled: true,
    children: 'Secondary Small Disabled',
  },
};

export const TertiaryMedium: Story = {
  args: {
    variant: 'tertiary',
    size: 'md',
    isDisabled: false,
    children: 'Tertiary Button',
  },
};

export const TertiarySmall: Story = {
  args: {
    variant: 'tertiary',
    size: 'sm',
    isDisabled: false,
    children: 'Tertiary Small',
  },
};

export const TertiaryDisabled: Story = {
  args: {
    ...TertiaryMedium.args,
    isDisabled: true,
    children: 'Tertiary Disabled',
  },
};

export const TertiarySmallDisabled: Story = {
  args: {
    ...TertiarySmall.args,
    isDisabled: true,
    children: 'Tertiary Small Disabled',
  },
};

export const Generic: Story = {
  args: {
    variant: 'generic',
    size: 'md',
    isDisabled: false,
    children: 'Generic Button',
  },
};

export const GenericSmall: Story = {
  args: {
    variant: 'generic',
    size: 'sm',
    isDisabled: false,
    children: 'Generic Small',
  },
};

export const GenericSecondary: Story = {
  args: {
    variant: 'genericSecondary',
    size: 'md',
    isDisabled: false,
    children: 'Generic Secondary',
  },
};

export const MarketingDefault: Story = {
  args: {
    variant: 'marketingDefault',
    size: 'md',
    isDisabled: false,
    children: 'Marketing Default',
  },
};

export const MarketingSolid: Story = {
  args: {
    variant: 'marketingSolid',
    size: 'md',
    isDisabled: false,
    children: 'Marketing Solid',
  },
};

export const CircleVariant: Story = {
  args: {
    variant: 'circle',
    size: 'md',
    isDisabled: false,
    children: '+',
  },
};

export const UnbrandedRestaurant: Story = {
  args: {
    variant: 'unbrandedRestaurant',
    size: 'md',
    isDisabled: false,
    children: 'Unbranded Restaurant',
  },
};

export const LoginVariant: Story = {
  args: {
    variant: 'login',
    size: 'full',
    isDisabled: false,
    children: 'Login',
  },
};

export const FullWidth: Story = {
  args: {
    variant: 'primary',
    size: 'full',
    isDisabled: false,
    children: 'Full Width Button',
  },
};

// --- Edge Cases ---

export const WithLongLabel: Story = {
  args: {
    ...Default.args,
    children: 'This is a button with a very long label to test text wrapping and overflow behavior',
  },
};

export const WithSingleCharacter: Story = {
  args: {
    ...Default.args,
    variant: 'circle',
    children: '×',
  },
};
