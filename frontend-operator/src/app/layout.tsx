import type { Metadata } from "next";
import { Space_Grotesk, Inter, IBM_Plex_Mono, Press_Start_2P } from "next/font/google";
import { AppProviders } from "@/components/shared/AppProviders";
import "./globals.css";

// Headline font: Space Grotesk -- geometric/blocky enough to rhyme with the
// cube motif, but still reads as "modern corporate" per the brief (unlike
// a full pixel font, which is illegible at headline length).
const display = Space_Grotesk({
  subsets: ["latin"],
  variable: "--font-display",
  weight: ["500", "600", "700"],
});

const sans = Inter({
  subsets: ["latin"],
  variable: "--font-sans",
});

const mono = IBM_Plex_Mono({
  subsets: ["latin"],
  variable: "--font-mono",
  weight: ["400", "500"],
});

// True pixel font -- used deliberately sparingly (logo mark, achievement
// toasts, tiny eyebrow labels) so the "Minecraft" signal is unmistakable
// without sacrificing readability anywhere text-heavy.
const pixel = Press_Start_2P({
  subsets: ["latin"],
  variable: "--font-pixel",
  weight: "400",
});

export const metadata: Metadata = {
  title: "Project Ghost Load",
  description: "Find the capacity your data center is already paying for.",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" className={`${display.variable} ${sans.variable} ${mono.variable} ${pixel.variable}`}>
      <body className="font-sans">
        <AppProviders>{children}</AppProviders>
      </body>
    </html>
  );
}
