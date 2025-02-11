import {Layout} from "../components/layout";

export function NotFound() {
    return (
        <Layout title="Nabi - 404" description="You're not supposed to be here!">
            <div className="flex justify-center items-center text-center flex-col">
                <h1 className="font-bold text-5xl mb-5"></h1>
                <p className="text-2xl">
                    The page you've clicked on seems not to be here. You probably shouldn't be here.
                </p>
            </div>
        </Layout>
    )
}