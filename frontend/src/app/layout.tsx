import type { Metadata } from "next";
import { Sora, JetBrains_Mono, Inter } from "next/font/google";
import "./globals.css";
import QueryProvider from "@/providers/QueryProvider";

const sora = Sora({
  variable: "--font-sora",
  subsets: ["latin"],
});

const jetbrainsMono = JetBrains_Mono({
  variable: "--font-jetbrains-mono",
  subsets: ["latin"],
});

const inter = Inter({
  variable: "--font-inter",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Ghost Load | Eficiencia para data centers",
  description:
    "Detecta capacidad inactiva, mide el desperdicio energético y descubre cómo aprovechar mejor tu infraestructura.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="es" className="light">
      <head>
        <link
          rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200&display=swap"
        />
      </head>
      <body
        className={`${sora.variable} ${jetbrainsMono.variable} ${inter.variable} antialiased min-h-screen bg-[#fbf9f8] text-[#1b1c1c] selection:bg-[#00fabe] selection:text-[#002116]`}
      >
        <QueryProvider>{children}</QueryProvider>
      </body>
    </html>
  );
}
