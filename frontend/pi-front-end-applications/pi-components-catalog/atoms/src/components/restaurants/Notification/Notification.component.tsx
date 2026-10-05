import { Box, Flex, CloseButton, BoxProps, useMultiStyleConfig } from '@chakra-ui/react';
import type { CloseButtonProps } from '@chakra-ui/react';
import { formatDataTestId, sanitize } from '@whitbread-eos/utils';
import type { ReactElement } from 'react';

import { Dismiss } from '../../../assets/icons';
import Icon from '../../Icon';

export type NotificationStatus = 'info' | 'warning' | 'success' | 'error' | undefined;
interface Props extends Omit<BoxProps, 'onClick'> {
  status: NotificationStatus;
  title?: string;
  description: string | string[] | ReactElement;
  onClick?: () => void;
  isClosed?: boolean;
  variant: string;
  svg: ReactElement;
  showCloseButton?: boolean;
  prefixDataTestId?: string;
  isInnerHTML?: boolean;
  wrapperStyles?: BoxProps;
}

export default function Notification(props: Props) {
  const {
    isInnerHTML,
    description,
    title,
    prefixDataTestId,
    wrapperStyles,
    isClosed,
    status,
    variant,
    svg,
    onClick,
    showCloseButton,
    ...restProps
  } = props;

  const styles = useMultiStyleConfig('Alert', { status, variant });

  const containerStyles = {
    width: '100%',
    display: 'flex',
    alignItems: 'center',
    position: 'relative' as const,
    overflow: 'hidden',
    ...styles.container,
  };

  const titleStyles = {
    ...styles.title,
    maxWidth: 'full',
    mr: 0,
  };

  const descriptionStyles = {
    display: 'inline',
    ...styles.description,
  };

  const closeIconStyle = {
    position: 'absolute',
    right: 'sm',
    top: 'sm',
  } as CloseButtonProps;

  return (
    <>
      {!isClosed && (title || description) && (
        <Box
          role="alert"
          p="md"
          sx={containerStyles}
          onClick={onClick}
          data-testid={formatDataTestId(prefixDataTestId, 'Alert')}
          {...restProps}
          {...wrapperStyles}
        >
          <Icon svg={svg} />
          <Flex
            ml="8px"
            direction="column"
            data-testid={formatDataTestId(prefixDataTestId, 'AlertColumn')}
          >
            {isInnerHTML && typeof title === 'string' ? (
              <Box sx={titleStyles} data-testid={formatDataTestId(prefixDataTestId, 'AlertTitle')}>
                {sanitize(title)}
              </Box>
            ) : (
              <Box sx={titleStyles} data-testid={formatDataTestId(prefixDataTestId, 'AlertTitle')}>
                {title}
              </Box>
            )}
            {isInnerHTML && typeof description === 'string' ? (
              <Box
                sx={descriptionStyles}
                data-testid={formatDataTestId(prefixDataTestId, 'AlertDescription')}
              >
                {sanitize(description)}
              </Box>
            ) : Array.isArray(description) ? (
              description.map((descriptionText: string, index) => {
                return (
                  <Box
                    key={index}
                    sx={descriptionStyles}
                    data-testid={formatDataTestId(prefixDataTestId, `AlertDescription-${index}`)}
                  >
                    {descriptionText}
                  </Box>
                );
              })
            ) : (
              <Box
                sx={descriptionStyles}
                data-testid={formatDataTestId(prefixDataTestId, 'AlertDescription')}
              >
                {description}
              </Box>
            )}
          </Flex>
          {onClick && showCloseButton && (
            <CloseButton {...closeIconStyle}>
              <Icon svg={<Dismiss transform="scale(1.1)" />} />
            </CloseButton>
          )}
        </Box>
      )}
    </>
  );
}
