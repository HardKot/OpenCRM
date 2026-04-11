import { Utils } from "./Utils";

const Adapter = Object.freeze({
  getErrorMessage: (error: any, defaultText: string) => {
    if (Utils.isIError(error)) return error.error;
    if (Utils.isIMessage(error)) return error.message;
    if (Utils.isString(error)) return error;
    return defaultText;
  },
});

export { Adapter };
