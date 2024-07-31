import React, {ReactNode, useCallback} from "react";

interface ButtonProps {
    children: ReactNode,
    className?: string,
    onClick?: (
        event: React.MouseEvent<HTMLAnchorElement | HTMLButtonElement, MouseEvent>
    ) => void,
    disabled?: boolean,
    href?: string,
}

export function Button(props: ButtonProps) {
    const { onClick } = props;
    const callback = useCallback((
        event: React.MouseEvent<HTMLAnchorElement | HTMLButtonElement, MouseEvent>
        ) => {

        },
        [onClick]
    );
}