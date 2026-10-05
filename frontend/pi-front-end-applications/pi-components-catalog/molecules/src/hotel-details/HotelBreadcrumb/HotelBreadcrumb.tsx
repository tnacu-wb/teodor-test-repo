import { Box, Text, BoxProps } from '@chakra-ui/react';
import { Breadcrumb as BreadcrumbType, BreadcrumbItem } from '@whitbread-eos/api';
import { Breadcrumb } from '@whitbread-eos/atoms';
import { useStaticHotelInformation } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useEffect, useState } from 'react';

interface Props {
  openInNewTab?: boolean;
}

const HotelBreadcrumb = ({ openInNewTab }: Readonly<Props>) => {
  const { breadcrumb, isLoading, isError, error } = useStaticHotelInformation();
  const { t } = useTranslation(['common']);
  const [breadcrumbList, setBreadcrumbList] = useState<BreadcrumbItem[]>([]);

  useEffect(() => {
    if (!breadcrumb) {
      return;
    }

    const breadcrumbList: BreadcrumbItem[] = breadcrumb
      ? breadcrumb.map((breadcrumbData: BreadcrumbType, index: number) => ({
          name: breadcrumbData.title,
          url: index === breadcrumb.length - 1 ? '#' : breadcrumbData.link,
          isCurrentPage: index === breadcrumb.length - 1,
        }))
      : [];

    setBreadcrumbList(breadcrumbList);
  }, [breadcrumb]);

  if (isLoading) {
    return <Text>{t('searchresults.list.hotel.loading')}</Text>;
  }

  if (isError) {
    return <Text>{(error as Error)?.message}</Text>;
  }

  return breadcrumbList.length ? (
    <Box data-testid="breadcrumbs-hdp" {...breadcrumbBoxStyles}>
      <Breadcrumb
        items={breadcrumbList}
        openInNewTab={openInNewTab}
        listProps={{
          flexWrap: 'wrap',
        }}
      />
    </Box>
  ) : null;
};

const breadcrumbBoxStyles = {
  mt: 'xl',
  mb: 'xl',
} as BoxProps;

export { HotelBreadcrumb };
