import { Meta, StoryObj } from '@storybook/react';

import Card from './Card.component';

const loremIpsumLong = `Lorem ipsum dolor sit amet, consectetur adipiscing elit. In lacus nisl, efficitur quis ex et,
finibus ultricies nisl. In sagittis dui sem, nec eleifend lorem consequat vitae. Curabitur nec
est massa. Suspendisse sodales lobortis lacus, eu porttitor lectus. Vestibulum ante ipsum
primis in faucibus orci luctus et ultrices posuere cubilia curae; Cras pharetra, tellus quis
hendrerit ultricies, mi nisi lacinia diam, et cursus urna justo quis orci. Duis congue massa
quis purus viverra scelerisque. In non ex in augue pellentesque sollicitudin quis in tortor.
Fusce dictum, mi et lobortis laoreet, ipsum eros vehicula sapien, interdum ullamcorper quam
erat ut mauris. Donec vitae leo quis mi iaculis sagittis ac sit amet libero. Phasellus aliquam
tortor ligula, id luctus velit condimentum quis. Ut eget sem non nisi elementum dignissim et
vitae sem. Nullam euismod ut mauris et varius. Cras vestibulum blandit ante. Sed condimentum
justo mattis viverra interdum. Fusce vel turpis congue metus ornare maximus. Praesent mollis
hendrerit est sed ultricies. In lacinia, urna vitae tincidunt viverra, mauris nulla tincidunt
ipsum, quis tempor felis arcu id libero. Maecenas id erat ut metus aliquam facilisis. Morbi
orci libero, porta tincidunt porttitor vel, scelerisque eu mauris. Nam at arcu vitae mauris
tincidunt euismod nec.`;

const meta: Meta<typeof Card> = {
  title: 'Card',
  component: Card,
  argTypes: {
    children: {
      description: 'Card content - accepts any React node',
      control: false,
    },
    padding: {
      description: 'Internal padding of the card (default: "md")',
      control: { type: 'text' },
    },
    p: {
      description: 'Chakra UI padding shorthand',
      control: { type: 'text' },
    },
    bgColor: {
      description: 'Background color of the card',
      control: { type: 'color' },
    },
  },
  parameters: {
    docs: {
      description: {
        component:
          'Flexible container card with border, shadow, and configurable padding and background color.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// --- Basic Usage ---

export const Default: Story = {
  args: {
    children: <p>{loremIpsumLong}</p>,
  },
};

export const WithShortContent: Story = {
  args: {
    children: <p>This is a short card with minimal content.</p>,
  },
};

export const WithLongContent: Story = {
  args: {
    children: <p>{loremIpsumLong}</p>,
  },
};

export const WithStructuredContent: Story = {
  args: {
    children: (
      <div>
        <h3>Card Title</h3>
        <p>This is a card with structured HTML content including headings and paragraphs.</p>
        <ul>
          <li>Item 1</li>
          <li>Item 2</li>
          <li>Item 3</li>
        </ul>
      </div>
    ),
  },
};

export const WithCustomBackground: Story = {
  args: {
    children: <p>Card with a custom background colour</p>,
    bgColor: '#f0f4ff',
  },
};

// --- Configuration ---

export const WithSmallPadding: Story = {
  args: {
    children: <p>Card with small padding</p>,
    p: 'sm',
  },
};

export const WithLargePadding: Story = {
  args: {
    children: <p>Card with large padding</p>,
    p: 'lg',
  },
};

export const WithCustomPadding: Story = {
  args: {
    children: <p>Card with custom padding (40px)</p>,
    p: '40px',
  },
};

// --- Edge Cases ---

export const WithComplexContent: Story = {
  args: {
    children: (
      <div>
        <h2>Booking Summary</h2>
        <div style={{ marginTop: '16px' }}>
          <p>
            <strong>Hotel:</strong> Premier Inn London
          </p>
          <p>
            <strong>Check-in:</strong> 2024-12-15
          </p>
          <p>
            <strong>Check-out:</strong> 2024-12-17
          </p>
          <p>
            <strong>Total:</strong> £299.00
          </p>
        </div>
      </div>
    ),
  },
};
