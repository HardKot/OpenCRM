import { useState } from "react";

interface UsePageProps {
  page?: number;
  size?: number;
  sortId?: string;
  sortDirection?: "asc" | "desc";
}

const usePage = (init?: UsePageProps) => {
  const [page, setPage] = useState(init?.page ?? 1);
  const [size, setSize] = useState(init?.size ?? 50);
  const [sort, setSort] = useState({
    columnId: init?.sortId ?? "id",
    direction: init?.sortDirection ?? "asc",
  });

  const onPageChange = (newPage: number) => {
    setPage(newPage);
  };

  const onSizeChange = (newSize: number) => {
    setSize(newSize);
    setPage(1);
  };

  const onSortChange = (columnId: string, direction: "asc" | "desc") => {
    setSort({ columnId, direction });
    setPage(1);
  };

  return {
    page,
    size,
    sort,
    onPageChange,
    onSizeChange,
    onSortChange,
  };
};

export { usePage };
