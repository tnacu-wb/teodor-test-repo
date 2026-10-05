import { Meta, StoryObj } from '@storybook/react';
import React from 'react';

import { Alert, Info, Success, Error as ErrorIcon, Close } from '../../assets/icons';
import Icon from './Icon.component';

const meta: Meta<typeof Icon> = {
  title: 'Icon',
  component: Icon,
  argTypes: {
    svg: {
      description: 'A React SVG element to render inside the icon container',
      control: false,
    },
    src: {
      description: 'Image source URL (used instead of svg for raster/remote images)',
      control: 'text',
    },
    alt: {
      description: 'Alt text for the image (applies when using src)',
      control: 'text',
    },
    useNextImage: {
      description: 'Use Next.js Image component for optimised loading (requires src)',
      control: 'boolean',
    },
    height: {
      description: 'Height of the icon container (defaults to 4rem)',
      control: 'text',
    },
    width: {
      description: 'Width of the icon container (defaults to 10.75rem)',
      control: 'text',
    },
  },
  parameters: {
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=862%3A40511',
    },
    docs: {
      description: {
        component:
          'An icon wrapper component that supports inline SVGs, Chakra Image, or Next.js optimised Image rendering modes.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// --- Basic Usage ---

export const Default: Story = {
  args: {
    svg: <Alert />,
  },
};

// --- Variants ---

export const WithColorOverride: Story = {
  args: {
    svg: <Alert color="blue" />,
  },
};

export const InfoVariant: Story = {
  args: {
    svg: <Info />,
  },
};

export const SuccessVariant: Story = {
  args: {
    svg: <Success />,
  },
};

export const ErrorVariant: Story = {
  args: {
    svg: <ErrorIcon />,
  },
};

export const CloseVariant: Story = {
  args: {
    svg: <Close />,
  },
};

// --- Sizes ---

export const WithCustomSize: Story = {
  args: {
    svg: <Alert />,
    height: '6rem',
    width: '6rem',
  },
};

export const SmallSize: Story = {
  args: {
    svg: <Alert />,
    height: '1.5rem',
    width: '1.5rem',
  },
};

// --- Configuration ---

export const WithImageSrc: Story = {
  args: {
    src: 'https://placehold.co/172x64/png?text=Icon',
    alt: 'Placeholder icon',
  },
};

export const WithNextImage: Story = {
  args: {
    src: 'https://placehold.co/172x64/png?text=Next',
    alt: 'Next.js optimised icon',
    useNextImage: true,
  },
};

// --- Edge Cases ---

export const WithEmptyAlt: Story = {
  args: {
    src: 'https://placehold.co/172x64/png?text=NoAlt',
    alt: '',
  },
};

export const WithNoSvgOrSrc: Story = {
  args: {},
};
