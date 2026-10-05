import { Box } from '@chakra-ui/react';
import { Info, Notification } from '@whitbread-eos/atoms';
import { formatDataTestId, renderSanitizedHtml, useSemanticTypography } from '@whitbread-eos/utils';

export interface Props {
  infoMessages: string[] | undefined;
  prefixDataTestId?: string;
}

export default function BookingSummaryInfoMessages({
  infoMessages = [],
  prefixDataTestId,
}: Readonly<Props>) {
  const baseDataTestId = formatDataTestId(prefixDataTestId, 'HotelInformation');
  const baseDataTestMessageId = formatDataTestId(baseDataTestId, 'InfoMessages');
  const getTypographyProps = useSemanticTypography();

  return (
    <Box mt="sm" data-testid={formatDataTestId(baseDataTestId, 'InfoMessages')}>
      {infoMessages?.map((message: string, index: number) => {
        if (message.length) {
          return (
            <Box
              mt="md"
              key={message}
              data-testid={formatDataTestId(baseDataTestMessageId, `${index}`)}
            >
              <Notification
                maxWidth="full"
                variant="info"
                status="info"
                description={
                  <Box
                    className="formatLinks"
                    {...getTypographyProps({}, infoMessageSemanticTypography)}
                  >
                    {renderSanitizedHtml(message)}
                  </Box>
                }
                svg={<Info />}
              />
            </Box>
          );
        }
      })}
    </Box>
  );
}

const infoMessageSemanticTypography = {
  textStyle: 'body-s-regular',
};
