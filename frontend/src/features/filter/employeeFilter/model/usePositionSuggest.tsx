import { useGetSuggestPositions } from "#shared/api";
import { useRef, useState } from "react";

const usePositionSuggest = () => {
  const [position, setPosition] = useState("");
  const [awaitLoading, setAwaitLoading] = useState(false);
  const timerId = useRef<number>();

  const {
    data = { items: [] },
    isLoading,
    isFetching,
  } = useGetSuggestPositions(position, {
    skip: !position,
  });

  const handleInputChange = (value: string) => {
    setAwaitLoading(true);

    if (timerId.current) {
      clearTimeout(timerId.current);
    }

    timerId.current = setTimeout(() => {
      setPosition(value);
      setAwaitLoading(false);
    }, 500);
  };

  return {
    positions: data.items ?? [],
    isLoading: awaitLoading || isFetching || isLoading,
    handleInputChange,
  };
};

export { usePositionSuggest };
