package live.shuuyu.nabi.chat

abstract class ChatCommand(val name: String, val description: String) {
    abstract fun execute(context: ChatCommandContext)
}