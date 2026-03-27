import React from 'react';
import { TextField, TextFieldProps } from '@mui/material';
import { Control, Controller, FieldValues, Path } from 'react-hook-form';

export type TextInputProps = {
    right?: React.ReactNode;
} & TextFieldProps;

const TextInputBase: React.FC<TextInputProps> = (props) => {
    const { right, ...textFieldProps } = props;
    return (
        <TextField
            variant="outlined"
            size="small"
            fullWidth
            {...textFieldProps}
            InputProps={{
                ...textFieldProps.InputProps,
                endAdornment: right || textFieldProps.InputProps?.endAdornment,
            }}
        />
    );
};

type TextInputFormBaseProps<T extends FieldValues = FieldValues> = {
    name: Path<T>;
    control: Control<T>;
};

export type TextInputFormProps<T extends FieldValues = FieldValues> = 
    TextInputFormBaseProps<T> & 
    Omit<TextInputProps, 'name'>;

const TextInputForm = <T extends FieldValues = FieldValues>(
    props: TextInputFormProps<T>
) => {
    const { name, control, ...textFieldProps } = props;
    
    return (
        <Controller
            name={name}
            control={control}
            render={({ field: { value, onChange, onBlur, ref }, fieldState: { error } }) => (
                <TextInputBase
                    {...(textFieldProps as TextFieldProps)}
                    name={name}
                    value={value ?? ''}
                    onChange={onChange}
                    onBlur={onBlur}
                    inputRef={ref}
                    error={!!error}
                    helperText={error ? error.message : textFieldProps.helperText}
                />
            )}
        />
    );
};

export const TextInput = Object.assign(TextInputBase, { Form: TextInputForm });
