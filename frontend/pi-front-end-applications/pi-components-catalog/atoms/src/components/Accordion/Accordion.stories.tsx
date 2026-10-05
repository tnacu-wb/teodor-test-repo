import { Box, Center } from '@chakra-ui/react';
import { Meta, StoryObj } from '@storybook/react';
import React from 'react';

import Accordion from './Accordion.component';

const meta: Meta<typeof Accordion> = {
  title: 'Accordion',
  component: Accordion,
  decorators: [
    (Story) => {
      return (
        <Center>
          <Box w={{ mobile: '100%', md: '31.25rem' }}>
            <Story />
          </Box>
        </Center>
      );
    },
  ],
  argTypes: {
    accordionItems: {
      description: 'Array of accordion items with title and content (string or React nodes)',
      control: 'object',
    },
    allowMultiple: {
      description: 'Allow multiple accordion sections to be open simultaneously',
      control: 'boolean',
    },
    allowToggle: {
      description: 'Allow the currently expanded section to be collapsed by clicking it again',
      control: 'boolean',
    },
    bgColor: {
      description: 'Background color for expanded accordion sections',
      control: 'text',
    },
    accordionOverwriteStyles: {
      description:
        'Custom Chakra UI style props for accordion parts (container, button, text, panel, item, icon)',
      control: 'object',
    },
  },
  parameters: {
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=1573%3A43776',
    },
    docs: {
      description: {
        component:
          'An accessible accordion component for displaying collapsible sections of content. Supports multiple open sections, custom styling, and React node content.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// eslint-disable-next-line @typescript-eslint/no-empty-function
const noop = () => {};

const defaultAccordionItems = [
  {
    title: 'Section 1 title',
    content:
      'Developing and Marketing Products and Services\n' +
      '\n' +
      'for raising brand awareness;\n' +
      'to understand you better as a customer by analysing your transactions and other information you provide to us or which we learn through your interactions with us;\n' +
      'for marketing (including creating profiles), competitions and promotions by post, email, text and push notification where permitted to do so by law;\n' +
      'we may use your data to provide personalised promotional offers to you where permitted to do so by law;\n' +
      'we may also use your data to provide you with personalised promotional offers on selected partner websites (for example, you might see an advertisement for our products on a partner site such as Facebook and Google);\n' +
      'we also share some of your information with marketing service and ad technology providers and digital marketing networks, such as Facebook, Google, Adobe and The Trade Desk, to present advertisements that might interest you.',
  },
  {
    title: 'Section 2 title',
    content:
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor\n' +
      '            incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud\n' +
      '            exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.',
  },
  {
    title: 'How do I know if my booking has been confirmed and what heppen if is so long?',
    content:
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor\n' +
      '            incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud\n' +
      '            exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.',
  },
  {
    title: 'extra long description',
    content:
      'Withdrawing consent \n' +
      '\n' +
      'Wherever we rely on your consent, you will always and at any time be able to withdraw that consent with effect for the future. We will continue to process your personal data for other purposes on a different lawful basis (other than consent) where that applies.\n' +
      '\n' +
      'Objecting to data processing including direct\n' +
      '\n' +
      'You have the right to object, on grounds relating to your particular situation, at any time to processing of your personal data which is conducted on the legitimate interest basis or which is necessary for the performance of a task carried out in the public interest or in the exercise of official authority vested in the controller, including profiling based on those provisions. In such case, we will no longer process your personal data unless we can demonstrates compelling legitimate grounds for the processing which overrides your interests, rights and freedoms or that the processing serves the establishment, exercise or defence of legal claims.\n' +
      '\n' +
      "Where personal data are processed for direct marketing purposes, you have an absolute right to opt-out of direct marketing, and any profiling we carry out for direct marketing, at any time and without any limitation. You can do this by clicking on the 'unsubscribe' link located in the footer of every marketing email or text or by contacting us (see the contact details provided below). Where you objected to our processing for direct marketing purposes, we will no longer process your personal data for such purposes.\n" +
      '\n' +
      'Where you have a relationship with another organisation, such as a social media platform like Facebook, we may ask them to send marketing to you, subject to your consent. If you object to receiving marketing from us we will stop marketing to you. However, please contact the organisation directly if you want to withdraw your consent to such organisation marketing to you.\n' +
      '\n' +
      'Other qualified rights\n' +
      '\n' +
      'You have the right to know whether or not we process information about you and to access that information.\n' +
      'You have the right to update, correct and complete any information we hold about you which is inaccurate or incomplete.\n' +
      'You have the right to obtain the personal data you provide to us for a contract or with your consent in a commonly used, structured, and machine-readable format, and to ask us to share (port) this personal data to another controller.\n' +
      'You have the right to ask that we erase or restrict (stop active) processing of your personal data.\n' +
      'These rights may be limited, for example if fulfilling your request would reveal personal data about another person or you ask us to erase information which we are required by law to Relevant exemptions are also included within the data protection laws that apply in the UK or Germany as the case may be. We will inform you of relevant exemptions we rely upon when responding to any request you make.\n' +
      '\n' +
      'To exercise any of these rights, you can get in touch with us using the details set out below.\n' +
      '\n' +
      'If you have concerns, you have the right to lodge a complaint with any data protection supervisory authority, in particular, the one of the country in which you are resident, work or in which your complaint arises. For the contact details of the Information Commissioner in the UK see www.ico.org.uk, for Information Commissioner in the Isle of Man see www.inforights.im and for the Office of Information Commissioner in Jersey see https://oicjersey.org . For the contact details of the competent German supervisory authority see https://www.bfdi.bund.de/DE/Infothek/Anschriften_Links/anschriften_links-node.html. Details of all EU supervisory authorities can be found at http://ec.europa.eu/newsroom/article29/item-detail.cfm?item_id=612080 ',
  },
];

// --- Basic Usage ---

export const Default: Story = {
  args: {
    accordionItems: defaultAccordionItems,
    allowMultiple: true,
  },
};

// --- Configuration ---

export const SingleSectionAtATime: Story = {
  args: {
    accordionItems: defaultAccordionItems,
    allowMultiple: false,
  },
};

export const WithCustomBackgroundColor: Story = {
  args: {
    accordionItems: defaultAccordionItems,
    allowMultiple: true,
    bgColor: 'lightBlue50',
  },
};

export const WithReactNodeContent: Story = {
  args: {
    accordionItems: [
      {
        title: <p style={{ margin: 0, fontWeight: 'bold' }}>React Node Title</p>,
        content: (
          <em style={{ color: 'var(--chakra-colors-blue500)' }}>Custom React content component</em>
        ),
      },
      {
        title: 'Mixed Content Section',
        content: (
          <div>
            <p>This section has React node content</p>
            <strong>With multiple elements</strong>
          </div>
        ),
      },
    ],
  },
};

export const WithToggleEnabled: Story = {
  args: {
    accordionItems: defaultAccordionItems,
    allowMultiple: true,
    allowToggle: true,
  },
};

export const WithCustomStyling: Story = {
  args: {
    accordionItems: defaultAccordionItems,
    accordionOverwriteStyles: {
      button: {
        p: '1rem 0.5rem',
        borderRadius: '4px',
        _expanded: {
          bgColor: 'lightGrey2',
        },
      },
      text: {
        fontSize: 'lg',
        fontWeight: 700,
      },
      panel: {
        p: '1rem',
      },
    },
  },
};

export const WithCallbacks: Story = {
  args: {
    accordionItems: [
      {
        title: 'Interactive Section 1',
        content: 'Content for section 1',
        onToggleSection: noop,
      },
      {
        title: 'Interactive Section 2',
        content: 'Content for section 2',
        onToggleSection: noop,
      },
      {
        title: 'Interactive Section 3',
        content: 'Content for section 3',
        onToggleSection: noop,
      },
    ],
  },
};

// --- Edge Cases ---

export const SingleItem: Story = {
  args: {
    accordionItems: [
      {
        title: 'Single Section',
        content: 'This accordion contains only one section that can be toggled open and closed.',
      },
    ],
  },
};

export const ShortContent: Story = {
  args: {
    accordionItems: [
      {
        title: 'Section A',
        content: 'Brief content',
      },
      {
        title: 'Section B',
        content: 'Short text here',
      },
      {
        title: 'Section C',
        content: 'Minimal content',
      },
    ],
  },
};
