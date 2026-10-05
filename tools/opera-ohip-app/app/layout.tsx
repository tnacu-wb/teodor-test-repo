import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'Digital Opera Test Data Update',
  description: 'Update Rate Plans, Clear Restrictions, and Set Sell Limits in Opera Cloud',
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
