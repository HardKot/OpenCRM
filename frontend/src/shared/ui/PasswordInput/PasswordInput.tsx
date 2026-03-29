import { useState } from "react";
import { TextInput, TextInputProps } from "../TextInput";
import { Button } from "../Button";
import { Control, Controller, FieldValues, Path } from "react-hook-form";

interface PasswordInputProps extends Omit<TextInputProps, 'type'> {}

export type PasswordInputFormProps<T extends FieldValues = FieldValues> = Omit<PasswordInputProps, 'name'> & {
    name: Path<T>;
    control: Control<T>;
};

const PasswordInput = (props: PasswordInputProps) => {
    const [isShowPassword, setIsShowPassword] = useState(false);

    return (
        <TextInput 
            {...props} 
            type={isShowPassword ? "text" : "password"} 
            right={(
                <Button.Icon 
                    icon={isShowPassword ? "VisibilityOff" : "Visibility"}
                    onClick={() => setIsShowPassword(!isShowPassword)}
                    size="small"
                />
                )}
            />
    )
}

const PasswordInputForm = <T extends FieldValues = FieldValues>({ name, control, ...textFieldProps }: PasswordInputFormProps<T>) => (
    <Controller
        name={name}
        control={control}
        render={({ field: { value, onChange, onBlur, ref }, fieldState: { error } }) => {
            const shouldShowError = Boolean(error) && Boolean(String(value ?? '').trim());
            
            return (
                <PasswordInput
                    {...textFieldProps}
                    name={name}
                    value={value ?? ''}
                    onChange={onChange}
                    onBlur={onBlur}
                    inputRef={ref}
                    error={shouldShowError}
                    helperText={shouldShowError ? error?.message : textFieldProps.helperText}
                />
            );
        }}
    />
);

PasswordInput.Form = PasswordInputForm;

export { PasswordInput }
