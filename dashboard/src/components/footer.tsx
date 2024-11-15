import {InlineIcon} from "@iconify/react";
import Github from '@iconify-icons/uil/github';
import Discord from '@iconify-icons/uil/discord';

export default function Footer()  {
    return (
        <footer className="sticky flex justify-center items-center mx-auto text-center flex-row bg-black rounded-lg m-4">
            <div className="w-full max-w-screen-2xl mx-auto p-4 flex justify-center items-center mb-24 gap-y-24">
                <div className="flex flex-row gap-3">
                    <a
                        href="https://github.com/catgirlclient/nabi"
                        target="_blank"
                        className="hover:text-indigo"
                        aria-label="github-icon"
                    ><InlineIcon icon={Github} mode="style" style={{ fontSize: "50px" }}/></a>
                    <a
                        href="https://discord.gg/42da8JWwKa"
                        target="_blank"
                        className="hover:text-indigo"
                        aria-label="discord-icon"
                    ><InlineIcon icon={Discord} mode="style" style={{ fontSize: "50px" }}/></a>
                </div>
                <div className="flex flex-row flex md:flex-row md:items-start md:-justify-between">
                    <p>
                        Nabi is not affiliated with Discord Inc.
                    </p>
                </div>
            </div>
        </footer>
    )
}