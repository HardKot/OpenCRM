import { ButtonBase, MenuItem, Menu as MuiMenu } from '@mui/material'
import PopupState, { bindTrigger, bindMenu } from 'material-ui-popup-state';
import React from 'react';

interface MenuProps {
    Component: React.ReactElement
    MenuItems?: { label: string, onClick: () => void }[];
}

const Menu = (
    { Component, MenuItems }: MenuProps
) => (
        <PopupState variant="popover" popupId="demo-popup-menu">
            {(popupState) => (
                <>
                    <ButtonBase
                        {...bindTrigger(popupState)}
                        sx={{
                            display: 'inline-flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            borderRadius: 1,
                            px: 2,
                            py: 1,
                            textAlign: 'inherit',
                        }}
                    >
                        {Component}
                    </ButtonBase>
                    <MuiMenu
                        {...bindMenu(popupState)}
                    >
                        {MenuItems?.map((item, index) => (
                            <MenuItem
                                key={index}
                                onClick={() => {
                                    item.onClick();
                                    popupState.close();
                                }}
                            >
                                {item.label}
                            </MenuItem>
                        ))}
                    </MuiMenu>
                </>
            )}
    </PopupState>
    )


export { Menu }