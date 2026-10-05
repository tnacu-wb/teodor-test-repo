import { Text } from '@chakra-ui/react';
import { Meta, StoryObj } from '@storybook/react';

import RadioButton from './RadioButton.component';

const meta: Meta<typeof RadioButton> = {
  title: 'RadioButton',
  component: RadioButton,
  argTypes: {
    isChecked: {
      description: 'Controls whether the radio is selected.',
      control: 'boolean',
    },
    children: {
      description: 'Custom label content rendered next to the radio indicator.',
      control: false,
    },
    listIndex: {
      description: 'Position hint in grouped layouts to compute border radius and border widths.',
      control: 'select',
      options: [0, 1, 2, 'left', 'last'],
    },
    type: {
      description: 'Optional type suffix used to customize test IDs for radio wrapper and input.',
      control: 'text',
    },
    variant: {
      description: 'Visual variant for radio container styling.',
      control: 'radio',
      options: ['borderless'],
    },
    width: {
      description: 'Container width applied to the radio wrapper.',
      control: 'text',
    },
    padding: {
      description: 'Optional responsive padding override for the radio wrapper.',
      control: 'object',
    },
    flexDirection: {
      description: 'Flex direction passed through to the Chakra Radio component.',
      control: 'radio',
      options: ['row', 'row-reverse', 'column', 'column-reverse'],
    },
    alignItems: {
      description: 'Alignment for the Chakra Radio component content.',
      control: 'radio',
      options: ['flex-start', 'center', 'flex-end', 'stretch', 'baseline'],
    },
    withOutline: {
      description: 'Enables an outline-based selected state style on the wrapper.',
      control: 'boolean',
    },
    isDisabled: {
      description: 'Disables radio interaction and applies disabled styles.',
      control: 'boolean',
    },
    onChange: {
      description: 'Callback fired when the radio selection changes.',
      control: false,
    },
    borderColorChecked: {
      description: 'Border color when radio button is checked.',
      control: 'text',
    },
    backgroundColorChecked: {
      description: 'Background color when radio button is checked.',
      control: 'text',
    },
  },
  parameters: {
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=862%3A40523',
    },
    docs: {
      description: {
        component:
          'Radio Button presents a selectable option with optional rich content and grouped border treatments.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// eslint-disable-next-line @typescript-eslint/no-empty-function
const noop = () => {};

const wetBathroomDescription = (
  <>
    <Text>Wet bathroom</Text>
    <Text>
      Level access shower room with high-powered shower, conveniently placed shower controls,
      folding seat and wider doors.
    </Text>
  </>
);

const loweredBathroomDescription = (
  <>
    <Text>Lowered bathroom</Text>
    <Text>
      Bathroom with a lowered bath set at standard wheelchair height (480mm), lever taps, wider
      doors and bath mats available on request.
    </Text>
  </>
);

// --- Basic Usage ---
export const Default: Story = {
  args: {
    isChecked: false,
    onChange: noop,
  },
};

// --- Variants ---
export const BorderlessVariant: Story = {
  args: {
    ...Default.args,
    children: wetBathroomDescription,
    variant: 'borderless',
  },
};

// --- States ---
export const SelectedState: Story = {
  args: {
    ...Default.args,
    isChecked: true,
  },
};

export const DisabledState: Story = {
  args: {
    ...Default.args,
    isDisabled: true,
  },
};

// --- Configuration ---
export const WithOutline: Story = {
  args: {
    ...Default.args,
    withOutline: true,
    children: loweredBathroomDescription,
  },
};

// --- Edge Cases ---
export const WithLongContent: Story = {
  args: {
    ...Default.args,
    children: wetBathroomDescription,
  },
};

export const WithEmptyContent: Story = {
  args: {
    ...Default.args,
    children: <Text />,
  },
};
