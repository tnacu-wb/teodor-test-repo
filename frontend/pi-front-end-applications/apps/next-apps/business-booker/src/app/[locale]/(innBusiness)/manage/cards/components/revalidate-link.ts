'use server';

import { revalidatePath } from 'next/cache';

export const revalidateCacheOnLink = async (path: string) => {
  revalidatePath(path);
};
