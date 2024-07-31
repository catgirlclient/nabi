import {createRoot} from "react-dom/client";
import React, {StrictMode} from "react";
import {Index} from "./pages";

const container = document.getElementById("root");
const root = createRoot(container!);

root.render (
    <StrictMode>
        <Index />
    </StrictMode>
)
