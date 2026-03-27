type IMessage = { message: string }

const Utils = Object.freeze({
    isObject: (value: any): value is object => {
        return value !== null && typeof value === 'object' && !Array.isArray(value);
    },
    isString: (value: any): value is string => {
        return typeof value === 'string';
    },
    isIMessage: (value: any): value is IMessage => {
        return Utils.isObject(value) && 'message' in value && typeof value.message === 'string';
    },
    isIError: (value: any): value is { error: string } => {
        return Utils.isObject(value) && 'error' in value && typeof value.error === 'string';
    }
});

export { Utils }