"use client";

import { ChevronLeft, ChevronRight } from "lucide-react";
import { cn } from "@/lib/utils";

export function Pagination({
  page,
  pageCount,
  onPageChange,
  totalItems,
  pageSize,
}: {
  page: number;
  pageCount: number;
  onPageChange: (page: number) => void;
  totalItems: number;
  pageSize: number;
}) {
  if (pageCount <= 1) return null;

  const start = (page - 1) * pageSize + 1;
  const end = Math.min(page * pageSize, totalItems);

  return (
    <div className="flex items-center justify-between border-t border-graphite-100 px-5 py-3">
      <p className="text-xs text-graphite-400">
        Mostrando <span className="font-medium text-graphite-600">{start}–{end}</span> de{" "}
        <span className="font-medium text-graphite-600">{totalItems}</span>
      </p>
      <div className="flex items-center gap-1">
        <button
          onClick={() => onPageChange(Math.max(1, page - 1))}
          disabled={page === 1}
          className={cn(
            "flex h-7 w-7 items-center justify-center rounded-md border border-graphite-200 text-graphite-500",
            "hover:bg-graphite-50 disabled:cursor-not-allowed disabled:opacity-40"
          )}
        >
          <ChevronLeft className="h-3.5 w-3.5" />
        </button>
        <span className="px-2 text-xs font-medium text-graphite-600">
          {page} / {pageCount}
        </span>
        <button
          onClick={() => onPageChange(Math.min(pageCount, page + 1))}
          disabled={page === pageCount}
          className={cn(
            "flex h-7 w-7 items-center justify-center rounded-md border border-graphite-200 text-graphite-500",
            "hover:bg-graphite-50 disabled:cursor-not-allowed disabled:opacity-40"
          )}
        >
          <ChevronRight className="h-3.5 w-3.5" />
        </button>
      </div>
    </div>
  );
}
