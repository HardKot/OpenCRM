import { useState } from "react";

interface UsePageProps {
    page?: number;
    size?: number;
}

const usePage = (init?: UsePageProps) => {
    const [page, setPage] = useState(init?.page ?? 1);
    const [size, setSize] = useState(init?.size ?? 50);

    const onPageChange = (newPage: number) => {
        setPage(newPage);
    }

    const onSizeChange = (newSize: number) => {
        setSize(newSize);
        setPage(1);
    }

    return {
        page,
        size,
        onPageChange,
        onSizeChange,
    }

}

export { usePage };