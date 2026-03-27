import { Visibility, VisibilityOff } from "@mui/icons-material";
import { FC } from "react";




const Icons: Record<string, FC<{ size?: number; color?: IconColor }>> = {
    Visibility: Visibility,
    VisibilityOff: VisibilityOff,
}

type IconColor = 'inherit' | 'primary' | 'secondary' | 'action' | 'error' | 'disabled';

type IconName = keyof typeof Icons;

interface IconProps {
    size?: number;
    color?: IconColor;
    name: IconName;
}

const Icon = ({ size, color, name }: IconProps) => {
    const Components = Icons[name];
    if (!Components) {
        console.warn(`Icon with name "${name}" does not exist.`);
        return null;
    }
    return <Components size={size} color={color} />;
}

export { Icon };
export type { IconProps, IconName };