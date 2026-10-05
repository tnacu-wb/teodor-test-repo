import {
  Breadcrumb as BreadcrumbChakra,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbProps as BreadcrumbPropsChakra,
  Link,
} from '@chakra-ui/react';
import { formatAssetsUrl, useSemanticTypography } from '@whitbread-eos/utils';
import React from 'react';

interface BreadcrumbProps extends BreadcrumbPropsChakra {
  size?: string;
  variant?: string;
  linkComponent?: React.ElementType;
  items: Array<BreadcrumbItemProps>;
  openInNewTab?: boolean;
}

export interface BreadcrumbItemProps {
  url: string;
  name: string;
  isCurrentPage?: boolean;
}

//passing openInNewTab as props in order to redirect breadcrumb link on new tab for ccui channel only

export default function Breadcrumb({
  linkComponent: CustomLinkComponent,
  openInNewTab = false,
  ...props
}: Readonly<BreadcrumbProps>) {
  const getTypographyProps = useSemanticTypography();
  const breadcrumbLinkSemanticTypography = { textStyle: 'body-xs-regular' };

  return (
    <BreadcrumbChakra {...props}>
      {props.items?.map((item) => (
        <BreadcrumbItem key={item.name} isCurrentPage={item.isCurrentPage}>
          {CustomLinkComponent ? (
            <CustomLinkComponent href={item.url} passHref>
              <BreadcrumbLink {...getTypographyProps({}, breadcrumbLinkSemanticTypography)}>
                {item.name}
              </BreadcrumbLink>
            </CustomLinkComponent>
          ) : (
            <BreadcrumbLink
              as={Link}
              isExternal={openInNewTab}
              href={openInNewTab ? formatAssetsUrl(item.url) : item.url}
              {...getTypographyProps({}, breadcrumbLinkSemanticTypography)}
            >
              {item.name}
            </BreadcrumbLink>
          )}
        </BreadcrumbItem>
      ))}
    </BreadcrumbChakra>
  );
}
