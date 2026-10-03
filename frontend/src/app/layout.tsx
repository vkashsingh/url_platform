import type { Metadata } from 'next';
import { Inter } from 'next/font/google';
import 'bootstrap/dist/css/bootstrap.min.css';
import './globals.css';
import NavBar from '@/components/NavBar';

const inter = Inter({ subsets: ['latin'] });

export const metadata: Metadata = {
  title: 'URL Platform – URL Shortening Service',
  description:
    'A production-style URL shortening application built with Next.js and Spring Boot.',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <NavBar />
        <main className="container py-4">{children}</main>
        <footer className="text-center py-3 text-muted small">
          URL Platform &copy; {new Date().getFullYear()}
        </footer>
      </body>
    </html>
  );
}
