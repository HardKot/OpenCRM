import { PropsWithChildren, ReactElement } from "react";
import {
  AccordionDetails,
  AccordionSummary,
  Accordion as MuiAccordion,
  Typography,
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";

import { Icon, IconName } from "../Icon";

interface AccordionProps extends PropsWithChildren {
  title: string;
  icon?: IconName;
  left?: ReactElement;
}

const Accordion = (props: AccordionProps) => {
  return (
    <MuiAccordion
      elevation={0}
      sx={{
        boxShadow: "none",
        "&.Mui-expanded": {
          boxShadow: "none",
        },
      }}
    >
      <AccordionSummary
        disableRipple={false}
        focusRipple
        expandIcon={<ExpandMoreIcon sx={{ backgroundColor: "transparent" }} />}
        sx={{
          borderRadius: 2,
          border: "1px solid",
          borderColor: "divider",
          bgcolor: "background.paper",
          px: 2,
          minHeight: 44,
          transition: "background-color 0.2s ease",
          "&:hover": {
            bgcolor: "action.hover",
          },
          "&.Mui-focusVisible": {
            bgcolor: "action.hover",
          },
          "&.Mui-expanded": {
            minHeight: 44,
          },
          "& .MuiAccordionSummary-content": {
            my: 1,
            gap: 1,
            alignItems: "center",
          },
          "& .MuiAccordionSummary-content.Mui-expanded": {
            my: 1,
          },
          "& .MuiAccordionSummary-expandIconWrapper": {
            backgroundColor: "transparent",
          },
          "& .MuiAccordionSummary-expandIconWrapper.Mui-expanded": {
            backgroundColor: "transparent",
          },
          "& .MuiTouchRipple-ripple .MuiTouchRipple-child": {
            backgroundColor: "currentColor",
            opacity: 0.2,
          },
        }}
      >
        {props.left}
        {props.icon && <Icon name={props.icon} />}
        <Typography component="span">{props.title}</Typography>
      </AccordionSummary>
      <AccordionDetails>{props.children}</AccordionDetails>
    </MuiAccordion>
  );
};

export type { AccordionProps };
export { Accordion };
