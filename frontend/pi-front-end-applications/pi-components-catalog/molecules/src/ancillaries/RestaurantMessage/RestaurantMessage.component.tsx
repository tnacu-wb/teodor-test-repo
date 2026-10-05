import type { TextProps, BoxProps } from '@chakra-ui/react';
import { Box, Text } from '@chakra-ui/react';
import { formatDataTestId } from '@whitbread-eos/utils';

export interface Props {
  messageTitle: string;
  messageDescription: string | undefined;
  prefixDataTestId?: string;
}

export default function RestaurantMessage({
  messageDescription,
  messageTitle,
  prefixDataTestId,
}: Readonly<Props>) {
  const baseDataTestId = formatDataTestId(prefixDataTestId, 'RestaurantMessage');
  const titleId = formatDataTestId(baseDataTestId, 'Title');
  const descriptionId = formatDataTestId(baseDataTestId, 'Description');
  return (
    <Box
      {...wrapperStyle}
      role="status"
      aria-live="polite"
      aria-atomic="true"
      aria-labelledby={titleId}
      aria-describedby={messageDescription ? descriptionId : undefined}
      data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}
    >
      <Text id={titleId} {...titleStyle} data-testid={formatDataTestId(baseDataTestId, 'Title')}>
        {messageTitle}
      </Text>
      {messageDescription && (
        <Text
          id={descriptionId}
          {...bodyStyle}
          data-testid={formatDataTestId(baseDataTestId, 'Description')}
        >
          {messageDescription}
        </Text>
      )}
    </Box>
  );
}

// replace typographic styles with semantic tokens, when available in figma design
const titleStyle = {
  as: 'h2',
  fontWeight: 'semibold',
  color: 'darkGrey1',
  letterSpacing: '0',
  fontSize: {
    base: 'xl',
  },
  lineHeight: {
    base: '1.2',
  },
} as TextProps;

const bodyStyle = {
  mt: 'sm',
  color: 'darkGrey2',
  letterSpacing: '0',
  fontSize: {
    base: 'sm',
  },
  lineHeight: {
    base: '1.2',
  },
  fontWeight: 'normal',
} as TextProps;

const wrapperStyle = {
  bg: 'white',
  p: '4',
  borderRadius: 'md',
  borderWidth: '1px',
  borderStyle: 'solid',
  borderColor: 'tertiary',
} as BoxProps;
