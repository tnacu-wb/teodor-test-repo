import React, { ReactNode } from 'react';
import InfiniteScroll from 'react-infinite-scroll-component';

export interface Props {
  children: ReactNode;
  loader?: React.ReactElement;
  dataLength: number;
  hasMore: boolean;
  next: () => void;
  scrollThreshold?: number;
  style?: { overflow: string };
  endMessage?: React.ReactElement;
  scrollableTarget?: string;
}

export default function InfiniteScroller({
  children,
  loader,
  dataLength,
  hasMore,
  next,
  scrollThreshold,
  style,
  endMessage,
  scrollableTarget,
}: Readonly<Props>) {
  return (
    <InfiniteScroll
      loader={loader}
      dataLength={dataLength}
      hasMore={hasMore}
      next={next}
      scrollThreshold={scrollThreshold}
      style={style}
      endMessage={endMessage}
      scrollableTarget={scrollableTarget}
    >
      {children}
    </InfiniteScroll>
  );
}
