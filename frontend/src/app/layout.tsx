import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import QueryProvider from "@/providers/QueryProvider";
import ThemeToggle from "@/components/ThemeToggle";
import SiteHeader from "@/components/SiteHeader";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Enterprise Hub - Next.js, Zustand & React Query",
  description: "A premium enterprise dashboard starter kit incorporating Zustand for global state management and TanStack Query for data fetching.",
};

function ThemeScript() {
  return (
    <script
      dangerouslySetInnerHTML={{
        __html: `(function(){try{var t=localStorage.getItem('ghostload.theme');if(t==='light'){document.documentElement.classList.add('light')}}catch(e){}})();`,
      }}
    />
  );
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <head>
        <ThemeScript />
      </head>
      <body
        className={`${geistSans.variable} ${geistMono.variable} antialiased min-h-screen bg-slate-950 selection:bg-indigo-500 selection:text-white transition-colors duration-500`}
      >
        <SiteHeader />
        <ThemeToggle />
        <QueryProvider>{children}</QueryProvider>
      </body>
    </html>
  );
}
