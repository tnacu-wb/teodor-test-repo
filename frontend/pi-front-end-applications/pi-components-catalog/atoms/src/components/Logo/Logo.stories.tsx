import { Meta, StoryObj } from '@storybook/react';

import Logo from './Logo.component';

const meta: Meta<typeof Logo> = {
  title: 'Logo',
  component: Logo,
  argTypes: {
    href: {
      description: 'URL the logo links to when clicked',
      control: 'text',
    },
    variant: {
      description: 'Brand variant determining which logo SVG or image is rendered',
      control: 'select',
      options: [
        'pi',
        'pid',
        'pi-simple',
        'pid-simple',
        'pi-icon',
        'hub',
        'hub-simple',
        'zip',
        'zip-simple',
      ],
    },
    src: {
      description: 'Optional image source URL used for non-SVG logo rendering',
      control: 'text',
    },
    alt: {
      description: 'Alt text for the logo image',
      control: 'text',
    },
    isHeaderLogo: {
      description: 'When true, renders the Hub variant as an inline SVG instead of an image',
      control: 'boolean',
    },
    useNextImage: {
      description: 'When true, uses Next.js Image component for optimised loading',
      control: 'boolean',
    },
  },
  parameters: {
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=862%3A40510',
    },
    docs: {
      description: {
        component:
          'Renders brand logos for Premier Inn, hub by Premier Inn, and ZIP by Premier Inn. Supports multiple variants, optional link wrapping, and responsive scaling.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// --- Basic Usage ---

export const Default: Story = {
  args: {
    href: 'https://www.premierinn.com/gb/en/home.html',
    variant: 'pi',
  },
};

// --- Premier Inn Variants ---

export const PILogoSimple: Story = {
  args: {
    ...Default.args,
    variant: 'pi-simple',
  },
};

export const PILogoIcon: Story = {
  args: {
    ...Default.args,
    variant: 'pi-icon',
  },
};

// --- Hub Variants ---

export const HubLogo: Story = {
  args: {
    ...Default.args,
    variant: 'hub',
  },
};

export const HubLogoSimple: Story = {
  args: {
    ...Default.args,
    variant: 'hub-simple',
  },
};

// --- ZIP Variants ---

export const ZipLogo: Story = {
  args: {
    ...Default.args,
    variant: 'zip',
  },
};

export const ZipLogoSimple: Story = {
  args: {
    ...Default.args,
    variant: 'zip-simple',
  },
};

// --- States ---

export const WithoutLink: Story = {
  args: {
    variant: 'pi',
  },
};

export const HeaderHubLogo: Story = {
  args: {
    ...Default.args,
    variant: 'hub',
    isHeaderLogo: true,
  },
};
