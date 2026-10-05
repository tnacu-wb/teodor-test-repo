import { Box, Grid, GridItem, GridProps, HStack } from '@chakra-ui/react';
import {
  Children,
  ReactElement,
  ReactNode,
  ReactPortal,
  JSXElementConstructor,
  PromiseLikeOfReactNode,
} from 'react';

export interface ListProps extends GridProps {
  children: ReactNode;
  cols?: number;
  icon?: ReactNode;
}

export default function List(props: Readonly<ListProps>) {
  const listItems = Children.toArray(props.children);
  const renderItem = (
    listItem:
      | string
      | number
      | ReactElement<any, string | JSXElementConstructor<any>>
      | Iterable<ReactNode>
      | ReactPortal
      | PromiseLikeOfReactNode,
    idx: number
  ): ReactNode => {
    return (
      <GridItem key={idx}>
        <HStack spacing="1rem">
          {props.icon}
          <Box>{listItem as ReactNode}</Box>
        </HStack>
      </GridItem>
    );
  };

  return (
    <Grid templateColumns={`repeat(${props.cols}, 1fr)`} gap={4} {...props}>
      {listItems.map(renderItem)}
    </Grid>
  );
}

List.defaultProps = {
  cols: 1,
};
