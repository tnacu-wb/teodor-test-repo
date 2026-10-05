import { Box } from '@chakra-ui/react';
import { ReactNode } from 'react';

import { formatDataTestId } from '../../utils/formatters';

interface Props {
  children: ReactNode;
  dataTestId?: string;
}

export default function Section({ children, dataTestId }: Readonly<Props>) {
  return (
    <Box
      as="section"
      data-testid={formatDataTestId(dataTestId, 'Section')}
      borderBottom="1px solid var(--chakra-colors-lightGrey4)"
    >
      {children}
    </Box>
  );
}
