import { Box, Center, Flex, Text } from '@chakra-ui/react';
import { Meta, StoryObj } from '@storybook/react';
import { ComponentProps, useState } from 'react';

import Button from '../Button';
import ModalVariants from './ModalVariants.component';
import {
  CookieModalVariantProps,
  DefaultModalVariantProps,
  GalleryModalVariantProps,
  InfoModalVariantProps,
  LoginModalVariantProps,
} from './variants';

// eslint-disable-next-line @typescript-eslint/no-empty-function
const noop = () => {};

const shortContent = (
  <Flex maxW="26rem" mx="7rem" mt="1.5rem" mb="2.5rem">
    <Text>
      Lorem Ipsum is simply dummy text of the printing and typesetting industry. It has survived not
      only five centuries, but also the leap into electronic typesetting.
    </Text>
  </Flex>
);

const longContent = (
  <Flex maxW="62rem" mx="1.5rem" mt="1.5rem" mb="2.5rem">
    <Text>
      Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has
      been the industry standard dummy text ever since the 1500s, when an unknown printer took a
      galley of type and scrambled it to make a type specimen book. It has survived not only five
      centuries, but also the leap into electronic typesetting, remaining essentially unchanged.
      Lorem Ipsum has been the industry standard dummy text ever since the 1500s, when an unknown
      printer took a galley of type and scrambled it to make a type specimen book.
    </Text>
  </Flex>
);

const galleryContent = (
  <Flex maxW="61rem" mt="lg" mb="6xl" mx="4xl">
    <Text>
      Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in classical
      Latin literature from 45 BC.
    </Text>
  </Flex>
);

function ModalVariantsWithState(
  props: Omit<ComponentProps<typeof ModalVariants>, 'isOpen' | 'onClose'>
) {
  const [isOpen, setIsOpen] = useState(true);

  return (
    <Box>
      <Center>
        <Button onClick={() => setIsOpen((prev) => !prev)} size="md" variant="primary">
          OPEN MODAL
        </Button>
      </Center>
      <ModalVariants {...props} isOpen={isOpen} onClose={() => setIsOpen(false)} />
    </Box>
  );
}

const meta: Meta<typeof ModalVariants> = {
  title: 'ModalVariants',
  component: ModalVariants,
  argTypes: {
    isOpen: {
      description: 'Controls whether the modal is visible',
      control: false,
    },
    onClose: {
      description: 'Callback invoked when the modal requests to close',
      control: false,
    },
    closeOnOverlayClick: {
      description: 'Closes the modal when users click the overlay',
      control: 'boolean',
    },
    children: {
      description: 'Body content rendered inside the modal',
      control: false,
    },
    dataTestId: {
      description: 'Optional base data-testid value for QA selectors',
      control: 'text',
    },
    variant: {
      description: 'Modal variant that selects the internal modal implementation',
      control: 'select',
      options: ['default', 'gallery', 'info', 'cookie', 'login', 'data', 'upgrade'],
    },
    variantProps: {
      description: 'Additional variant-specific configuration object',
      control: 'object',
    },
    hasAutoFocus: {
      description: 'Enables Chakra auto-focus when the modal opens',
      control: 'boolean',
    },
    headerStyles: {
      description: 'Style overrides applied to the modal header',
      control: 'object',
    },
    headerContentStyles: {
      description: 'Style overrides for modal header content area',
      control: 'object',
    },
    contentContainerStyles: {
      description: 'Style overrides for modal content wrapper',
      control: 'object',
    },
    overlayStyles: {
      description: 'Style overrides for the modal overlay',
      control: 'object',
    },
    updatedWidth: {
      description: 'Responsive width map for the modal container',
      control: 'object',
    },
    isLoading: {
      description: 'Shows loading visual state for supported modal variants',
      control: 'boolean',
    },
    landscapeModal: {
      description: 'Landscape rendering flags for supported variants',
      control: 'object',
    },
    portalProps: {
      description: 'Portal rendering configuration for append target and container ref',
      control: 'object',
    },
  },
  parameters: {
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=1091%3A14480',
    },
    docs: {
      description: {
        component:
          'Modal variant orchestrator that maps a variant key to a specific modal implementation while preserving shared modal controls.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

const defaultVariantProps: DefaultModalVariantProps = {
  title: '',
};

const infoVariantProps: InfoModalVariantProps = {
  title: 'Hotel Facilities',
  delimiter: true,
};

const cookieVariantProps: CookieModalVariantProps = {
  title: 'Cookie settings',
  closeOnOverlayClick: false,
};

const loginVariantProps: LoginModalVariantProps = {
  title: '',
  goBackButtonText: 'Go back',
  onGoBack: noop,
  footer: 'I am a footer',
};

// --- Basic Usage ---

export const Default: Story = {
  args: {
    variant: 'default',
    variantProps: defaultVariantProps,
    closeOnOverlayClick: true,
    children: shortContent,
  },
  render: (args) => <ModalVariantsWithState {...args} />,
};

// --- Variants ---

export const GalleryVariant: Story = {
  args: {
    ...Default.args,
    variant: 'gallery',
    variantProps: {
      title: '4/12',
    } as GalleryModalVariantProps,
    children: galleryContent,
  },
};

export const InfoVariant: Story = {
  args: {
    ...Default.args,
    variant: 'info',
    variantProps: infoVariantProps,
    children: (
      <Flex flexDir="column" px="md">
        <Text h="4rem">This will be some intro text.</Text>
        <Text h="4rem">This will be some intro text.</Text>
        <Text h="4rem">This will be some intro text.</Text>
      </Flex>
    ),
  },
};

export const CookieVariant: Story = {
  args: {
    ...Default.args,
    variant: 'cookie',
    variantProps: cookieVariantProps,
    closeOnOverlayClick: false,
  },
};

export const LoginVariant: Story = {
  args: {
    ...Default.args,
    variant: 'login',
    variantProps: loginVariantProps,
    closeOnOverlayClick: false,
    children: (
      <Flex mt="md" mb="lg" mx="lg" maxW="33rem" flexDirection="column">
        <Flex>Log into your Premier Inn account</Flex>
      </Flex>
    ),
  },
};

// --- States ---

export const LoadingState: Story = {
  args: {
    ...Default.args,
    isLoading: true,
  },
};

// --- Configuration ---

export const WithCustomHeaderStyles: Story = {
  args: {
    ...Default.args,
    headerStyles: {
      bg: 'lightGrey1',
      color: 'darkGrey3',
    },
  },
};

// --- Edge Cases ---

export const WithLongContent: Story = {
  args: {
    ...Default.args,
    children: longContent,
  },
};

export const WithEmptyContent: Story = {
  args: {
    ...Default.args,
    children: <Box />,
  },
};
