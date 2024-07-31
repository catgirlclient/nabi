import React, {useCallback} from "react";

interface ButtonProps {
    onClick?: (
        event: React.MouseEvent<HTMLAnchorElement | HTMLButtonElement, MouseEvent>
    ) => void,
    disabled?: Boolean
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