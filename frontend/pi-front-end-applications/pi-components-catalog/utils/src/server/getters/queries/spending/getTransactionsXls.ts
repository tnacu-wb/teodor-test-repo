import { resolveAndDownloadBlob } from '../../../formatters';

export const getTransactionsXls = async (
  token: string,
  schemeCustomerId: number,
  tetheredUserGuid: string,
  scheme: string
) => {
  if (!token || !schemeCustomerId || !tetheredUserGuid || !scheme) {
    return null;
  }

  const searchParams = new URLSearchParams({
    scheme,
  });

  const url = `${
    process.env.NEXT_PUBLIC_REST_API
  }/v2/piba/account/transactions/download/${schemeCustomerId}/${tetheredUserGuid}?${searchParams.toString()}`;

  const response = await fetch(url, {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
      'WB-Authorization': `Bearer ${token}`,
      accept: 'application/vnd.ms-excel',
    },
    cache: 'no-cache',
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch transactions XLS`);
  }

  const contentDisposition = response.headers.get('content-disposition');
  const filename = contentDisposition
    ?.split('filename=')[1]
    ?.replace(/(^["'])|(["']$)/g, '')
    ?.trim();

  const blob = await response.blob();
  resolveAndDownloadBlob(blob, filename ?? 'transactions', '.xls');
};
