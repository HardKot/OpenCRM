import React from 'react';
import { Checkbox as MuiCheckbox, CheckboxProps as MuiCheckboxProps, FormControlLabel, FormControl, FormHelperText } from '@mui/material';
import { Control, Controller, FieldValues, Path } from 'react-hook-form';

export interface CheckboxProps extends MuiCheckboxProps {
    label?: string;
    helperText?: string;
    error?: boolean;
}

const CheckboxBase: React.FC<CheckboxProps> = ({ label, helperText, error, ...props }) => {
    return (
        <FormControl error={error}>
            <FormControlLabel
                control={<MuiCheckbox {...props} />}
                label={label || ''}
            />
             {helperText && <FormHelperText>{helperText}</FormHelperText>}
        </FormControl>
    );
};

export type CheckboxFormProps<T extends FieldValues> = Omit<CheckboxProps, 'name' | 'checked' | 'defaultChecked'> & {
    name: Path<T>;
    control: Control<T>;
};

const CheckboxForm = <T extends FieldValues>({ name, control, ...props }: CheckboxFormProps<T>) => {
    return (
        <Controller
            name={name}
            control={control}
            render={({ field: { value, onChange, onBlur, ref }, fieldState: { error } }) => (
                <CheckboxBase
                    {...props}
                    name={name}
                    checked={!!value}
                    onChange={(e) => onChange(e.target.checked)}
                    onBlur={onBlur}
                    inputRef={ref}
                    error={!!error}
                    helperText={error ? error.message : props.helperText}
                />
            )}
        />
    );
};

export const Checkbox = Object.assign(CheckboxBase, { Form: CheckboxForm });
