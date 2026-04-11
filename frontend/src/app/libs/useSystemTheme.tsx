import { useSyncExternalStore } from "react";

const useSystemTheme = () =>
  useSyncExternalStore(
    (listener) => {
      const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)");
      mediaQuery.addEventListener("change", listener);
      return () => mediaQuery.removeEventListener("change", listener);
    },
    () => {
      const isDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
      return isDark ? "dark" : "light";
    },
  );

export { useSystemTheme };
