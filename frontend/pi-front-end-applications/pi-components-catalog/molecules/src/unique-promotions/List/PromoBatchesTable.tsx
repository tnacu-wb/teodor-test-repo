import { Box, Container, useToast } from '@chakra-ui/react';
import { useReactTable, getCoreRowModel, SortingState } from '@tanstack/react-table';
import { LoadingSpinner } from '@whitbread-eos/atoms';
import { useCustomLocale, usePromoTranslation } from '@whitbread-eos/utils';
import { useRouter } from 'next/router';
import React, { useState, useRef, useEffect } from 'react';

import { PromoNotes } from '../Notes';
import PasswordModal from '../PasswordModal';
import { downloadFileFromUrl } from '../PasswordModal/common';
import { usePromoBatches } from '../hooks';
import { useBatchById, usePromoBatchAsDownload } from '../hooks/use-batch-by-id';
import {
  HeaderSection,
  TableSection,
  PaginationSection,
  usePromoBatchColumns,
  getNextPageIndex,
} from './PromoBatchesTableComponents';
import { styles } from './styles';
import { PromoBatch } from './types';
import { PromoBatchesTableContext } from './usePromoBatchesTableContext';

const NO_OF_RECORDS_PAGE_SIZE = 5;

const PromoBatchesTable = () => {
  const baseDataTestId = 'PromoBatchesTable';
  const [sorting, setSorting] = useState<SortingState>([{ id: 'created', desc: true }]);
  const [pageIndex, setPageIndex] = useState(0);
  const [loadingTransition, setLoadingTransition] = useState(false);
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [batchId, setBatchId] = useState<string | null>(null);
  const previousDataRef = useRef<PromoBatch[]>([]);
  const previousTotalRef = useRef(0);
  const hasDownloadedRef = useRef(false);

  const columns = usePromoBatchColumns();
  const router = useRouter();
  const { country, language } = useCustomLocale();
  const pageSize = NO_OF_RECORDS_PAGE_SIZE;

  const SESSION_STORAGE_KEY = 'promoBatchCreated';
  const toast = useToast();
  const t = usePromoTranslation();
  const notes = [t.notesLineOne, t.notesLineTwo, t.notesLineThree];

  const {
    batchData: batchIdData,
    isLoading: isBatchIdLoading,
    isError: isBatchIdError,
  } = useBatchById(batchId ?? '', {
    enabled: Boolean(batchId),
  });

  const {
    data: promoBatchAsDownloadData,
    isLoading: promoBatchAsDownloadLoading,
    isError: promoBatchAsDownloadDataError,
  } = usePromoBatchAsDownload(batchIdData?.batchId ?? '', {
    enabled:
      !isBatchIdLoading &&
      !isBatchIdError &&
      Boolean(batchIdData?.password) &&
      Boolean(batchIdData?.batchId),
  });

  const { data, total, isLoading, refetch } = usePromoBatches({
    pageIndex,
    pageSize,
    sorting,
  });

  useEffect(() => {
    if (
      batchIdData?.s3Key &&
      !promoBatchAsDownloadLoading &&
      !promoBatchAsDownloadDataError &&
      !hasDownloadedRef.current
    ) {
      hasDownloadedRef.current = true;

      downloadFileFromUrl(batchIdData.s3Key, `${batchIdData.campaignName || 'promo-batch'}.zip`);
    }
  }, [batchIdData?.s3Key, promoBatchAsDownloadLoading, promoBatchAsDownloadDataError]);

  useEffect(() => {
    if (
      promoBatchAsDownloadData &&
      !promoBatchAsDownloadDataError &&
      !promoBatchAsDownloadLoading
    ) {
      setShowPasswordModal(true);
    }
  }, [promoBatchAsDownloadData]);

  useEffect(() => {
    refetch();
  }, [refetch]);

  useEffect(() => {
    if (!showPasswordModal) {
      refetch();
    }
  }, [showPasswordModal]);

  useEffect(() => {
    if (!isLoading && data.length > 0) {
      previousDataRef.current = data;
      previousTotalRef.current = total;
    }
  }, [isLoading, data, total]);

  const stableData = isLoading ? previousDataRef.current : data;
  const stableTotal = isLoading ? previousTotalRef.current : total;

  const handleToggle = () => {
    setLoadingTransition(true);
    router.replace(`/${country}/${language}/unique-promotions/create`)?.finally(() => {
      setTimeout(() => setLoadingTransition(false), 150);
    });
  };

  const table = useReactTable({
    data: stableData,
    columns,
    state: {
      sorting,
      pagination: { pageIndex, pageSize },
      columnPinning: {
        right: ['download'],
      },
    },
    enableColumnPinning: true,
    pageCount: Math.ceil(total / pageSize),
    manualPagination: true,
    manualSorting: true,
    enableSorting: true,
    getSortedRowModel: undefined,
    onSortingChange: setSorting,
    onPaginationChange: (updater) =>
      setPageIndex((old) => getNextPageIndex(old, pageSize, updater)),
    getCoreRowModel: getCoreRowModel(),
  });

  const handlePageChange = (page: number) => setPageIndex(page - 1);

  useEffect(() => {
    if (!router.isReady) return;

    const wasCreated = sessionStorage.getItem(SESSION_STORAGE_KEY);
    if (!wasCreated) return;

    if (isLoading || data.length === 0) return;

    sessionStorage.removeItem(SESSION_STORAGE_KEY);

    const latestBatch = data[0];
    const status = latestBatch?.status;

    const STATUS_CONFIG: Record<string, { toastStatus: 'success' | 'warning'; message: string }> = {
      Running: { toastStatus: 'warning', message: t.batchProcessingMessage },
      Pending: { toastStatus: 'warning', message: t.batchProcessingMessage },
    };

    const config = STATUS_CONFIG[status] ?? {
      toastStatus: 'success',
      message: t.batchSuccessMessage,
    };

    toast({
      description: config.message,
      status: config.toastStatus,
      duration: 3000,
      isClosable: true,
      position: 'top',
    });
  }, [router.isReady, isLoading, data, toast, t]);

  return (
    <PromoBatchesTableContext.Provider
      value={{
        showModal: showPasswordModal,
        setShowModal: setShowPasswordModal,
        setBatchId,
        batchIdData,
      }}
    >
      <Box as={Container} sx={styles.containerWrapper} data-testid={`${baseDataTestId}-container`}>
        <Box
          sx={{
            ...styles.container,
            ...(isLoading || isBatchIdLoading || promoBatchAsDownloadLoading
              ? {
                  pointerEvents: 'none',
                  opacity: 0.6,
                  transition: 'filter 0.2s ease, opacity 0.2s ease',
                  position: 'relative',
                }
              : {}),
          }}
        >
          {(isLoading || loadingTransition || isBatchIdLoading || promoBatchAsDownloadLoading) && (
            <Box
              position="absolute"
              inset={0}
              display="flex"
              alignItems="center"
              justifyContent="center"
              zIndex={10}
            >
              <LoadingSpinner />
            </Box>
          )}
          <HeaderSection
            loadingTransition={loadingTransition}
            handleToggle={handleToggle}
            setSorting={setSorting}
            sorting={sorting}
            data-testid={`${baseDataTestId}-header-section`}
          />
          <Box sx={styles.tableMinHeight}>
            <TableSection
              columns={columns}
              table={table}
              sorting={sorting}
              setSorting={setSorting}
              data={stableData}
              loading={isLoading}
              data-testid={`${baseDataTestId}-table-section`}
            />
          </Box>
          <PaginationSection
            totalRows={stableTotal}
            pageSize={pageSize}
            pageIndex={pageIndex}
            handlePageChange={handlePageChange}
            data-testid={`${baseDataTestId}-pagination-section`}
          />
        </Box>

        <PromoNotes isBackground={false} notes={notes} />
        <PasswordModal />
      </Box>
    </PromoBatchesTableContext.Provider>
  );
};

export default PromoBatchesTable;
