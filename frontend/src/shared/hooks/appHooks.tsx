import { I18n } from "i18n-js";
import { useEffect, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux"

export const useAppDispatch = useDispatch.withTypes<AppDispatch>()
export const useAppSelector = useSelector.withTypes<RootState>()



type I18nState = {
    t: I18n['t']
};

export const useI18n = (): I18nState => {
    const [i18n, setI18n] = useState<I18nState>(() => ({ t: window.app.i18n.t.bind(window.app.i18n) }));

    useEffect(() => {
        const unsubscribe = window.app.i18n.onChange(() => {
            setI18n({ t: window.app.i18n.t.bind(window.app.i18n) });
        });

        return () => {
            unsubscribe();
        };
    }, [])

    return i18n;
}