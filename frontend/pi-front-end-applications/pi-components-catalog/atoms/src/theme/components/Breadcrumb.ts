import type { ComponentMultiStyleConfig } from '@chakra-ui/theme';

const Breadcrumb: ComponentMultiStyleConfig = {
  parts: ['item', 'link', 'container', 'separator'],
  baseStyle: {
    container: {
      fontSize: 'xxs',
    },
    item: {
      _focus: {
        outline: 'none',
      },
    },
    link: {
      _hover: {
        textDecoration: 'initial',
      },
      _focus: {
        outline: 'none',
        boxShadow: 'none',
      },
    },
  },
  sizes: {
    sm: {
      separator: {
        marginLeft: '1',
        marginRight: '1',
      },
    },
  },
  variants: {
    mobile: {
      container: {
        width: 'var(--chakra-space-breakpoint-m)',
      },
    },
  },
};

export default Breadcrumb;
