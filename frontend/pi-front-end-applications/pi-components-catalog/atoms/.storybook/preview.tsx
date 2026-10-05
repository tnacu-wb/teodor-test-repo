import { ChakraProvider } from '@chakra-ui/react';

import theme from '../src/theme';
import Fonts from '../src/theme/components/Fonts';
// @ts-expect-error -- Storybook needs this generated global side-effect CSS import
import './shadcn-storybook.css';

const defaultDesignUrl = 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0';

const preview = {
  parameters: {
    design: {
      type: 'figma',
      url: defaultDesignUrl,
    },
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/,
      },
      sort: 'requiredFirst',
    },
    actions: { argTypesRegex: '^on[A-Z].*' },
  },
  decorators: [
    (Story: any) => {
      return (
        <ChakraProvider theme={theme}>
          <Fonts />
          <Story />
        </ChakraProvider>
      );
    },
  ],
};

export default preview;
