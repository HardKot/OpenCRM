import { Avatar as MuiAvatar } from "@mui/material";    

interface AvatarProps {
    src: string;
    alt: string;
    size?: number;
}

interface AvatarTextProps {
    text: string;
    size?: number;
}


const AvatarText = (props: AvatarTextProps) => {
    const { text, size = 44 } = props;
    return <MuiAvatar sx={{ height: size, width: size, fontSize: (size / 3) }} variant="rounded">{text}</MuiAvatar>
}

const Avatar = (props: AvatarProps) => {
    const { src, alt, size = 44 } = props;
    return <MuiAvatar src={src} alt={alt} sx={{ height: size, width: size }} variant="rounded" />
};

Avatar.Text = AvatarText;

export { Avatar }

