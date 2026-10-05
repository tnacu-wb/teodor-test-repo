import { Box, Image } from '@chakra-ui/react';
import { Meta, StoryObj } from '@storybook/react';
import { ComponentProps, useState } from 'react';

import MobileCarousel from './MobileCarousel.component';

const sampleImages = [
  'https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-2.jpg',
  'https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-3.jpg',
  'https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-5.jpg',
  'https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Bedroom-6.jpg',
  'https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Bedroom-7.jpg',
  'https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Shower.jpg',
  'https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg',
];

const sampleThumbnails = sampleImages.map((src) => <Image key={src} src={src} />);

const sampleChildren = sampleImages.map((src) => <img key={src} src={src} />);

function MobileCarouselWithState(props: Partial<ComponentProps<typeof MobileCarousel>>) {
  const [activeSlide, setActiveSlide] = useState(props.activeSlide ?? 0);
  return (
    <MobileCarousel
      {...props}
      activeSlide={activeSlide}
      setActiveSlide={setActiveSlide}
      thumbnails={props.thumbnails ?? sampleThumbnails}
    >
      {props.children ?? sampleChildren}
    </MobileCarousel>
  );
}

const meta: Meta<typeof MobileCarousel> = {
  title: 'MobileCarousel',
  component: MobileCarousel,
  argTypes: {
    children: {
      description: 'Main carousel slide content (typically full-size images)',
      control: false,
    },
    thumbnails: {
      description: 'Array of JSX elements rendered as clickable thumbnail navigation',
      control: false,
    },
    activeSlide: {
      description: 'Index of the currently active slide (zero-based)',
      control: { type: 'number', min: 0 },
    },
    setActiveSlide: {
      description: 'State setter callback invoked when the active slide changes',
      control: false,
    },
  },
  decorators: [
    (Story) => (
      <Box w="35%" m="auto">
        <Story />
      </Box>
    ),
  ],
  parameters: {
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=1109%3A13793',
    },
    docs: {
      description: {
        component:
          'A mobile-optimised image carousel with thumbnail navigation, built on react-slick. Supports swipe gestures and keyboard navigation.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

const renderWithState = (args: Partial<ComponentProps<typeof MobileCarousel>>) => (
  <MobileCarouselWithState {...args} />
);

// --- Basic Usage ---

export const Default: Story = {
  render: renderWithState,
};

// --- Configuration ---

export const WithDifferentInitialSlide: Story = {
  render: renderWithState,
  args: {
    activeSlide: 3,
  },
};

// --- Edge Cases ---

export const WithTwoSlides: Story = {
  render: renderWithState,
  args: {
    thumbnails: sampleThumbnails.slice(0, 2),
    children: sampleChildren.slice(0, 2),
  },
};
