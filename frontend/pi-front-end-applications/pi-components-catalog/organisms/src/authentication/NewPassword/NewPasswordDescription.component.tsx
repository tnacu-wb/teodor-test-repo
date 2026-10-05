import type { FlexProps, ListProps, TextProps } from '@chakra-ui/react';
import { Flex, ListItem, Text, UnorderedList } from '@chakra-ui/react';
import { ResetPasswordLabels } from '@whitbread-eos/api';

export interface Props {
  labels: ResetPasswordLabels;
}

export default function NewPasswordDescription({ labels }: Readonly<Props>) {
  return (
    <Flex {...containerStyles}>
      <Text {...descStyles}>{labels?.resetPassword?.criteriaDescription}</Text>
      <UnorderedList {...listStyles}>
        <ListItem>{labels?.resetPassword?.passwordRequirementsMin}</ListItem>
        <ListItem>{labels?.resetPassword?.passwordRequirementsIdentical}</ListItem>
        <ListItem>{labels?.resetPassword?.passwordRequirementsAllowed}</ListItem>
      </UnorderedList>
    </Flex>
  );
}

const containerStyles = {
  marginY: '2xl',
  flexDirection: 'column',
} as FlexProps;

const descStyles = {
  color: 'darkGrey1',
  fontSize: 'md',
  fontWeight: 'normal',
} as TextProps;

const listStyles = {
  mt: 'md',
  lineHeight: '3',
  w: 'full',
  fontSize: 'sm',
  fontWeight: 500,
  gap: '.5rem',
} as ListProps;
