import React from "react";
import { InputAdornment, TextField, TextFieldProps } from "@mui/material";
import { Control, Controller, FieldValues, Path } from "react-hook-form";
import { IMaskInput } from "react-imask";

interface TextMaskCustomProps {
  onChange: (event: { target: { name: string; value: string } }) => void;
  name: string;
  mask: string;
}

const TextMaskCustom = React.forwardRef<HTMLElement, TextMaskCustomProps>(
  function TextMaskCustom(props, ref) {
    const { onChange, mask, ...other } = props;
    return (
      <IMaskInput
        {...other}
        mask={mask as any}
        inputRef={ref as any}
        onAccept={(value: any) =>
          onChange({ target: { name: props.name, value } })
        }
        overwrite
      />
    );
  },
);

export type TextInputProps = {
  right?: React.ReactNode;
  mask?: typeof Number | string;
} & TextFieldProps;

const TextInputBase: React.FC<TextInputProps> = (props) => {
  const { right, mask, ...textFieldProps } = props;
  return (
    <TextField
      variant="outlined"
      size="small"
      fullWidth
      {...textFieldProps}
      sx={{
        height: 48,
        ...textFieldProps.sx,
      }}
      InputProps={{
        ...textFieldProps.InputProps,
        ...(mask
          ? {
              inputComponent: TextMaskCustom as any,
              inputProps: {
                ...(textFieldProps.inputProps || {}),
                mask,
              },
            }
          : {}),
        endAdornment: right || (
          <InputAdornment position="end" sx={{ mr: -0.5 }}>
            {right}
          </InputAdornment>
        ),
      }}
    />
  );
};

type TextInputFormBaseProps<T extends FieldValues = FieldValues> = {
  name: Path<T>;
  control: Control<T>;
};

export type TextInputFormProps<T extends FieldValues = FieldValues> =
  TextInputFormBaseProps<T> & Omit<TextInputProps, "name">;

const TextInputForm = <T extends FieldValues = FieldValues>(
  props: TextInputFormProps<T>,
) => {
  const { name, control, ...textFieldProps } = props;

  return (
    <Controller
      name={name}
      control={control}
      render={({
        field: { value, onChange, onBlur, ref },
        fieldState: { error },
      }) => {
        // Don't show error if field is empty (required error is implicit)
        const shouldShowError =
          Boolean(error) && Boolean(String(value ?? "").trim());

        return (
          <TextInputBase
            {...(textFieldProps as TextFieldProps)}
            name={name}
            value={value ?? ""}
            onChange={onChange}
            onBlur={onBlur}
            inputRef={ref}
            error={shouldShowError}
            helperText={
              shouldShowError ? error?.message : textFieldProps.helperText
            }
          />
        );
      }}
    />
  );
};

export const TextInput = Object.assign(TextInputBase, { Form: TextInputForm });
