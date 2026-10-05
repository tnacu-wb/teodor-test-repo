import { Text } from '@chakra-ui/react';
import { Meta, StoryObj } from '@storybook/react';
import React, { ComponentProps, useState } from 'react';

import ExpandText from './ExpandText.component';

const sampleText =
  'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis luctus scelerisque felis eu tempor. Etiam eu enim condimentum, vulputate nisl ut, vestibulum ante. Vivamus aliquam maximus tortor ut pharetra. Integer vehicula, arcu eget mollis hendrerit, purus massa ornare odio, sit amet posuere justo neque vitae libero. Aliquam erat volutpat. Nullam ultricies accumsan hendrerit. Praesent in purus arcu. Praesent placerat, libero et congue viverra, augue leo lobortis sapien, vitae cursus tellus enim facilisis purus.';

function ExpandTextWithState(props: ComponentProps<typeof ExpandText>) {
  const [show, setShow] = useState(props.show ?? false);
  return (
    <ExpandText
      {...props}
      show={show}
      onClick={() => setShow(!show)}
      buttonText={show ? 'Show Less' : 'Show More'}
    />
  );
}

const meta: Meta<typeof ExpandText> = {
  title: 'ExpandText',
  component: ExpandText,
  argTypes: {
    show: {
      description: 'Controls whether the content is expanded or collapsed',
      control: 'boolean',
    },
    buttonText: {
      description: 'Text displayed on the toggle button',
      control: 'text',
    },
    startingHeight: {
      description: 'Initial height of the collapsed content area (CSS value)',
      control: 'text',
    },
    onClick: {
      description: 'Callback fired when the toggle button is clicked',
      control: false,
    },
    children: {
      description: 'Content to be displayed inside the collapsible area',
      control: false,
    },
  },
  parameters: {
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=1573%3A43926',
    },
    docs: {
      description: {
        component:
          'A collapsible text block that shows a gradient fade and a toggle button to expand or collapse content.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// --- Basic Usage ---

export const Default: Story = {
  args: {
    show: false,
    buttonText: 'Show More',
    startingHeight: 87,
    children: <Text>{sampleText}</Text>,
  },
};

// --- States ---

export const ExpandedState: Story = {
  args: {
    ...Default.args,
    show: true,
    buttonText: 'Show Less',
  },
};

// --- Configuration ---

export const WithCustomStartingHeight: Story = {
  args: {
    ...Default.args,
    startingHeight: 40,
  },
};

export const WithInteractiveToggle: Story = {
  render: (args) => <ExpandTextWithState {...args} />,
  args: {
    show: false,
    buttonText: 'Show More',
    startingHeight: 87,
    children: <Text>{sampleText}</Text>,
  },
};

// --- Edge Cases ---

export const WithLongContent: Story = {
  args: {
    show: false,
    buttonText: 'Show More',
    startingHeight: 87,
    children: (
      <Text>
        {sampleText} {sampleText} {sampleText}
      </Text>
    ),
  },
};

export const WithShortContent: Story = {
  args: {
    show: false,
    buttonText: 'Show More',
    startingHeight: 87,
    children: <Text>Short text.</Text>,
  },
};
