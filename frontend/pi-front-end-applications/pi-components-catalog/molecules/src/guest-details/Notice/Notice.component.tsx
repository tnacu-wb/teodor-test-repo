import { Box, BoxProps, Link, StyleProps } from '@chakra-ui/react';
import { FieldsType, Info, Notification } from '@whitbread-eos/atoms';
import { renderSanitizedHtml } from '@whitbread-eos/utils';

interface Props {
  formField?: FieldsType;
  description: string;
  linkLabel?: string;
  linkPath?: string;
  styles?: StyleProps;
}

export default function Notice({
  formField,
  description,
  styles,
  linkLabel,
  linkPath,
}: Readonly<Props>) {
  const notificationDescription = () => {
    return (
      <Box data-testid={formField?.testid}>
        <Box {...textStyle} className="formatLinks">
          {renderSanitizedHtml(
            formField?.props?.description ? formField.props.description : description
          )}
        </Box>
        {(linkPath || formField?.props?.linkPath) && (
          <Link
            ml="2px"
            textDecoration="underline"
            color={'darkGrey3'}
            isExternal
            href={linkPath ?? formField?.props?.linkPath}
          >
            {linkLabel ?? formField?.props?.linkLabel}
          </Link>
        )}
      </Box>
    );
  };

  return (
    <Box {...styles}>
      <Notification
        status="info"
        svg={<Info />}
        variant={formField?.props?.variant || 'infoGrey'}
        maxW="full"
        description={notificationDescription()}
      />
    </Box>
  );
}

const textStyle = {
  d: 'inline-block',
  mr: 'xs',
} as BoxProps;
