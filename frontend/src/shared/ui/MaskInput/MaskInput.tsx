import React from 'react';
import { IMaskInput } from 'react-imask';
import { TextField, TextFieldProps } from '@mui/material';
import { Control, Controller, FieldValues, Path } from 'react-hook-form';


interface CustomProps {
  onChange: (event: { target: { name: string; value: string } }) => void;
  name: string;
  mask: string | object;
}

const TextMaskCustom = React.forwardRef<HTMLElement, CustomProps>(
  function TextMaskCustom(props, ref) {
    const { onChange, mask, ...other } = props;
    return (
      <IMaskInput
        {...other}
        mask={mask as any}
        inputRef={ref as any}
        onAccept={(value: any) => onChange({ target: { name: props.name, value } })}
        overwrite
      />
    );
  },
);

export type MaskInputProps = TextFieldProps & {
    mask: string | object;
};

const MaskInputBase: React.FC<MaskInputProps> = ({ mask, InputProps, ...props }) => {
    return (
        <TextField
            {...props}
            InputProps={{
                ...InputProps,
                inputComponent: TextMaskCustom as any,
                inputProps: {
                    mask,
                    ...InputProps?.inputProps
                }
            }}
        />
    );
};

export type MaskInputFormProps<T extends FieldValues> = Omit<MaskInputProps, 'name'> & {
    name: Path<T>;
    control: Control<T>;
};

const MaskInputForm = <T extends FieldValues>({ name, control, ...props }: MaskInputFormProps<T>) => {
    return (
        <Controller
            name={name}
            control={control}
            render={({ field: { value, onChange, onBlur, ref }, fieldState: { error } }) => (
                 <MaskInputBase
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

export const MaskInput = Object.assign(MaskInputBase, { Form: MaskInputForm });
