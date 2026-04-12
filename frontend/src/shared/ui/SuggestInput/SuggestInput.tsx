import { Autocomplete as MuiAutocomplete, TextField } from "@mui/material";
import { Control, Controller, FieldValues, Path } from "react-hook-form";

interface SuggestInputProps {
  label?: string;
  options: string[];
  value: string | null;
  onSelect: (value: string | null) => void;
  onInputChange: (value: string) => void;
  loading?: boolean;
}

type SuggestInputFormBaseProps<T extends FieldValues = FieldValues> = {
  name: Path<T>;
  control: Control<T>;
} & Omit<SuggestInputProps, "value" | "onSelect">;

const SuggestInput = ({
  options,
  label,
  value,
  onSelect,
  onInputChange,
  loading,
}: SuggestInputProps) => {
  return (
    <MuiAutocomplete
      sx={{ width: "100%" }}
      value={value}
      options={options}
      loading={loading}
      onInputChange={(_, newInput) => {
        onInputChange(newInput);
      }}
      onChange={(_, newValue) => {
        onSelect(newValue);
      }}
      renderInput={(params) => (
        <TextField {...params} label={label} size="small" sx={{ height: 48 }} />
      )}
      getOptionLabel={(it) => it}
    />
  );
};

const SuggestInputForm = <T extends FieldValues = FieldValues>({
  name,
  control,
  onInputChange,
  options,
  label,
  loading,
}: SuggestInputFormBaseProps<T>) => {
  return (
    <Controller
      name={name}
      control={control}
      render={({ field: { value, onChange } }) => (
        <SuggestInput
          options={options}
          label={label}
          loading={loading}
          value={value}
          onSelect={onChange}
          onInputChange={onInputChange}
        />
      )}
    />
  );
};

SuggestInput.Form = SuggestInputForm;

export { SuggestInput };
