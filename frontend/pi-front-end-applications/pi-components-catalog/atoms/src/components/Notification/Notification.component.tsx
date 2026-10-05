import type { BoxProps, CloseButtonProps } from '@chakra-ui/react';
import { Box, CloseButton, Flex, useMultiStyleConfig } from '@chakra-ui/react';
import { formatDataTestId, renderSanitizedHtml } from '@whitbread-eos/utils';
import type { ReactElement } from 'react';

import { Dismiss } from '../../assets/icons';
import { semanticTextStyles } from '../../theme/adapters/semanticTypography';
import Icon from '../Icon';

export type NotificationStatus = 'info' | 'warning' | 'success' | 'error' | undefined;
type SemanticTextStyleKey = keyof typeof semanticTextStyles;

interface Props extends Omit<BoxProps, 'onClick'> {
  status: NotificationStatus;
  title?: string;
  description: string | string[] | ReactElement | undefined;
  onClick?: () => void;
  isClosed?: boolean;
  variant: string;
  svg: ReactElement;
  showCloseButton?: boolean;
  prefixDataTestId?: string;
  isInnerHTML?: boolean;
  wrapperStyles?: BoxProps;
  descriptionTextStyle?: SemanticTextStyleKey;
  descriptionStrongTextStyle?: SemanticTextStyleKey;
}

export default function Notification(props: Readonly<Props>) {
  const {
    isInnerHTML,
    description,
    title,
    prefixDataTestId,
    showCloseButton,
    wrapperStyles,
    isClosed,
    status,
    variant,
    svg,
    onClick,
    descriptionTextStyle,
    descriptionStrongTextStyle,
    sx,
    ...restProps
  } = props;

  const styles = useMultiStyleConfig('Alert', { status, variant });

  const liveRegionRole = status === 'error' || status === 'warning' ? 'alert' : 'status';

  const containerStyles = {
    width: '100%',
    display: 'flex',
    alignItems: 'center',
    position: 'relative' as const,
    overflow: 'hidden',
    ...styles.container,
    py: '1rem',
    ...wrapperStyles,
  };

  const titleSemanticStyles =
    typeof restProps.textStyle === 'string'
      ? semanticTextStyles[restProps.textStyle as string]
      : {};

  const titleStyles = {
    ...styles.title,
    ...titleSemanticStyles,
    maxWidth: 'full',
    mr: 0,
    ...(restProps.fontWeight ? { fontWeight: restProps.fontWeight } : {}),
    '& a:hover': {
      textDecoration: 'underline',
    },
  };

  const descriptionSemanticStyles =
    typeof descriptionTextStyle === 'string' ? semanticTextStyles[descriptionTextStyle] : {};

  const descriptionStrongSemanticStyles =
    typeof descriptionStrongTextStyle === 'string'
      ? semanticTextStyles[descriptionStrongTextStyle]
      : {};

  const descriptionStyles = {
    display: 'inline',
    ...styles.description,
    ...descriptionSemanticStyles,
    '& a:hover': {
      textDecoration: 'underline',
    },
    '& strong': {
      ...descriptionStrongSemanticStyles,
    },
  };

  const closeIconStyle = {
    position: 'absolute',
    right: 'sm',
    top: 'sm',
  } as CloseButtonProps;

  const arrayDescription = Array.isArray(description);

  return (
    <>
      {!isClosed && (title || description) && (
        <Box
          role={liveRegionRole}
          aria-atomic="true"
          p="md"
          {...restProps}
          sx={{ ...containerStyles, ...sx }}
          onClick={onClick}
          data-testid={formatDataTestId(prefixDataTestId, 'Alert')}
        >
          <Icon style={{ alignSelf: 'flex-start' }} svg={svg} />
          <Flex direction="column" data-testid={formatDataTestId(prefixDataTestId, 'AlertColumn')}>
            {isInnerHTML && typeof title === 'string' ? (
              <Box
                className="formatLinks"
                sx={titleStyles}
                data-testid={formatDataTestId(prefixDataTestId, 'AlertTitle')}
              >
                {renderSanitizedHtml(title)}
              </Box>
            ) : (
              <Box sx={titleStyles} data-testid={formatDataTestId(prefixDataTestId, 'AlertTitle')}>
                {title}
              </Box>
            )}
            {isInnerHTML && typeof description === 'string' ? (
              <Box
                className="formatLinks"
                sx={descriptionStyles}
                data-testid={formatDataTestId(prefixDataTestId, 'AlertDescription')}
              >
                {renderSanitizedHtml(description)}
              </Box>
            ) : (
              displayAlertDescriptionComponent()
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

  function displayAlertDescriptionComponent() {
    return arrayDescription ? (
      (description as string[])?.map((descriptionText: string, index) => {
        return (
          <Box
            key={descriptionText}
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
    );
  }
}
