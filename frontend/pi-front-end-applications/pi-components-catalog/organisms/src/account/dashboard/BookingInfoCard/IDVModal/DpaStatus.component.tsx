import { Box, BoxProps, Divider, Flex, Heading, HeadingProps } from '@chakra-ui/react';
import { Button } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';

import { IdvKV, IdvRadio } from './components';

export interface DpaStatusType {
  dpaPassed: boolean;
  dpaOverride: boolean;
  eCnpPassword: string;
}

type IDVCardPropsType = {
  dpaStatus: DpaStatusType;
  onDpaPassed: (dpaPassed: boolean) => void;
  onDpaOverride: (dpaOverride: boolean) => void;
  onReuseDetails?: () => void;
  t: (id: string) => string;
  baseDataTestId: string;
};

const DpaStatus = ({
  dpaStatus,
  onDpaPassed,
  onDpaOverride,
  onReuseDetails,
  t,
  baseDataTestId,
}: IDVCardPropsType) => {
  return (
    <Box {...cardStyle}>
      <Flex flexDir="column">
        <Heading {...headingStyle} data-testid={formatDataTestId(baseDataTestId, 'Heading')}>
          {t('ccui.idv.dpaStatus.heading')}
        </Heading>
        <Divider mt="1rem" mb="2rem"></Divider>
        <IdvRadio
          value={dpaStatus.dpaPassed}
          onChange={onDpaPassed}
          dataTestId={formatDataTestId(baseDataTestId, 'DpaPassed')}
          text={t('ccui.idv.dpaStatus.dpaPassed')}
          options={[
            { text: t('ccui.idv.dpaStatus.dpaPassedYes'), value: true },
            { text: t('ccui.idv.dpaStatus.dpaPassedNo'), value: false },
          ]}
        ></IdvRadio>
        <Divider my="1rem"></Divider>
        <IdvRadio
          value={dpaStatus.dpaOverride}
          onChange={onDpaOverride}
          dataTestId={formatDataTestId(baseDataTestId, 'DpaOverride')}
          text={t('ccui.idv.dpaStatus.dpaOverride')}
          options={[
            { text: t('ccui.idv.dpaStatus.dpaOverrideYes'), value: true },
            { text: t('ccui.idv.dpaStatus.dpaOverrideNo'), value: false },
          ]}
        ></IdvRadio>
        <Divider my="1rem"></Divider>
        <IdvKV
          text={t('ccui.idv.dpaStatus.eCnpPassword')}
          value={dpaStatus.eCnpPassword}
          dataTestId={formatDataTestId(baseDataTestId, 'ECnpPassword')}
        ></IdvKV>
        {onReuseDetails && (
          <Button
            size="md"
            variant="primary"
            onClick={onReuseDetails}
            disabled={!dpaStatus.dpaPassed}
          >
            {t('ccui.idv.dpaStatus.reuseDetails')}
          </Button>
        )}
      </Flex>
    </Box>
  );
};

const cardStyle = {
  w: { mobile: 'full', xs: 'full', sm: 'full', md: 'full', lg: '311px', xl: '333px' },
  border: '1px solid var(--chakra-colors-lightGrey4)',
  p: '1.5rem',
} as BoxProps;

const headingStyle = {
  color: 'darkGrey2',
  fontSize: '1.25rem',
  lineHeight: '1.5rem',
  fontWeight: '700',
  fontFamily: 'Proxima Nova Sans, helvetica, arial, sans-serif',
} as HeadingProps;

export default DpaStatus;
