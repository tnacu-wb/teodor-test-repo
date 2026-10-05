import { Box } from '@chakra-ui/react';
import { Meta, StoryObj } from '@storybook/react';
import { useEffect, useState } from 'react';

import Carousel from './Carousel.component';

const imageUrls = [
  'https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-2.jpg',
  'https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-3.jpg',
  'https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-5.jpg',
  'https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Bedroom-6.jpg',
  'https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Bedroom-7.jpg',
  'https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Shower.jpg',
  'https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg',
];

const carouselSlides = imageUrls.map((imageUrl, index) => (
  <img key={imageUrl} src={imageUrl} alt={`Premier Plus room ${index + 1}`} />
));

function StatefulCarousel({ activeSlide = 0, ...args }: React.ComponentProps<typeof Carousel>) {
  const [currentSlide, setCurrentSlide] = useState(activeSlide);

  useEffect(() => {
    setCurrentSlide(activeSlide);
  }, [activeSlide]);

  return (
    <Carousel {...args} activeSlide={currentSlide} setActiveSlide={setCurrentSlide}>
      {args.children}
    </Carousel>
  );
}

const meta: Meta<typeof Carousel> = {
  title: 'Carousel',
  component: Carousel,
  argTypes: {
    activeSlide: {
      description: 'Zero-based index of the slide currently in view.',
      control: {
        type: 'number',
        min: 0,
        max: imageUrls.length - 1,
      },
    },
    setActiveSlide: {
      description: 'Callback used by the carousel to update the active slide index.',
      table: {
        disable: true,
      },
    },
    children: {
      description: 'Slide content rendered by the carousel.',
      table: {
        disable: true,
      },
    },
    focusOnLoad: {
      description: 'Focuses slider content on load and when the active slide changes.',
      control: 'boolean',
    },
    infinite: {
      description: 'Loops from the last slide back to the first slide.',
      control: 'boolean',
    },
    arrows: {
      description: 'Shows next and previous navigation arrow buttons.',
      control: 'boolean',
    },
    additionalNavButtonStyles: {
      description: 'Style overrides for navigation buttons and icons.',
      control: 'object',
    },
  },
  decorators: [
    (Story) => (
      <Box w="55%" m="auto">
        <Story />
      </Box>
    ),
  ],
  parameters: {
    docs: {
      description: {
        component:
          'Image carousel/slider with configurable navigation arrows, infinite looping, and focus management.',
      },
    },
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=1109%3A13793',
    },
  },
};

export default meta;

type Story = StoryObj<typeof meta>;

const baseArgs: Story['args'] = {
  activeSlide: 0,
  focusOnLoad: true,
  infinite: true,
  arrows: true,
  children: carouselSlides,
};

// --- Basic Usage ---

export const Default: Story = {
  args: {
    ...baseArgs,
  },
  render: (args) => <StatefulCarousel {...args} />,
};

// --- Configuration ---

export const StartsFromThirdSlide: Story = {
  args: {
    ...baseArgs,
    activeSlide: 2,
  },
  render: (args) => <StatefulCarousel {...args} />,
};

export const FiniteCarousel: Story = {
  args: {
    ...baseArgs,
    infinite: false,
  },
  render: (args) => <StatefulCarousel {...args} />,
};

export const WithoutNavigationArrows: Story = {
  args: {
    ...baseArgs,
    arrows: false,
  },
  render: (args) => <StatefulCarousel {...args} />,
};

export const WithCustomNavigationStyles: Story = {
  args: {
    ...baseArgs,
    additionalNavButtonStyles: {
      boxStyles: {
        background: 'primary',
        opacity: 1,
      },
      leftIconStyle: {
        transform: 'translateX(1.4rem) scale(1.2)',
      },
      rightIconStyle: {
        transform: 'translateX(1.5rem) scale(1.2)',
      },
    },
  },
  render: (args) => <StatefulCarousel {...args} />,
};

// --- Edge Cases ---

export const WithSingleSlide: Story = {
  args: {
    ...baseArgs,
    infinite: false,
    arrows: false,
    children: [<img key="single" src={imageUrls[0]} alt="Single slide carousel" />],
  },
  render: (args) => <StatefulCarousel {...args} />,
};
