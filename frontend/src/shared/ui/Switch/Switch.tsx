import React from 'react';
import { Switch as MuiSwitch, SwitchProps as MuiSwitchProps, FormControlLabel, FormControl, FormHelperText } from '@mui/material';
import { Control, Controller, FieldValues, Path } from 'react-hook-form';

export interface SwitchProps extends MuiSwitchProps {
    label?: string;
    helperText?: string;
    error?: boolean;
}

const SwitchBase: React.FC<SwitchProps> = ({ label, helperText, error, ...props }) => {
    return (
        <FormControl error={error}>
            <FormControlLabel
                control={<MuiSwitch {...props} />}
                label={label || ''}
            />
            {helperText && <FormHelperText>{helperText}</FormHelperText>}
        </FormControl>
    );
};

export type SwitchFormProps<T extends FieldValues> = Omit<SwitchProps, 'name' | 'checked' | 'defaultChecked'> & {
    name: Path<T>;
    control: Control<T>;
};

const SwitchForm = <T extends FieldValues>({ name, control, ...props }: SwitchFormProps<T>) => {
    return (
        <Controller
            name={name}
            control={control}
            render={({ field: { value, onChange, onBlur, ref }, fieldState: { error } }) => (
                <SwitchBase
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


export const Switch = Object.assign(SwitchBase, { Form: SwitchForm });
