import { Box, BoxProps } from '@chakra-ui/react';
import { renderSanitizedHtml } from '@whitbread-eos/utils';

interface Props extends BoxProps {
  html: string;
}

export default function DescriptionBox({ html, ...otherBoxProps }: Readonly<Props>) {
  return (
    <Box className="formatLinks" {...otherBoxProps}>
      {renderSanitizedHtml(html)}
    </Box>
  );
}
