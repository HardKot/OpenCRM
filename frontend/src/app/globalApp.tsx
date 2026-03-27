import { i18n } from "./i18n/i18n";
import { store } from "./store";

// @ts-expect-error
const _global = (typeof window !== 'undefined' ? window : global) as any;

if (!_global.app) _global.app = {};

const app = _global.app as Window['app'];

if (!app.i18n) app.i18n = i18n;
if (!app.store) app.store = store;

