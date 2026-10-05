'use client';

import { InnBusinessUserPilotData } from '@whitbread-eos/api';
import { usePathname } from 'next/navigation';
import { useEffect } from 'react';
import { Userpilot } from 'userpilot';

declare global {
  interface Window {
    userpilotSettings: {
      token: string;
      connection: string;
    };
  }
}

Userpilot.initialize(process?.env?.NEXT_PUBLIC_USER_PILOT_APP_TOKEN || '');

type Props = {
  userPilotData: InnBusinessUserPilotData;
};

export default function UserPilot({ userPilotData }: Props) {
  const pathname = usePathname();

  const { employeeId, innBusinessPayRoles, role, companyId, companySector, numberOfEmployees } =
    userPilotData;
  useEffect(() => {
    window.userpilotSettings = {
      token: process?.env?.NEXT_PUBLIC_USER_PILOT_APP_TOKEN || '',
      connection: 'polling',
    };
    Userpilot.identify(employeeId, {
      role,
      innBusinessPayRoles: innBusinessPayRoles?.join(','),
      company: {
        id: companyId,
        companySector,
        numberOfEmployees,
      },
    });
  }, []);

  useEffect(() => {
    Userpilot.reload();
  }, [pathname]);

  return <div />;
}
