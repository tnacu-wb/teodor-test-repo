export type MemoIds = {
  reservationId: string;
  memoIds: string[];
};

export type Memo = {
  ids: MemoIds[];
  description: string;
  createdOn: string;
  createdBy: string;
  modifiedOn: string;
  modifiedBy: string;
  memoType: string;
};

export type MemosResponse = {
  memos: Memo[];
};
