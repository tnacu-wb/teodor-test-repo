import { Box, BoxProps, Flex } from '@chakra-ui/react';
import { formatDataTestId, renderSanitizedHtml } from '@whitbread-eos/utils';
import React from 'react';

import { Info } from '../../assets/icons';
import Icon from '../Icon';

interface Props extends BoxProps {
  informationBox: string;
}

export default function DonationsInfoBox({ informationBox }: Readonly<Props>) {
  const baseDataTestId = 'Donation';

  return (
    <Flex {...donationInfoBoxStyle} data-testid={formatDataTestId(baseDataTestId, 'InfoBox')}>
      <Box pr="sm" data-testid={formatDataTestId(baseDataTestId, 'InfoBoxIcon')}>
        <Icon svg={<Info />} />
      </Box>
      <Box
        {...donationInfoBoxTextStyle}
        data-testid={formatDataTestId(baseDataTestId, 'InfoBoxText')}
        className="formatLinks"
      >
        {renderSanitizedHtml(informationBox)}
      </Box>
    </Flex>
  );
}

const donationInfoBoxStyle = {
  border: '1px solid var(--chakra-colors-lightGrey2)',
  borderRadius: 'var(--chakra-space-xs)',
  p: 'md',
  w: { mobile: 'full', md: '45rem', lg: '50.5rem', xl: '54rem' },
  mt: 'xl',
};

const donationInfoBoxTextStyle = {
  fontSize: 'sm',
  fontWeight: 'normal',
};
