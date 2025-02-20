import React, {FC} from "react";
import Navbar from "./navbar/navbar";
import Footer from "./footer";
import '../styles/global.scss'

interface LayoutProps {
    title: string;
    description: string;
    children: React.ReactNode;
}

export const Layout: FC<LayoutProps> = ({ title, description, children}) => {
    let theme = localStorage.getItem("theme");

    return (
        <>
            <head>
                <title>{title}</title>
                <meta property="og:title" content={title}></meta>
                <meta property="og:type" content="website"></meta>
                <meta property="og:description" content={description}></meta>
            </head>
            <body>
                <Navbar />
                <main>{children}</main>
                <Footer />
            </body>
        </>
    )
}