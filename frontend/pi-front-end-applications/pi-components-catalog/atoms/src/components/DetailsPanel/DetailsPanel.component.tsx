import { Box, Grid, Text, StyleProps } from '@chakra-ui/react';
import { formatDataTestId } from '@whitbread-eos/utils';
import { Fragment } from 'react';

interface DetailsPanelItemProp {
  data: {
    title: string;
    rows: {
      key: string;
      value: string;
    }[];
  };
  stylesObject: {
    wrapper: StyleProps;
    title: StyleProps;
    rowKey: StyleProps;
    rowValue: StyleProps;
  };
}

export default function DetailsPanel({ data, stylesObject }: Readonly<DetailsPanelItemProp>) {
  const baseDataTestId = 'BookingDetailsTable';

  return (
    <Box {...stylesObject?.wrapper} data-testid={formatDataTestId(baseDataTestId, 'Container')}>
      <Text {...stylesObject?.title} data-testid={data?.title}>
        {data?.title}
      </Text>

      <Grid templateColumns="1fr 2fr" gap={2}>
        {data?.rows?.map((row) => {
          return (
            <Fragment key={`row-${row.key}`}>
              <Text {...stylesObject?.rowKey} data-testid={row.key}>
                {row.key}
              </Text>
              <Text {...stylesObject?.rowValue}>{row.value}</Text>
            </Fragment>
          );
        })}
      </Grid>
    </Box>
  );
}
