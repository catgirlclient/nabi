import {useEffect, useState} from "react";
import {clearInterval} from "node:timers";

const useTypewriter = (text: string, speed: number) => {
    const [displayText, setDisplayText] = useState('');

    useEffect(() => {
        let i = 0;
        const typingInterval = setInterval(() => {
            if (i < text.length) {
                setDisplayText(prevText => prevText + text.charAt(i));
                i++;
            } else {
                clearInterval(typingInterval);
            }
            }, speed);

        return () => {
            clearInterval(typingInterval);
        };
        }, [text, speed]);
    return displayText;
}

// @ts-ignore
export const Typewriter = ({text, speed}) => {
    const displayText = useTypewriter(text, speed);

    return <p>{text}</p>
}