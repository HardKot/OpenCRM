import { Visibility, VisibilityOff, LightMode, DarkMode, AutoMode, BrightnessMedium, Add } from "@mui/icons-material";
import { FC } from "react";


export const Icons: Record<string, FC<{ size?: number; color?: IconColor }>> = {
    Visibility: Visibility,
    VisibilityOff: VisibilityOff,
    LightMode: LightMode,    
    DarkMode: DarkMode,
    AutoMode: AutoMode,
    BrightnessMedium: BrightnessMedium,
    Add: Add
}

type IconColor = 'inherit' | 'primary' | 'secondary' | 'action' | 'error' | 'disabled';

type IconName = keyof typeof Icons;

interface IconProps {
    size?: number;
    color?: IconColor;
    name: IconName;
}

const Icon = ({ size, color = 'inherit', name }: IconProps) => {
    const Components = Icons[name];
    if (!Components) {
        console.warn(`Icon with name "${name}" does not exist.`);
        return null;
    }
    return <Components size={size} color={color} />;
}

export { Icon };
export type { IconProps, IconName };