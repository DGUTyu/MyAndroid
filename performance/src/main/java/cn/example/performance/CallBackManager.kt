package cn.example.performance

object CallBackManager {
    private val sCallBacks = mutableListOf<CallBack>()

    fun addCallBack(callBack: CallBack) {
        sCallBacks.add(callBack)
    }

    fun removeCallBack(callBack: CallBack) {
        sCallBacks.remove(callBack)
    }
}