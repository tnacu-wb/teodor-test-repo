import { Box, ListItem, UnorderedList } from '@chakra-ui/react';
import { Meta, StoryObj } from '@storybook/react';

import ButtonComponent from '../Button';
import Popover from './Popover.component';

const meta: Meta<typeof Popover> = {
  title: 'Popover',
  component: Popover,
  argTypes: {
    triggerItem: {
      description: 'Element that triggers the popover when clicked.',
      control: false,
    },
    children: {
      description: 'Content rendered inside the popover panel.',
      control: false,
    },
    styles: {
      description: 'Optional style overrides for popover internals like arrow styles.',
      control: 'object',
    },
  },
  decorators: [
    (Story) => {
      return (
        <Box mt="2xl" display="flex" alignItems="center" justifyContent="center">
          <Story key="popover-key" />
        </Box>
      );
    },
  ],
  parameters: {
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=1900%3A60167',
    },
    docs: {
      description: {
        component:
          'Popover displays contextual content anchored to a trigger element for hints, links, or supplementary information.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

const baseTrigger = (
  <ButtonComponent key="popover-trigger" size="sm" variant="primary">
    Click me
  </ButtonComponent>
);

// --- Basic Usage ---
export const Default: Story = {
  args: {
    triggerItem: baseTrigger,
    children: <p>Hello from inside the popover!</p>,
  },
};

// --- Variants ---
export const ListContentVariant: Story = {
  args: {
    triggerItem: (
      <ButtonComponent key="popover-trigger-list" size="sm" variant="primary">
        Click to show the list
      </ButtonComponent>
    ),
    children: (
      <UnorderedList key="popover-content-example">
        <ListItem key="1">Lorem ipsum dolor sit amet</ListItem>
        <ListItem key="2">Consectetur adipiscing elit</ListItem>
        <ListItem key="3">Integer molestie lorem at massa</ListItem>
        <ListItem key="4">Facilisis in pretium nisl aliquet</ListItem>
      </UnorderedList>
    ),
  },
};

// --- Configuration ---
export const WithCustomArrowStyles: Story = {
  args: {
    ...Default.args,
    styles: {
      arrowStyles: {
        bg: 'white',
        border: '1px solid var(--chakra-colors-lightGrey4)',
      },
    },
  },
};

// --- Edge Cases ---
export const WithLongContent: Story = {
  args: {
    ...Default.args,
    children: (
      <p>
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in vulputate purus, quis
        ornare purus. Integer et ex in nunc laoreet venenatis ut id diam. Sed sed pellentesque
        sapien. Praesent eu sem lectus. Praesent dapibus est dui, ut dignissim massa consequat nec.
        Sed condimentum enim quis porttitor molestie. Nulla finibus facilisis quam, quis rhoncus
        odio eleifend a. Sed eget velit ac ipsum pulvinar lobortis. Nulla odio risus, viverra eget
        sollicitudin et, bibendum et enim.
      </p>
    ),
  },
};

export const WithEmptyContent: Story = {
  args: {
    ...Default.args,
    children: <></>,
  },
};
