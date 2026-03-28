import { useHoldSession } from "#shared/api/authApi";
import { useEffect } from "react";

const useSessionHoldEffect = () => {
    const [trigger] = useHoldSession();

    useEffect(() => {
        const timer = setInterval(() => trigger(), 60000);
        return () => clearInterval(timer);
    }, [trigger]);
}

export { useSessionHoldEffect };