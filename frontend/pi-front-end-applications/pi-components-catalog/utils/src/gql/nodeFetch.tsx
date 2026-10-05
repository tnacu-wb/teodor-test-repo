import { RequestInfo, RequestInit } from 'undici';

const getFetchWithAgent = async (url: RequestInfo, options: RequestInit) => {
  return globalThis.fetch(url as any, options as any);
};

export const getNodeFetch = () => {
  return getFetchWithAgent;
};
