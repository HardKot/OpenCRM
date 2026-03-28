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
    const { text, size } = props;
    return <MuiAvatar sx={{ height: size, width: size }} variant="square">{text}</MuiAvatar>
}

const Avatar = (props: AvatarProps) => {
    const { src, alt, size } = props;
    return <MuiAvatar src={src} alt={alt} sx={{ height: size, width: size }} variant="square"/>
};

Avatar.Text = AvatarText;

export { Avatar }

