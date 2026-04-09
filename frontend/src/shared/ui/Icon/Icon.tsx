import {
  Visibility,
  VisibilityOff,
  LightMode,
  DarkMode,
  AutoMode,
  BrightnessMedium,
  Add,
  Delete,
  Restore,
} from "@mui/icons-material";

export const Icons = {
  Visibility: Visibility,
  VisibilityOff: VisibilityOff,
  LightMode: LightMode,
  DarkMode: DarkMode,
  AutoMode: AutoMode,
  BrightnessMedium: BrightnessMedium,
  Add: Add,
  Delete: Delete,
  Restore: Restore,
};

type IconColor =
  | "inherit"
  | "primary"
  | "secondary"
  | "action"
  | "error"
  | "disabled";

type IconName = keyof typeof Icons;

interface IconProps {
  size?: number;
  color?: IconColor;
  name: IconName;
}

const Icon = ({ size, color = "inherit", name }: IconProps) => {
  const Components = Icons[name];
  if (!Components) {
    console.warn(`Icon with name "${name}" does not exist.`);
    return null;
  }
  // @ts-expect-error
  return <Components size={size} color={color} />;
};

export { Icon };
export type { IconProps, IconName };
