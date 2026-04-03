import { View } from "#shared/ui";

type NavigationMenuItems = "employees";

interface NavigationMenuProps {
  onGoTo: (to: NavigationMenuItems) => void;
}

const NavigationMenu = ({ onGoTo }: NavigationMenuProps) => <View>{}</View>;
