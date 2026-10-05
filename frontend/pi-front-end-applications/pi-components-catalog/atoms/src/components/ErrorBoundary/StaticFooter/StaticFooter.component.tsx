import { Box, Flex, Text } from '@chakra-ui/react';
import { formatDataTestId, renderSanitizedHtml } from '@whitbread-eos/utils';
import { format } from 'date-fns';

export default function StaticFooterComponent() {
  const baseDataTestId = 'Footer';
  const footerWrapperStyles = () => {
    return {
      w: 'full',
      borderTop: '1px solid var(--chakra-colors-lightGrey1)',
      pt: { mobile: '0', md: 'lg', lg: 'lg', xl: '3xl' },
      px: { mobile: 'md', md: 'md', lg: 'xl', xl: '5xl' },
      pb: { lg: 'lg', xl: '3xl' },
    };
  };

  const copyrightData = `&copy; ${format(new Date(), 'yyyy')} Premier Inn`;

  return (
    <Box
      maxW="var(--chakra-space-breakpoint-xl)"
      {...footerWrapperStyles()}
      data-testid={formatDataTestId(baseDataTestId, 'Wrapper-Error')}
    >
      <Flex
        data-testid={formatDataTestId(baseDataTestId, 'CopyrightSection')}
        {...copyrightSectionStyles}
      >
        <Text {...copyrightStyles} className="formatLinks">
          {renderSanitizedHtml(copyrightData)}
        </Text>
      </Flex>
    </Box>
  );
}

const copyrightStyles = {
  fontSize: 'sm',
  fontWeight: 'normal',
  lineHeight: 1,
  color: 'darkGrey1',
};

const copyrightSectionStyles = {
  justifyContent: 'space-between',
  alignItems: 'center',
  w: 'full',
};
