'use client';

import { Button, TableRow, TableCell } from '@whitbread-eos/atoms/ui';
import { useState, useEffect } from 'react';

import { LoadMoreRowsAndToken } from '~components/innBusiness/DataTable';

export type LoadMoreProps = {
  getExtraRows: (pageToken: string, clickCount: number) => Promise<LoadMoreRowsAndToken>;
  loadMoreLabel?: string;
  testId: string;
};

export function LoadMore({ getExtraRows, loadMoreLabel, testId }: LoadMoreProps) {
  const [rows, setRows] = useState<React.ReactNode[]>([]);
  const [token, setToken] = useState<string>('');
  const [clickCount, setClickCount] = useState<number>(0);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  useEffect(() => {
    const getPageToken = async () => {
      const { pageToken } = await getExtraRows('', 0);
      setToken(pageToken);
    };
    getPageToken();
  }, []);

  const onClickHandler = async () => {
    if (isLoading) return;
    setIsLoading(true);
    try {
      const data = await getExtraRows(token, clickCount);

      setRows((prevRows) => [...prevRows, ...(data.result as React.ReactNode[])]);
      setClickCount(clickCount + 1);

      setToken(data.pageToken);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      {rows}
      {token && (
        <TableRow className={noHoverStyle}>
          <TableCell colSpan={4} className={paginationCellStyle}>
            <div className={containerStyle}>
              <Button
                variant="LoadMoreButton"
                size="LoadMoreButton"
                data-testid={`${testId}-LoadMoreButton`}
                className={buttonStyle}
                onClick={onClickHandler}
                disabled={isLoading}
              >
                <span data-testid={`${testId}-LoadMoreButton-label`} className={textStyle}>
                  {loadMoreLabel}
                </span>
              </Button>
            </div>
          </TableCell>
        </TableRow>
      )}
    </>
  );
}
const containerStyle =
  'flex justify-center items-center w-full p-4 mobile:px-6 border-t border-lightGrey3';
const buttonStyle =
  'w-72 mobile:w-full mobile:h-full p-2 border border-secondaryColor rounded gap-4';
const textStyle = 'text-secondaryColor font-semibold text-lg leading-[22px]';
const noHoverStyle = 'hover:bg-transparent';
const paginationCellStyle = 'h-[60px] px-0 border-t-0 py-0';
