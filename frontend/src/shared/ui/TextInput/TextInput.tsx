import React from 'react';
import { TextField, TextFieldProps } from '@mui/material';
import { Control, Controller, FieldValues, Path } from 'react-hook-form';

export type TextInputProps = TextFieldProps;

const TextInputBase: React.FC<TextInputProps> = (props) => {
    return <TextField variant="outlined" size="small" fullWidth {...props} />;
};

export type TextInputFormProps<T extends FieldValues> = Omit<TextInputProps, 'name'> & {
    name: Path<T>;
    control: Control<T>;
};

const TextInputForm = <T extends FieldValues>({ name, control, ...props }: TextInputFormProps<T>) => {
    return (
        <Controller
            name={name}
            control={control}
            render={({ field: { value, onChange, onBlur, ref }, fieldState: { error } }) => (
                <TextInputBase
                    {...props}
                    name={name}
                    value={value ?? ''}
                    onChange={onChange}
                    onBlur={onBlur}
                    inputRef={ref}
                    error={!!error}
                    helperText={error ? error.message : props.helperText}
                />
            )}
        />
    );
};

export const TextInput = Object.assign(TextInputBase, { Form: TextInputForm });
